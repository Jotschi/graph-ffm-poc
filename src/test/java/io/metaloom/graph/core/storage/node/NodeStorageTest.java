package io.metaloom.graph.core.storage.node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractGraphCoreTest;
import io.metaloom.graph.core.storage.data.AbstractElementStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public class NodeStorageTest extends AbstractGraphCoreTest {

	private Path path = Path.of("target", "data.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBasics() throws Exception {
		try (NodeDataStorage st = new NodeDataStorageImpl(path)) {
			GraphUUID uuid = st.uuid();
			assertEquals(AbstractElementStorage.HEADER_LAYOUT.byteSize(), uuid.offset());
			st.store(uuid, "HAS_NAME", null);

			NodeData node = st.load(uuid);
			assertEquals("HAS_NAME", node.label());
			assertEquals(uuid.toString(), node.uuid().toString(), "The uuids should match");

			GraphUUID uuid2 = st.uuid();
			assertEquals(AbstractElementStorage.HEADER_LAYOUT.byteSize() + NodeDataStorageImpl.NODE_LAYOUT.byteSize(), uuid2.offset());
		}
	}
}
