package io.metaloom.graph.core.storage.rel;

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

public class RelationshipStorageImpl extends AbstractGraphStorage<RelationshipInternal> implements RelationshipStorage {

	private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("node_a_offset"),
		ValueLayout.JAVA_LONG.withName("rel_uuid_part"),
		ValueLayout.JAVA_LONG.withName("node_b_offset"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(MAX_LABEL_LEN, ValueLayout.JAVA_BYTE).withName("label"),
		MemoryLayout.sequenceLayout(MAX_PROP_IDS, ValueLayout.JAVA_LONG).withName("props"));

	public RelationshipStorageImpl(Path path) throws IOException {
		super(path, LAYOUT, "rels");
	}

	@Override
	public RelationshipInternal read(GraphUUID uuid) throws IOException {
		long offset = uuid.offset();

		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {

			// Check if the file is large enough
			if (fc.size() < offset + LAYOUT.byteSize()) {
				throw new IOException("Relationship not found");
			}

			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, LAYOUT.byteSize(), arena);

			// Get the values
			long fromId = (long) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("node_a_offset")).get(memorySegment, 0);
			long toId = (long) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("node_b_offset")).get(memorySegment, 0);
			boolean free = (boolean) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
			String label = readLabel(memorySegment);
			long[] propIds = readPropIds(memorySegment);

			// System.out.println("Free: " + free);
			fc.force(false);
			GraphUUID fromUuid = GraphUUID.uuid(fromId);
			GraphUUID toUuid = GraphUUID.uuid(toId);
			return new RelationshipInternal(uuid, fromUuid, toUuid, label, propIds);
		}

	}

	@Override
	public void update(GraphUUID uuid, GraphUUID nodeA, GraphUUID nodeB, String label, long[] propIds) throws IOException {

		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
			long offset = uuid.offset();

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, LAYOUT.byteSize(), arena);
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rel_uuid_part")).set(memorySegment, 0, (long) uuid.randomValue());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("node_a_offset")).set(memorySegment, 0, (long) nodeA.offset());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("node_b_offset")).set(memorySegment, 0, (long) nodeB.offset());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
			fc.force(false);
		}
	}

	@Override
	public long[] loadRelationshipIds(GraphUUID fromId) {
		// MemorySegment segment = arena.allocate(NODE_REL_LAYOUT);
		// segment.ge
		// NODE_REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeId"));
		return null;
	}

	@Override
	public RelationshipInternal create(GraphUUID nodeA, GraphUUID nodeB, String label, long propIds[]) throws IOException {

		// Map the memory segment
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
			long offset = offsetProvider().next(fc);
			GraphUUID uuid = GraphUUID.uuid(offset);

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, LAYOUT.byteSize(), arena);
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rel_uuid_part")).set(memorySegment, 0, (long) uuid.randomValue());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("node_a_offset")).set(memorySegment, 0, (long) nodeA.offset());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("node_b_offset")).set(memorySegment, 0, (long) nodeB.offset());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
			fc.force(false);

			return new RelationshipInternal(uuid, nodeA, nodeB, label, propIds);
		}
	}

}
