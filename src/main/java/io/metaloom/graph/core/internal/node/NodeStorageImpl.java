package io.metaloom.graph.core.internal.node;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.metaloom.graph.core.internal.AbstractGraphStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public class NodeStorageImpl extends AbstractGraphStorage<NodeInternal> implements NodeStorage {

	private static Logger logger = LoggerFactory.getLogger(NodeStorageImpl.class);

	private static final String MAGIC_BYTE = "node";

	private static final String RANDOM_UUID_PART_KEY = "random_uuid_part";

	private static final String NODE_REL_START_OFFSET_KEY = "node_rel_start_offset";

	protected static final GroupLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName(RANDOM_UUID_PART_KEY),
		ValueLayout.JAVA_LONG.withName(NODE_REL_START_OFFSET_KEY),
		ValueLayout.JAVA_BOOLEAN.withName(FREE_KEY),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(MAX_LABEL_LEN, ValueLayout.JAVA_BYTE).withName(LABEL_KEY),
		MemoryLayout.sequenceLayout(MAX_PROP_IDS, ValueLayout.JAVA_LONG).withName(PROPS_KEY));

	public NodeStorageImpl(Path path) throws IOException {
		super(path, LAYOUT, MAGIC_BYTE);
	}

	@Override
	public void update(GraphUUID uuid, String label, long propIds[]) throws IOException {
		long offset = uuid.offset();
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, LAYOUT.byteSize(), Arena.ofAuto());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(RANDOM_UUID_PART_KEY)).set(memorySegment, 0, (long) uuid.randomValue());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
		}
	}

	@Override
	public NodeInternal create(String label, long[] propIds) throws IOException {
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			long offset = offsetProvider().next(fc);
			GraphUUID uuid = GraphUUID.uuid(offset);

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, LAYOUT.byteSize(), Arena.ofAuto());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(RANDOM_UUID_PART_KEY)).set(memorySegment, 0, (long) uuid.randomValue());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
			return new NodeInternal(uuid, label, propIds);
		}
	}

	@Override
	public NodeInternal read(GraphUUID uuid) throws IOException {
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
			long offset = uuid.offset();

			// Check if the file is large enough to even contain the node.
			if (fc.size() < offset + LAYOUT.byteSize()) {
				logger.debug("Element offset {} with uuid {} is outside of storage file size", offset, uuid);
				return null;
			}

			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, LAYOUT.byteSize(), Arena.ofAuto());

			// Get the values
			boolean free = (boolean) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).get(memorySegment, 0);
			if (free) {
				logger.debug("Found memory segment was maked as free. Thus element data shall not be returned", offset, uuid);
				return null;
			}
			String label = readLabel(memorySegment);
			long randomValue = (long) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(RANDOM_UUID_PART_KEY)).get(memorySegment, 0);
			long[] propIds = readPropIds(memorySegment);

			GraphUUID loadedUuid = GraphUUID.from(offset, randomValue);
			return new NodeInternal(loadedUuid, label, propIds);
		}
	}

}
