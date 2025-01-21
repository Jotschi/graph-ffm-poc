package io.metaloom.graph.core.storage.rel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractElementStorageTest;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorage;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorageImpl;
import io.metaloom.graph.core.internal.rel.RelationshipReferenceInternal;

public class NodeRelationshipStorageTest extends AbstractElementStorageTest {

	private Path path = Path.of("target", "node_rels.mmap");

	public static final long NODE_OFFSET = 42L;

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBasics() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
			long offset = st.create(-1, 40L, NODE_OFFSET);
			st.create(offset, 41L, NODE_OFFSET);
			st.create(offset, 42L, NODE_OFFSET);
			st.create(offset, 43L, NODE_OFFSET);

			List<RelationshipReferenceInternal> list = st.load(offset);
			assertNotNull(list);
			assertEquals(4, list.size());

			// Delete the last element of the chain
			st.deleteByRelOffset(offset, 43L);

			list = st.load(offset);
			assertEquals(3, list.size());

			// Delete the first element of the chain
			st.deleteByRelOffset(offset, 40L);

			list = st.load(offset);
			assertEquals(2, list.size());
		}
	}

	@Test
	@Override
	public void testCreate() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
			long offset = st.create(-1, 40L, NODE_OFFSET);
			assertEquals(st.header().size(), offset);
		}
	}

	@Test
	@Override
	public void testDelete() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
			long offset = st.create(-1, 40L, NODE_OFFSET);
			st.deleteByRelOffset(offset, 40L);
		}
	}

	@Test
	@Override
	public void testRead() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
			long offset = st.create(-1, 40L, NODE_OFFSET);
			List<RelationshipReferenceInternal> refs = st.load(offset);
			assertEquals(1, refs.size());
		}
	}
}
