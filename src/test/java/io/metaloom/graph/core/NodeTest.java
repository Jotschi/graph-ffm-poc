package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
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
					st.node().store(i, 42);
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
					st.rel().store(i, i + 20, i + 10, "Hello World");
				}
				return null;
			});

			for (int i = 0; i < 4; i++) {
				long[] rels = st.rel().load(i);
				System.out.println("REL: " + i + "=>" + rels[0] + "," + rels[1]);
			}

			st.rel().load(2);
			st.rel().delete(2);
			st.rel().load(2);
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

	@Test
	public void testFMA() {
		long value = 10;
		MemoryLayout pointLayout = MemoryLayout.structLayout(
			ValueLayout.JAVA_LONG.withName("id"),
			ValueLayout.JAVA_INT.withName("y"));
		VarHandle xHandle = pointLayout.varHandle(MemoryLayout.PathElement.groupElement("id"));
		Arena arena = Arena.ofAuto();
		MemorySegment segment = arena.allocate(pointLayout);
		xHandle.set(segment, 0, (long) value);
		long xValue = (long) xHandle.get(segment, 0);

		assertEquals(xValue, value);
	}
}
