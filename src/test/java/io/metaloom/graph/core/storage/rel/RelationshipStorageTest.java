package io.metaloom.graph.core.storage.rel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractElementStorageTest;
import io.metaloom.graph.core.storage.data.FileHeader;
import io.metaloom.graph.core.uuid.GraphUUID;

public class RelationshipStorageTest extends AbstractElementStorageTest {

	private Path path = Path.of("target", "rels.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	@Override
	public void testCreate() throws Exception {
		try (RelationshipStorage st = new RelationshipStorageImpl(path)) {
			GraphUUID nodeA = GraphUUID.uuid(0L);
			GraphUUID nodeB = GraphUUID.uuid(1L);
			RelationshipInternal data = st.create(nodeA, nodeB, "HAS_NAME", null);
			assertNotNull(data);
		}
	}

	@Test
	@Override
	public void testDelete() throws Exception {
		try (RelationshipStorage st = new RelationshipStorageImpl(path)) {
			GraphUUID nodeA = GraphUUID.uuid(0L);
			GraphUUID nodeB = GraphUUID.uuid(1L);
			RelationshipInternal data = st.create(nodeA, nodeB, "HAS_NAME", null);
			assertNotNull(data);

			st.delete(data.uuid());

			assertNull(st.read(data.uuid()));
		}
	}

	@Test
	@Override
	public void testRead() throws Exception {
		try (RelationshipStorage st = new RelationshipStorageImpl(path)) {
			GraphUUID nodeA = GraphUUID.uuid(0L);
			GraphUUID nodeB = GraphUUID.uuid(1L);
			RelationshipInternal data = st.create(nodeA, nodeB, "HAS_NAME", null);
			assertNotNull(data);

			RelationshipInternal readData = st.read(data.uuid());
			assertNotNull(readData);
			assertEquals("HAS_NAME", readData.label());
		}
	}

	@Test
	public void testBasics() throws Exception {
		try (RelationshipStorage st = new RelationshipStorageImpl(path)) {

			// Create Relationship
			GraphUUID nodeA = GraphUUID.uuid(0L);
			GraphUUID nodeB = GraphUUID.uuid(1L);
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
