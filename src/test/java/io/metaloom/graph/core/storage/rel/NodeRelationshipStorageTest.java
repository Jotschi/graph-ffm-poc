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
import io.metaloom.graph.core.internal.FileHeader;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorage;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorageImpl;
import io.metaloom.graph.core.internal.rel.RelationshipReferenceInternal;

public class NodeRelationshipStorageTest extends AbstractElementStorageTest {

	private Path path = Path.of("target", "node_rels.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBasics() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
			long nodeOffset = 42L;
			long offset = st.create(-1, 40L, nodeOffset);
			st.create(offset, 41L, nodeOffset);
			st.create(offset, 42L, nodeOffset);
			st.create(offset, 43L, nodeOffset);

			List<RelationshipReferenceInternal> list = st.load(offset);
			assertNotNull(list);
			assertEquals(4, list.size());
		}
	}

	@Test
	@Override
	public void testCreate() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
		}
	}

	@Test
	@Override
	public void testDelete() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
		}
	}

	@Test
	@Override
	public void testRead() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
		}
	}
}
