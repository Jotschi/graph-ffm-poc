package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.element.impl.NodeImpl;
import io.metaloom.graph.core.element.impl.RelationshipImpl;
import io.metaloom.graph.core.storage.data.GraphStorage;
import io.metaloom.graph.core.storage.data.GraphStorageImpl;
import io.metaloom.graph.core.uuid.GraphUUID;

/**
 * Ensure that the map count sysctl parameter has been set to the required size.
 * 
 * sysctl -w vm.max_map_count=13107290
 */
public class GraphStorageTest extends AbstractGraphCoreTest {

	private Path basePath = Paths.get("target", "graphstorage-test");

	@BeforeEach
	public void setup() throws IOException {
		FileUtils.deleteDirectory(basePath.toFile());
		Files.createDirectories(basePath);
	}

	@Test
	public void testReadRelationship() throws Exception {
		GraphUUID uuid = null;
		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Node nodeA = new NodeImpl("Person");
			nodeA.set("name", "Wes Anderson");

			Node nodeB = new NodeImpl("Vehicle");
			nodeB.set("name", "VW Beetle");

			Relationship rel = new RelationshipImpl(nodeA, "OWNS", nodeB);
			rel.set("name", "relName");

			uuid = st.create(rel);

			Relationship loadedRel = st.readRelationship(uuid);
			System.out.println(loadedRel);
			assertRelationship(loadedRel);
		}

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Relationship rel = st.readRelationship(uuid);
			System.out.println(rel);
			assertRelationship(rel);
		}

	}

	@Test
	public void testReadNode() throws Exception {
		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Node node = new NodeImpl("Person");
			node.set("name", "Joe Doe");
			GraphUUID uuid = st.create(node);

			Node readNode = st.readNode(uuid);
			assertNotNull(readNode);
			assertEquals("Person", readNode.label());
			assertEquals("Joe Doe", readNode.get("name"));
		}
	}

	@Test
	public void testBulkRelationship() throws FileNotFoundException, Exception {

		AtomicReference<GraphUUID> firstUuid = new AtomicReference<>();

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			measure(() -> {
				for (int i = 0; i < 200_000; i++) {
					Node nodeA = new NodeImpl("Person");
					nodeA.set("name", "Wes Anderson");

					Node nodeB = new NodeImpl("Vehicle");
					nodeB.set("name", "VW Beetle");

					Relationship rel = new RelationshipImpl(nodeA, "OWNS", nodeB);
					rel.set("name", "relName");

					GraphUUID uuid = st.create(rel);
					if (firstUuid.get() == null) {
						firstUuid.set(uuid);
					}

					Relationship loadedRel = st.readRelationship(uuid);
					assertRelationship(loadedRel);
					if (i % 100 == 0) {
						System.out.println("Checked " + i);
					}
				}
			});
		}

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Relationship rel = st.readRelationship(firstUuid.get());
			assertRelationship(rel);
		}
	}

	@Test
	public void testBulkNode() throws FileNotFoundException, Exception {

		AtomicReference<GraphUUID> firstUuid = new AtomicReference<>();

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			measure(() -> {
				for (int i = 0; i < 10_000; i++) {
					Node node = new NodeImpl("Person");
					node.set("name", "Wes Anderson");

					GraphUUID uuid = st.create(node);
					if (firstUuid.get() == null) {
						firstUuid.set(uuid);
					}

					Node loadedNode = st.readNode(uuid);
					//assertNode(loadedNode);
					if (i % 100 == 0) {
						System.out.println("Checked " + i);
					}
				}
			});
		}

		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Node node = st.readNode(firstUuid.get());
			//assertRelationship(rel);
		}
	}

	@Test
	public void testTraverse() throws FileNotFoundException, Exception {
		try (GraphStorage st = new GraphStorageImpl(basePath)) {
			// st.loadRelationships(0);
		}
	}

	private void assertRelationship(Relationship loadedRel) {
		// REL
		assertNotNull(loadedRel);
		assertEquals(1, loadedRel.props().size());
		assertEquals("OWNS", loadedRel.label(), "The relationship label should have been set.");
		assertNotNull(loadedRel.uuid(), "The loaded relationship has no uuid set.");
		assertEquals("relName", loadedRel.get("name"), "The relationship name prop should have been set.");

		// FROM
		Node from = loadedRel.from();
		assertNotNull(from);
		assertEquals(1, from.props().size());
		assertEquals("Wes Anderson", from.get("name"));
		assertNotNull(from.uuid());
		assertEquals("Person", from.label());

		// TO
		Node to = loadedRel.to();
		assertNotNull(to);
		assertEquals(1, to.props().size());
		assertEquals("VW Beetle", to.get("name"));
		assertNotNull(to.uuid());
		assertEquals("Vehicle", to.label());

	}
}
