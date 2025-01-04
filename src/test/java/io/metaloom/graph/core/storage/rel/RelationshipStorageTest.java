package io.metaloom.graph.core.storage.rel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractGraphCoreTest;
import io.metaloom.graph.core.storage.data.FileHeader;
import io.metaloom.graph.core.uuid.GraphUUID;

public class RelationshipStorageTest extends AbstractGraphCoreTest {
	private Path path = Path.of("target", "data.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBasics() throws Exception {
		try (RelationshipStorage st = new RelationshipStorageImpl(path)) {

			// Create Relationship
			GraphUUID nodeA = null;
			GraphUUID nodeB = null;
			RelationshipInternal data = st.create(nodeA, nodeB, "HAS_NAME", null);
			GraphUUID uuid = data.uuid();
			assertEquals(FileHeader.HEADER_LAYOUT.byteSize(), uuid.offset());

			// Update Relationship
			st.update(uuid, nodeA, nodeB, "HAS_NAME", null);

			// Read Relationship
			RelationshipInternal readData = st.read(uuid);
			assertEquals("HAS_NAME", readData.label());
			assertEquals(uuid.toString(), readData.uuid().toString(), "The uuids should match");

			long count = st.offsetProvider().getElementCount();
			assertEquals(1, count);
		}
	}
}
