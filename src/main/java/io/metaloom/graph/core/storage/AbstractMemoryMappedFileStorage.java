package io.metaloom.graph.core.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class AbstractMemoryMappedFileStorage implements AutoCloseable {

	protected Arena arena;

	protected RandomAccessFile raFile;

	protected File file;

	protected final MemoryLayout layout;

	protected final Set<Long> freeIds = new HashSet<>();

	protected final VarHandle labelHandle;

	public AbstractMemoryMappedFileStorage(File file, MemoryLayout layout) throws FileNotFoundException {
		this.arena = Arena.ofShared();
		this.file = file;
		this.layout = layout;
		this.raFile = new RandomAccessFile(file, "rw");

		this.labelHandle = layout.varHandle(
			MemoryLayout.PathElement.groupElement("label"),
			MemoryLayout.PathElement.sequenceElement());

		try {
			loadFreeIds(freeIds, this.raFile, layout);
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

	private void loadFreeIds(Set<Long> ids, RandomAccessFile file, MemoryLayout layout) throws IOException {
		if (file.length() == 0) {
			return;
		}
		FileChannel fc = file.getChannel();
		for (long offset = 0; offset < file.length(); offset += layout.byteSize()) {
			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, layout.byteSize(), arena);
			boolean free = (boolean) layout.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
			long id = offset / layout.byteSize();
			if (free) {
				ids.add(id);
			}
		}
	}

	public Set<Long> getFreeIds() {
		return freeIds;
	}

}
