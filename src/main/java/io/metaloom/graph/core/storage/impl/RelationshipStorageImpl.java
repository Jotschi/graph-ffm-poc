package io.metaloom.graph.core.storage.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;

import io.metaloom.graph.core.storage.AbstractGraphStorage;
import io.metaloom.graph.core.storage.RelationshipStorage;

public class RelationshipStorageImpl extends AbstractGraphStorage implements RelationshipStorage {

	private static final GroupLayout REL_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("relId"),
		ValueLayout.JAVA_LONG.withName("nodeAId"),
		ValueLayout.JAVA_LONG.withName("nodeBId"),
		ValueLayout.JAVA_BOOLEAN.withName("free"),
		MemoryLayout.paddingLayout(7),
		MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("label")); // 32-byte label

	public RelationshipStorageImpl(Path path) throws FileNotFoundException {
		super(path, REL_LAYOUT);
	}

	@Override
	public long[] load(long relId) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = raFile.getChannel();

		// Check if the file is large enough
		if (raFile.length() < offset + REL_LAYOUT.byteSize()) {
			throw new IOException("Relationship not found");
		}

		MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, REL_LAYOUT.byteSize(), arena);

		// Get the values
		long nodeAId = (long) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeAId")).get(memorySegment, 0);
		long nodeBId = (long) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeBId")).get(memorySegment, 0);
		boolean free = (boolean) REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).get(memorySegment, 0);
		readLabel(memorySegment);

		System.out.println("Free: " + free);
		return new long[] { nodeAId, nodeBId };
	}

	@Override
	public void store(long relId, long nodeAId, long nodeBId, String label) throws IOException {
		// Calculate the offset
		long offset = relId * REL_LAYOUT.byteSize();

		// Map the memory segment
		FileChannel fc = raFile.getChannel();

		// Ensure the file is large enough
		ensureFileCapacity(fc, offset, layout);

		// Set the values
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, REL_LAYOUT.byteSize(), arena);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("relId")).set(memorySegment, 0, (long) relId);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeAId")).set(memorySegment, 0, (long) nodeAId);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nodeBId")).set(memorySegment, 0, (long) nodeBId);
		REL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("free")).set(memorySegment, 0, false);
		writeLabel(memorySegment, label);
	}

}
