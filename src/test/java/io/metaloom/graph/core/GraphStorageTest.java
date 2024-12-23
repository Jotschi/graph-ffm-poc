package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.storage.impl.GraphStorageImpl;

// Ensure map count is large enough
//sysctl -w vm.max_map_count=131072

public class GraphStorageTest {

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
		try (GraphStorageImpl st = new GraphStorageImpl(nodesPath, relsPath, propsPath)) {
			measure(() -> {
				for (int i = 0; i < 4; i++) {
					System.out.println("Storing: " + i);
					st.node().store(i, "Person");
				}
				return null;
			});
		}
	}

	@Test
	public void testRelationship() throws Exception {
		try (GraphStorageImpl st = new GraphStorageImpl(nodesPath, relsPath, propsPath)) {
			measure(() -> {
				for (int i = 0; i < 4; i++) {
					System.out.println("Storing: " + i);
					st.rel().store(st.rel().id(), i + 20, i + 10, "Hello World");
				}
				return null;
			});

			for (int i = 0; i < 4; i++) {
				long[] rels = st.rel().load(i);
				System.out.println("REL: " + i + "=>" + rels[0] + "," + rels[1]);
			}

			st.rel().load(2);
			st.rel().delete(2);
			st.rel().delete(4);
			st.rel().load(2);
			assertEquals(2, st.rel().getFreeIds().size(), "There should be two free ids");
			st.rel().store(st.rel().id(), 20, 10, "Hello World1");
			st.rel().store(st.rel().id(), 20, 10, "Hello World2");
			assertEquals(0, st.rel().getFreeIds().size(), "There should be no free ids");
			st.rel().store(st.rel().id(), 20, 10, "Hello World3");
			assertEquals(0, st.rel().getFreeIds().size(), "There should be no free ids");
		}
		try (GraphStorageImpl st = new GraphStorageImpl(nodesPath, relsPath, propsPath)) {
			for (Long id : st.rel().getFreeIds()) {
				System.out.println("Free Id: " + id);
			}
		}

	}

	private <T> T measure(TimeableAction<T> action) throws Exception {
		long start = System.currentTimeMillis();
		T ret = action.invoke();
		System.out.println("Duration: " + (System.currentTimeMillis() - start));
		return ret;
	}

}
