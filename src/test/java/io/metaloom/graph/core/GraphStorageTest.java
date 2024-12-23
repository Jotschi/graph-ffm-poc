package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.element.impl.NodeImpl;
import io.metaloom.graph.core.element.impl.RelationshipImpl;
import io.metaloom.graph.core.storage.data.GraphStorage;
import io.metaloom.graph.core.storage.data.impl.GraphStorageImpl;

public class GraphStorageTest {

	private Path basePath = Paths.get("target", "graphstorage-test");

	@BeforeEach
	public void setup() throws IOException {
		FileUtils.deleteDirectory(basePath.toFile());
		Files.createDirectories(basePath);
	}

	@Test
	public void testAC() {
		long src[] = new long[] { 1, 2, 3, 4 };
		long dest[] = new long[2];
		System.arraycopy(src, 0, dest, 0, 2);
		for (int i = 0; i < dest.length; i++) {
			System.out.println("Dest: [" + i + "]=" + dest[i]);
		}
	}

	@Test
	public void testBasics() throws FileNotFoundException, Exception {
		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Node nodeA = new NodeImpl("Person");
			nodeA.set("name", "Wes Anderson");

			Node nodeB = new NodeImpl("Vehicle");
			nodeB.set("name", "VW Beetle");

			Relationship rel = new RelationshipImpl(nodeA, "HAS_RELATIONSHIP", nodeB);
			rel.set("name", "relName");

			long id = st.store(rel);

			Relationship loadedRel = st.loadRelationship(id);
			assertRelationship(loadedRel);
		}
	}

	private void assertRelationship(Relationship loadedRel) {
		// REL
		assertNotNull(loadedRel);
		assertEquals(1, loadedRel.props().size());
		assertEquals("HAS_RELATIONSHIP", loadedRel.label(), "The relationship label should have been set.");
		assertNotNull(loadedRel.id(), "The loaded relationship has no id set.");
		assertEquals("relName", loadedRel.get("name"), "The relationship name prop should have been set.");

		// FROM
		Node from = loadedRel.from();
		assertNotNull(from);
		assertEquals(1, from.props().size());
		assertEquals("Wes Anderson", from.get("name"));
		assertNotNull(from.id());
		assertEquals("Person", from.label());

		// TO
		Node to = loadedRel.to();
		assertNotNull(to);
		assertEquals(1, to.props().size());
		assertEquals("VW Beetle", to.get("name"));
		assertNotNull(to.id());
		assertEquals("Vehicle", to.label());

	}
}
