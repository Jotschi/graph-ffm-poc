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

public class FFMAddressTest {

	public static final Path FILE_1 = Path.of("target", "data.mmap");
	public static final Path FILE_2 = Path.of("target", "data.mmap");

	public static final long fileSize = 512;

	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("key"),
		ValueLayout.ADDRESS.withName("addr"));

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(FILE_1);
		Files.deleteIfExists(FILE_2);
	}

	@Test
	public void testInterAddr() throws IOException {
		Arena session = Arena.ofAuto();

		MemorySegment segment2 = session.allocate(512);
		LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("key")).set(segment2, 0, 42L);
		System.out.println("Data: " +  segment2.address());

		try (FileChannel fileChannel = FileChannel.open(FILE_1,
			StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			fileChannel.truncate(fileSize);

			MemorySegment segment = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize, session);
			System.out.println(segment.address());
			LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("addr")).set(segment2, 0, segment);
			System.out.println(segment);

			MemorySegment loadedSegment = (MemorySegment) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("addr")).get(segment2, 0);
			loadedSegment = loadedSegment.reinterpret(LAYOUT.byteSize());

			System.out.println(loadedSegment);
			long c = (long) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("key")).get(loadedSegment, 0);
			System.out.println("Loaded from loaded Segment: " + c);
		}

	}
}
