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
import io.metaloom.graph.core.storage.data.RelationshipDataStorage;

public class RelationshipDataStorageImpl extends AbstractGraphStorage implements RelationshipDataStorage {

	private static final GroupLayout REL_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("relId"),
		ValueLayout.JAVA_LONG.withName("nodeAId"),
		ValueLayout.JAVA_LONG.withName("nodeBId"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(MAX_LABEL_LEN, ValueLayout.JAVA_BYTE).withName("label"),
		MemoryLayout.sequenceLayout(MAX_PROP_IDS, ValueLayout.JAVA_LONG).withName("props"));

	private static final GroupLayout NODE_REL_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("nodeId"),
		ValueLayout.JAVA_LONG.withName("relId"));
	
	public RelationshipDataStorageImpl(Path path) throws FileNotFoundException {
		super(path, REL_LAYOUT);
	}

	@Override
	public RelationshipData load(long relId) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();

		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {

			// Check if the file is large enough
			if (fc.size() < offset + REL_LAYOUT.byteSize()) {
				throw new IOException("Relationship not found");
			}

			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, REL_LAYOUT.byteSize(), arena);

			// Get the values
			long fromId = (long) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeAId")).get(memorySegment, 0);
			long toId = (long) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeBId")).get(memorySegment, 0);
			boolean free = (boolean) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
			String label = readLabel(memorySegment);
			long[] propIds = readPropIds(memorySegment);

			// System.out.println("Free: " + free);
			fc.force(false);
			return new RelationshipData(fromId, toId, label, propIds);
		}

	}

	@Override
	public long[] loadRelationshipIds(long fromId) {
//		MemorySegment segment = arena.allocate(NODE_REL_LAYOUT);
//		segment.ge
//		NODE_REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeId"));
		return null;
	}

	@Override
	public void store(long relId, long nodeAId, long nodeBId, String label, long propIds[]) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();

		// Map the memory segment
		// FileChannel fc = raFile.getChannel();
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

			// Ensure the file is large enough
			ensureFileCapacity(fc, offset, layout);

			// Set the values
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, REL_LAYOUT.byteSize(), arena);
			REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("relId")).set(memorySegment, 0, (long) relId);
			REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeAId")).set(memorySegment, 0, (long) nodeAId);
			REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeBId")).set(memorySegment, 0, (long) nodeBId);
			REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);
			writeLabel(memorySegment, label);
			writePropIds(memorySegment, propIds);
			fc.force(false);
		}
	}

}
