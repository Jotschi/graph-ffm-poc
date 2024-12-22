package io.metaloom.graph.core.storage.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import io.metaloom.graph.core.storage.AbstractMemoryMappedFileStorage;
import io.metaloom.graph.core.storage.NodeStorage;

public class NodeStorageImpl extends AbstractMemoryMappedFileStorage implements NodeStorage {

	private static final GroupLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("nodeId"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("label")); // 32-byte label

	public NodeStorageImpl(File file) throws FileNotFoundException {
		super(file, NODE_LAYOUT);
	}

	@Override
	public void store(long id, long y) throws IOException {
		System.out.println(NODE_LAYOUT.byteSize());
		// Calculate the offset
		long offset = id * NODE_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = raFile.getChannel();

		// Ensure the file is large enough
		if (raFile.length() < offset + NODE_LAYOUT.byteSize()) {
			// Write zeros to extend the file
			byte[] zeros = new byte[(int) (offset + NODE_LAYOUT.byteSize() - raFile.length())];
			fc.position(raFile.length());
			fc.write(ByteBuffer.wrap(zeros));
		}

		System.out.println("Using offset: " + offset);
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, NODE_LAYOUT.byteSize(), arena);

		// Set the values
		VarHandle idHandle = NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeId"));
		idHandle.set(memorySegment, 0, (long) id);
	}

}
