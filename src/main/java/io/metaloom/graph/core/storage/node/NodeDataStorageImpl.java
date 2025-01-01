package io.metaloom.graph.core.storage.node;

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
import io.metaloom.graph.core.uuid.GraphUUID;

public class NodeDataStorageImpl extends AbstractGraphStorage implements NodeDataStorage {

	private static final String MAGIC_BYTE = "node";

	private static final String RANDOM_UUID_PART_KEY = "random_uuid_part";

	private static final String NODE_REL_START_OFFSET_KEY = "node_rel_start_offset";

	protected static final GroupLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName(RANDOM_UUID_PART_KEY),
		ValueLayout.JAVA_LONG.withName(NODE_REL_START_OFFSET_KEY),
		ValueLayout.JAVA_BOOLEAN.withName(FREE_KEY),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(MAX_LABEL_LEN, ValueLayout.JAVA_BYTE).withName(LABEL_KEY),
		MemoryLayout.sequenceLayout(MAX_PROP_IDS, ValueLayout.JAVA_LONG).withName(PROPS_KEY));

	public NodeDataStorageImpl(Path path) throws IOException {
		super(path, NODE_LAYOUT, MAGIC_BYTE);
	}

	@Override
	public GraphUUID uuid() {
		return GraphUUID.uuid(nextOffset());
	}

	@Override
	public void store(GraphUUID uuid, String label, long propIds[]) throws IOException {
		long offset = uuid.offset();
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

			// Ensure the file is large enough
			ensureFileCapacity(fc, offset, layout);

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, NODE_LAYOUT.byteSize(), arena);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(RANDOM_UUID_PART_KEY)).set(memorySegment, 0, (long) uuid.randomValue());
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
		} finally {
			offsetProvider.set(offset + NODE_LAYOUT.byteSize());
		}
	}

	@Override
	public NodeData load(GraphUUID uuid) throws IOException {
		long offset = uuid.offset();
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {

			// Check if the file is large enough to even contain the node.
			if (fc.size() < offset + NODE_LAYOUT.byteSize()) {
				throw new IOException("Relationship not found");
			}

			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, NODE_LAYOUT.byteSize(), arena);

			// Get the values
			boolean free = (boolean) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).get(memorySegment, 0);
			String label = readLabel(memorySegment);
			long randomValue = (long) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(RANDOM_UUID_PART_KEY)).get(memorySegment, 0);
			long[] propIds = readPropIds(memorySegment);

			GraphUUID loadedUuid = GraphUUID.from(offset, randomValue);
			return new NodeData(loadedUuid, label, propIds);
		}
	}

}
