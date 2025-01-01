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
import io.metaloom.graph.core.storage.data.GraphStorageImpl;

public class GraphStorageTest extends AbstractGraphCoreTest {

	private Path basePath = Paths.get("target", "graphstorage-test");

	@BeforeEach
	public void setup() throws IOException {
		FileUtils.deleteDirectory(basePath.toFile());
		Files.createDirectories(basePath);
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

	@Test
	public void testBulk() throws FileNotFoundException, Exception {
		try (GraphStorage st = new GraphStorageImpl(basePath)) {

			measure(() -> {
				for (int i = 0; i < 10_000; i++) {
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
				return null;
			});
		}

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Relationship rel = st.loadRelationship(0L);
			assertRelationship(rel);
		}
	}

	@Test
	public void testWriteReadFS() throws FileNotFoundException, Exception {
		try (GraphStorage st = new GraphStorageImpl(basePath)) {
				Node nodeA = new NodeImpl("Person");
			nodeA.set("name", "Wes Anderson");

			Node nodeB = new NodeImpl("Vehicle");
			nodeB.set("name", "VW Beetle");

			Relationship rel = new RelationshipImpl(nodeA, "OWNS", nodeB);
			rel.set("name", "relName");

			long id = st.store(rel);

			Relationship loadedRel = st.loadRelationship(id);
			System.out.println(loadedRel);
			assertRelationship(loadedRel);
		}

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Relationship rel = st.loadRelationship(1L);
			System.out.println(rel);
			assertRelationship(rel);
		}
	}
	
	@Test
	public void testTraverse() throws FileNotFoundException, Exception {
		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			st.loadRelationships(0);
		}
	}

	private void assertRelationship(Relationship loadedRel) {
		// REL
		assertNotNull(loadedRel);
		assertEquals(1, loadedRel.props().size());
		assertEquals("OWNS", loadedRel.label(), "The relationship label should have been set.");
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
