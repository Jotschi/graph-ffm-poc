package io.metaloom.graph.core.ffm;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FFMLinkedListTest {

	public static final Path DATA_FILE = Path.of("target", "data.mmap");

	public static final long fileSize = 512;

	public static final MemoryLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.ADDRESS.withName("prev"),
		ValueLayout.JAVA_LONG.withName("id"),
		ValueLayout.ADDRESS.withName("next"));

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(DATA_FILE);
	}

	@Test
	public void testInterAddr() throws IOException {
		Arena session = Arena.ofAuto();

		try (FileChannel fileChannel = FileChannel.open(DATA_FILE,
			StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			fileChannel.truncate(fileSize);

			// Node 1
			MemorySegment node1 = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize, session);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id")).set(node1, 0, 41L);

			// Node 2
			MemorySegment node2 = fileChannel.map(FileChannel.MapMode.READ_WRITE, NODE_LAYOUT.byteSize(), fileSize, session);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id")).set(node2, 0, 42L);

			// Node 2 -> Node 1
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("prev")).set(node2, 0, node1);

			// Node 1 -> Node 2
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("next")).set(node1, 0, node2);

			// Follow the address references (next and prev and print the values)
			System.out.println("Value: " + loadIdFromReference(node2, "prev")); // Points to Node 1 (41L)
			System.out.println("Value: " + loadIdFromReference(node1, "next")); // Points to Node 2 (42L)

		}

	}

	private long loadIdFromReference(MemorySegment data, String key) {
		MemorySegment loadedSegment = (MemorySegment) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(key)).get(data, 0);
		loadedSegment = loadedSegment.reinterpret(NODE_LAYOUT.byteSize());
		return (long) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id")).get(loadedSegment, 0);
	}
}
