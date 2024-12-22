package io.metaloom.graph.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class MemoryStorage implements AutoCloseable {

	private static final MemoryLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("nodeId"),
		ValueLayout.JAVA_LONG.withName("y"));

	private static final MemoryLayout REL_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("relId"),
		ValueLayout.JAVA_LONG.withName("nodeAId"),
		ValueLayout.JAVA_LONG.withName("nodeBId"));

	private Arena arena;

	private RandomAccessFile nodesFile;

	private RandomAccessFile relsFile;

	public MemoryStorage(File nodesFile, File relsFile) throws FileNotFoundException {
		this.arena = Arena.ofShared();
		this.nodesFile = new RandomAccessFile(nodesFile, "rw");
		this.relsFile = new RandomAccessFile(relsFile, "rw");
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

	public long storeRelationship(long relId, long nodeAId, long nodeBId) throws IOException {
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

		return new long[] { nodeAId, nodeBId };
	}
}
