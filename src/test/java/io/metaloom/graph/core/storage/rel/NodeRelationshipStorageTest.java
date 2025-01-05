package io.metaloom.graph.core.storage.rel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.AbstractElementStorageTest;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorage;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorageImpl;

public class NodeRelationshipStorageTest extends AbstractElementStorageTest {

	private Path path = Path.of("target", "node_rels.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBasics() throws Exception {
		try (NodeRelationshipStorage st = new NodeRelationshipStorageImpl(path)) {
			st.create(0L, 0L);
		}
	}

	@Test
	@Override
	public void testCreate() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Test
	@Override
	public void testDelete() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Test
	@Override
	public void testRead() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
