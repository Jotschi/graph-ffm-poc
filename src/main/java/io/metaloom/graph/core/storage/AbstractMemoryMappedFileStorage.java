package io.metaloom.graph.core.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

public class AbstractMemoryMappedFileStorage implements Storage {

	protected Arena arena;

	protected RandomAccessFile raFile;

	protected File file;

	protected final MemoryLayout layout;

	protected final Deque<Long> freeIds = new ArrayDeque<>();

	protected final VarHandle labelHandle;

	protected final AtomicLong idProvider;

	public AbstractMemoryMappedFileStorage(File file, MemoryLayout layout) throws FileNotFoundException {
		this.arena = Arena.ofShared();
		this.file = file;
		this.layout = layout;
		this.raFile = new RandomAccessFile(file, "rw");

		this.labelHandle = layout.varHandle(
			MemoryLayout.PathElement.groupElement("label"),
			MemoryLayout.PathElement.sequenceElement());

		try {
			long lastId = loadFreeIds(layout);
			this.idProvider = new AtomicLong(lastId++);
		} catch (Exception e) {
			throw new RuntimeException("Error while loading free ids", e);
		}
	}

	@Override
	public void close() throws Exception {
		if (arena != null) {
			arena.close();
		}
		raFile.close();
	}

	public void writeLabel(MemorySegment segment, String label) {
		byte[] bytes = label.getBytes(StandardCharsets.UTF_8);
		if (bytes.length > 32) {
			throw new IllegalArgumentException("Label exceeds 32 bytes when encoded.");
		}

		System.out.println("Writing: " + label);

		// Write the label bytes into the sequence
		for (int i = 0; i < bytes.length; i++) {
			labelHandle.set(segment, 0, (long) i, bytes[i]);
		}

		// Pad the remaining space with 0s
		for (int i = bytes.length; i < 32; i++) {
			labelHandle.set(segment, 0, (long) i, (byte) 0);
		}
	}

	public void readLabel(MemorySegment segment) {
		byte[] labelBytes = new byte[32];
		for (int i = 0; i < 32; i++) {
			labelBytes[i] = (byte) labelHandle.get(segment, 0, i);
			if (labelBytes[i] == 0) {
				break;
			}
		}
		String retrievedLabel = new String(labelBytes, StandardCharsets.UTF_8).trim();
		System.out.println("Retrieved Label: " + retrievedLabel);
	}

	@Override
	public void delete(long relId) throws IOException {
		// Calculate the offset
		long offset = relId * layout.byteSize();
		if (offset > raFile.length()) {
			return;
		}

		// Map the memory segment
		FileChannel fc = raFile.getChannel();
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, layout.byteSize(), arena);
		layout.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, true);
		freeIds.add(relId);
	}

	private long loadFreeIds(MemoryLayout layout) throws IOException {
		if (raFile.length() == 0) {
			return 0L;
		}
		FileChannel fc = raFile.getChannel();
		long lastId = 0L;
		for (long offset = 0; offset < file.length(); offset += layout.byteSize()) {
			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, layout.byteSize(), arena);
			boolean free = (boolean) layout.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
			lastId = offset / layout.byteSize();
			if (free) {
				freeIds.add(lastId);
			}
		}
		return lastId;
	}

	protected void ensureFileCapacity(FileChannel fc, long offset) throws IOException {
		if (raFile.length() < offset + layout.byteSize()) {
			// Write zeros to extend the file
			byte[] zeros = new byte[(int) (offset + layout.byteSize() - raFile.length())];
			fc.position(raFile.length());
			fc.write(ByteBuffer.wrap(zeros));
			// System.out.println("Adding: " + zeros.length + " bytes to the file");
		}
	}

	@Override
	public long id() {
		if (freeIds.isEmpty()) {
			return idProvider.incrementAndGet();
		} else {
			return freeIds.pop();
		}
	}

	@Override
	public Deque<Long> getFreeIds() {
		return freeIds;
	}

}
