package io.metaloom.graph.core.storage.rel;

import java.io.IOException;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.metaloom.graph.core.storage.data.AbstractElementStorage;

public class NodeRelationshipStorageImpl extends AbstractElementStorage implements NodeRelationshipStorage {

	private static final String HEADER_FILE_TYPE = "nore";

	private static final String NODE_OFFSET_KEY = "node_offset";

	private static final String REL_OFFSET_KEY = "rel_offset";

	private static final String NEXT_OFFSET_KEY = "next_offset";

	private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName(NODE_OFFSET_KEY),
		ValueLayout.JAVA_LONG.withName(NEXT_OFFSET_KEY),
		ValueLayout.JAVA_LONG.withName(REL_OFFSET_KEY));

	public NodeRelationshipStorageImpl(Path path) throws IOException {
		super(path, HEADER_FILE_TYPE);
	}

	public void store() throws IOException {
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

			// Ensure the file is large enough
			// ensureFileCapacity(fc, offset, layout);
		}
	}

}
