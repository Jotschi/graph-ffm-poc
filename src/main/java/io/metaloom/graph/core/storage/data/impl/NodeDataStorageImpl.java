package io.metaloom.graph.core.storage.data.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.metaloom.graph.core.storage.data.AbstractGraphStorage;
import io.metaloom.graph.core.storage.data.NodeDataStorage;

public class NodeDataStorageImpl extends AbstractGraphStorage implements NodeDataStorage {

	private static final GroupLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("nodeId"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(MAX_LABEL_LEN, ValueLayout.JAVA_BYTE).withName("label"),
		MemoryLayout.sequenceLayout(MAX_PROP_IDS, ValueLayout.JAVA_LONG).withName("props"));

	public NodeDataStorageImpl(Path path) throws FileNotFoundException {
		super(path, NODE_LAYOUT);
	}

	@Override
	public void store(long id, String label, long propIds[]) throws IOException {
		// Calculate the offset
		long offset = id * NODE_LAYOUT.byteSize();

		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

			// Ensure the file is large enough
			ensureFileCapacity(fc, offset, layout);

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, NODE_LAYOUT.byteSize(), arena);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeId")).set(memorySegment, 0, (long) id);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
		}
	}

	@Override
	public NodeData load(long nodeId) throws IOException {
		// Calculate the offset
		long offset = nodeId * NODE_LAYOUT.byteSize();

		// Map the memory segment
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {

			// Check if the file is large enough to even contain the node.
			if (fc.size() < offset + NODE_LAYOUT.byteSize()) {
				throw new IOException("Relationship not found");
			}

			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, NODE_LAYOUT.byteSize(), arena);

			// Get the values
			boolean free = (boolean) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
			String label = readLabel(memorySegment);
			long[] propIds = readPropIds(memorySegment);

			// System.out.println("Free: " + free);
			return new NodeData(label, propIds);
		}
	}

}
