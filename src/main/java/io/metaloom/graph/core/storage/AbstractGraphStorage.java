package io.metaloom.graph.core.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

public class AbstractGraphStorage extends AbstractMMapFileStorage implements Storage {

	protected final MemoryLayout layout;

	protected final Deque<Long> freeIds = new ArrayDeque<>();

	protected final VarHandle labelHandle;

	protected final AtomicLong idProvider;

	public AbstractGraphStorage(Path path, MemoryLayout layout) throws FileNotFoundException {
		super(path);
		this.layout = layout;

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

		try (FileChannel fc = raFile.getChannel()) {
			long lastId = 0L;
			for (long offset = 0; offset < raFile.length(); offset += layout.byteSize()) {
				MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, layout.byteSize(), arena);
				boolean free = (boolean) layout.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
				lastId = offset / layout.byteSize();
				if (free) {
					freeIds.add(lastId);
				}
			}
			return lastId;
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
