package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.storage.impl.MemoryStorageImpl;

// Ensure map count is large enough
//sysctl -w vm.max_map_count=131072

public class NodeTest {

	File relsFile = new File("target", "rels.bin");
	File nodesFile = new File("target", "nodes.bin");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(nodesFile.toPath());
		Files.deleteIfExists(relsFile.toPath());
	}

	@Test
	public void testNode() throws Exception {
		try (MemoryStorageImpl st = new MemoryStorageImpl(nodesFile, relsFile)) {
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
		try (MemoryStorageImpl st = new MemoryStorageImpl(nodesFile, relsFile)) {
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
		try (MemoryStorageImpl st = new MemoryStorageImpl(nodesFile, relsFile)) {
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
