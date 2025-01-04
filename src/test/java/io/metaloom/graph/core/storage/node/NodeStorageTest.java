package io.metaloom.graph.core.storage.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractGraphCoreTest;
import io.metaloom.graph.core.storage.data.FileHeader;

public class NodeStorageTest extends AbstractGraphCoreTest {

	private Path path = Path.of("target", "data.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testCreate() throws IOException, Exception {
		try (NodeStorage st = new NodeStorageImpl(path)) {
			NodeInternal createdNode = st.create("HAS_NAME", null);
			assertNotNull(createdNode);
			assertNotNull(createdNode.uuid());
			assertEquals(FileHeader.HEADER_LAYOUT.byteSize(), createdNode.uuid().offset());
		}
	}

	@Test
	public void testBasics() throws Exception {
		try (NodeStorage st = new NodeStorageImpl(path)) {
			NodeInternal createdNode = st.create("HAS_NAME", null);
			assertEquals(FileHeader.HEADER_LAYOUT.byteSize(), createdNode.uuid().offset());

			NodeInternal readNode = st.read(createdNode.uuid());
			assertEquals("HAS_NAME", readNode.label());
			assertEquals(createdNode.uuid().toString(), readNode.uuid().toString(), "The uuids should match");

			long count = st.offsetProvider().getElementCount();
			assertEquals(1, count);
		}
	}
}
