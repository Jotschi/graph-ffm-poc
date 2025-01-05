package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.internal.InternalStorage;
import io.metaloom.graph.core.internal.InternalStorageImpl;
import io.metaloom.graph.core.internal.rel.RelationshipInternal;
import io.metaloom.graph.core.uuid.GraphUUID;

// Ensure map count is large enough
//sysctl -w vm.max_map_count=131072

public class DataStorageTest extends AbstractGraphCoreTest {

	Path relsPath = Path.of("target", "rels.bin");
	Path nodesPath = Path.of("target", "nodes.bin");
	Path propsPath = Path.of("target", "properties.bin");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(nodesPath);
		Files.deleteIfExists(relsPath);
		Files.deleteIfExists(propsPath);
	}

	@Test
	public void testCreate() throws Exception {
		try (InternalStorageImpl st = new InternalStorageImpl(nodesPath, relsPath, propsPath)) {
			measure(() -> {
				for (int i = 0; i < 4; i++) {
					System.out.println("Storing: " + i);
					st.node().create("Person", new long[] { 1L, 2L, 3L, 4L });
				}
				return null;
			});
		}
	}

	@Test
	public void testRelationship() throws Exception {

		Set<GraphUUID> uuids = new HashSet<>();

		try (InternalStorage st = new InternalStorageImpl(nodesPath, relsPath, propsPath)) {
			measure(() -> {
				for (int i = 0; i < 4; i++) {
					System.out.println("Storing: " + i);
					GraphUUID nodeA = GraphUUID.uuid(0);
					GraphUUID nodeB = GraphUUID.uuid(1);
					RelationshipInternal data = st.rel().create(nodeA, "Hello World", nodeB, null);
					uuids.add(data.uuid());
				}
				return null;
			});

			assertEquals(4, uuids.size());

			for (GraphUUID uuid : uuids) {
				RelationshipInternal relData = st.rel().read(uuid);
				System.out.println("REL: " + uuid + "=>" + relData.fromId() + "," + relData.toId());
			}

//			st.rel().read(2);
//			st.rel().delete(GraphUUID.uuid(2));
//			st.rel().delete(GraphUUID.uuid(4));
//			st.rel().read(2);
//			assertEquals(2, st.rel().getFreeOffsets().size(), "There should be two free ids");
//			st.rel().store(st.rel().nextOffset(), 20, 10, "Hello World1", null);
//			st.rel().store(st.rel().nextOffset(), 20, 10, "Hello World2", null);
//			assertEquals(0, st.rel().getFreeOffsets().size(), "There should be no free ids");
//			st.rel().store(st.rel().nextOffset(), 20, 10, "Hello World3", null);
//			assertEquals(0, st.rel().getFreeOffsets().size(), "There should be no free ids");
		}
		try (InternalStorageImpl st = new InternalStorageImpl(nodesPath, relsPath, propsPath)) {
			for (Long id : st.rel().offsetProvider().getFreeOffsets()) {
				System.out.println("Free Id: " + id);
			}
		}
	}

	@Test
	public void testRelationshipProps() throws Exception {
		try (InternalStorage st = new InternalStorageImpl(nodesPath, relsPath, propsPath)) {
			GraphUUID nodeA = GraphUUID.uuid(0);
			GraphUUID nodeB = GraphUUID.uuid(1);
			RelationshipInternal data = st.rel().create(nodeA, "test", nodeB, new long[] { 1, 2, 3 });
			assertNotNull(data);

			RelationshipInternal readData = st.rel().read(data.uuid());
			assertNotNull(readData);
		}
	}

}
