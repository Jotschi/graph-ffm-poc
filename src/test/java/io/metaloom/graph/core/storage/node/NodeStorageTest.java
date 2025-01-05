package io.metaloom.graph.core.storage.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractElementStorageTest;
import io.metaloom.graph.core.internal.FileHeader;
import io.metaloom.graph.core.internal.node.NodeInternal;
import io.metaloom.graph.core.internal.node.NodeStorage;
import io.metaloom.graph.core.internal.node.NodeStorageImpl;

public class NodeStorageTest extends AbstractElementStorageTest {

	private Path path = Path.of("target", "nodes.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBulk() throws Exception {
		try (NodeStorage st = new NodeStorageImpl(path)) {
			measure(() -> {
				for (int i = 0; i < 1_000_000; i++) {
					NodeInternal createdNode = st.create("HAS_NAME", null);
					assertNotNull(createdNode);

					NodeInternal readNode = st.read(createdNode.uuid());
					assertNotNull(readNode);

					if (i % 100 == 0) {
						System.out.println(i);
					}
				}
			});
		}
	}

	@Test
	@Override
	public void testCreate() throws Exception {
		try (NodeStorage st = new NodeStorageImpl(path)) {
			NodeInternal createdNode = st.create("HAS_NAME", null);
			assertNotNull(createdNode);
			assertNotNull(createdNode.uuid());
			assertEquals(FileHeader.HEADER_LAYOUT.byteSize(), createdNode.uuid().offset());
		}
	}

	@Test
	@Override
	public void testRead() throws Exception {
		try (NodeStorage st = new NodeStorageImpl(path)) {
			NodeInternal node = st.create("HAS_NAME", null);

			st.delete(node.uuid());

			assertNull(st.read(node.uuid()));
		}
	}

	@Test
	@Override
	public void testDelete() throws Exception {
		try (NodeStorage st = new NodeStorageImpl(path)) {
			NodeInternal node = st.create("HAS_NAME", null);

			st.delete(node.uuid());

			assertNull(st.read(node.uuid()));
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
