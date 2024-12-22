package io.metaloom.graph.core.storage.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
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
	public void store(long id, String label) throws IOException {
		// Calculate the offset
		long offset = id * NODE_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = raFile.getChannel();

		// Ensure the file is large enough
		ensureFileCapacity(fc, offset);

		// Set the values
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, NODE_LAYOUT.byteSize(), arena);
		NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeId")).set(memorySegment, 0, (long) id);
		NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);
		writeLabel(memorySegment, label);
	}

}
