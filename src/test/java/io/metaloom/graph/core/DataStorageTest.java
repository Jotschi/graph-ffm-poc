package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.storage.data.DataStorage;
import io.metaloom.graph.core.storage.data.impl.DataStorageImpl;
import io.metaloom.graph.core.storage.data.impl.RelationshipData;

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
	public void testNode() throws Exception {
		try (DataStorageImpl st = new DataStorageImpl(nodesPath, relsPath, propsPath)) {
			measure(() -> {
				for (int i = 0; i < 4; i++) {
					System.out.println("Storing: " + i);
					st.node().store(i, "Person", new long[] { 1L, 2L, 3L, 4L });
				}
				return null;
			});
		}
	}

	@Test
	public void testRelationship() throws Exception {
		try (DataStorage st = new DataStorageImpl(nodesPath, relsPath, propsPath)) {
			measure(() -> {
				for (int i = 0; i < 4; i++) {
					System.out.println("Storing: " + i);
					st.rel().store(st.rel().id(), i + 20, i + 10, "Hello World", null);
				}
				return null;
			});

			for (int i = 0; i < 4; i++) {
				RelationshipData relData = st.rel().load(i);
				System.out.println("REL: " + i + "=>" + relData.fromId() + "," + relData.toId());
			}

			st.rel().load(2);
			st.rel().delete(2);
			st.rel().delete(4);
			st.rel().load(2);
			assertEquals(2, st.rel().getFreeIds().size(), "There should be two free ids");
			st.rel().store(st.rel().id(), 20, 10, "Hello World1", null);
			st.rel().store(st.rel().id(), 20, 10, "Hello World2", null);
			assertEquals(0, st.rel().getFreeIds().size(), "There should be no free ids");
			st.rel().store(st.rel().id(), 20, 10, "Hello World3", null);
			assertEquals(0, st.rel().getFreeIds().size(), "There should be no free ids");
		}
		try (DataStorageImpl st = new DataStorageImpl(nodesPath, relsPath, propsPath)) {
			for (Long id : st.rel().getFreeIds()) {
				System.out.println("Free Id: " + id);
			}
		}
	}

	@Test
	public void testRelationshipProps() throws Exception {
		try (DataStorage st = new DataStorageImpl(nodesPath, relsPath, propsPath)) {
			long id = st.rel().id();
			st.rel().store(id, 41L, 42L, "test", new long[] { 1, 2, 3 });
			RelationshipData data = st.rel().load(id);
			assertNotNull(data);
		}
	}

}
