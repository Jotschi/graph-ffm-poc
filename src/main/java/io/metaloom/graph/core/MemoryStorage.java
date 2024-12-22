package io.metaloom.graph.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class MemoryStorage implements AutoCloseable {

	private static final MemoryLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("nodeId"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		ValueLayout.JAVA_LONG.withName("y"));

	private static final MemoryLayout REL_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("relId"),
		ValueLayout.JAVA_LONG.withName("nodeAId"),
		ValueLayout.JAVA_LONG.withName("nodeBId"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("label")); // 32-byte label

	private Arena arena;

	private RandomAccessFile nodesFile;

	private RandomAccessFile relsFile;

	private Set<Long> freeRelIds = new HashSet<>();
	private Set<Long> freeNodeIds = new HashSet<>();

	public MemoryStorage(File nodesFile, File relsFile) throws FileNotFoundException {
		this.arena = Arena.ofShared();
		this.nodesFile = new RandomAccessFile(nodesFile, "rw");
		this.relsFile = new RandomAccessFile(relsFile, "rw");
		try {
			loadFreeIds(freeRelIds, this.relsFile, REL_LAYOUT);
			loadFreeIds(freeNodeIds, this.nodesFile, NODE_LAYOUT);
		} catch (Exception e) {
			throw new RuntimeException("Error while loading free ids", e);
		}

	}

	private void loadFreeIds(Set<Long> ids, RandomAccessFile file, MemoryLayout layout) throws IOException {
		if (file.length() == 0) {
			return;
		}
		FileChannel fc = file.getChannel();
		for (long offset = 0; offset < file.length(); offset += layout.byteSize()) {
			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, REL_LAYOUT.byteSize(), arena);
			boolean free = (boolean) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
			long id = offset / layout.byteSize();
			if (free) {
				ids.add(id);
			}
		}
	}

	public void close() {
		if (arena != null) {
			arena.close();
		}
		try {
			nodesFile.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		try {
			relsFile.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public long storeNode(long id, long y) throws IOException {
		System.out.println(NODE_LAYOUT.byteSize());
		// Calculate the offset
		long offset = id * NODE_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = nodesFile.getChannel();

		// Ensure the file is large enough
		if (nodesFile.length() < offset + NODE_LAYOUT.byteSize()) {
			// Write zeros to extend the file
			byte[] zeros = new byte[(int) (offset + NODE_LAYOUT.byteSize() - nodesFile.length())];
			fc.position(nodesFile.length());
			fc.write(ByteBuffer.wrap(zeros));
		}

		System.out.println("Using offset: " + offset);
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, NODE_LAYOUT.byteSize(), arena);

		// Set the values
		VarHandle idHandle = NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeId"));
		idHandle.set(memorySegment, 0, (long) id);
		return memorySegment.address();
	}

	public void deleteRelationship(long relId) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();
		if (offset > relsFile.length()) {
			return;
		}

		// Map the memory segment
		FileChannel fc = relsFile.getChannel();
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, REL_LAYOUT.byteSize(), arena);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, true);

	}

	public long storeRelationship(long relId, long nodeAId, long nodeBId, String label) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = relsFile.getChannel();

		// Ensure the file is large enough
		if (relsFile.length() < offset + REL_LAYOUT.byteSize()) {
			// Write zeros to extend the file
			byte[] zeros = new byte[(int) (offset + REL_LAYOUT.byteSize() - relsFile.length())];
			fc.position(relsFile.length());
			fc.write(ByteBuffer.wrap(zeros));
			// System.out.println("Adding: " + zeros.length + " bytes to the file");
		}

		System.out.println("Using offset: " + offset);
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, REL_LAYOUT.byteSize(), arena);

		// Set the values
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("relId")).set(memorySegment, 0, (long) relId);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeAId")).set(memorySegment, 0, (long) nodeAId);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeBId")).set(memorySegment, 0, (long) nodeBId);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);

		byte[] labelBytes = label.getBytes(StandardCharsets.UTF_8);
		
		PathElement path = MemoryLayout.PathElement.groupElement("label");
		VarHandle labelHandle = REL_LAYOUT.varHandle(path);
		for (int i = 0; i < labelBytes.length && i < 32; i++) {
			labelHandle.set(memorySegment, 0, i, labelBytes[i]);
		}
		// Terminate with null
		if (labelBytes.length < 32) {
			for (int i = labelBytes.length; i < 32; i++) {
				labelHandle.set(memorySegment, 0, i, (byte) 0); // null-terminate the string
			}
		}

		return memorySegment.address();
	}

	public long[] loadRelationship(long relId) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = relsFile.getChannel();

		// Check if the file is large enough
		if (relsFile.length() < offset + REL_LAYOUT.byteSize()) {
			throw new IOException("Relationship not found");
		}

		MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, REL_LAYOUT.byteSize(), arena);

		// Get the values
		long nodeAId = (long) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeAId")).get(memorySegment, 0);
		long nodeBId = (long) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeBId")).get(memorySegment, 0);
		boolean free = (boolean) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);

		System.out.println("Free: " + free);
		return new long[] { nodeAId, nodeBId };
	}

	public Set<Long> getFreeNodeIds() {
		return freeNodeIds;
	}

	public Set<Long> getFreeRelIds() {
		return freeRelIds;
	}
}
