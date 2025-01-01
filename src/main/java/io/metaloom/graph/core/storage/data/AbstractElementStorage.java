package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class AbstractElementStorage extends AbstractMMapFileStorage {

	private static final String LAST_OFFSET_KEY = "last_offset";

	protected static final int VERSION_V1 = 1;

	protected static final String HEADER_MAGIC_BYTES_KEY = "magic_bytes";

	protected static final String HEADER_FILE_TYPE_KEY = "file_type";

	protected static final String VERSION_KEY = "version";

	public static final GroupLayout HEADER_LAYOUT = MemoryLayout.structLayout(
		MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_BYTE).withName(HEADER_MAGIC_BYTES_KEY),
		ValueLayout.JAVA_INT.withName(VERSION_KEY),
		ValueLayout.JAVA_LONG.withName(LAST_OFFSET_KEY),
		MemoryLayout.paddingLayout(512));

	public AbstractElementStorage(Path path, String magicByte) throws IOException {
		super(path);
		if (!Files.exists(path)) {
			init(magicByte);
		}
	}

	protected void init(String magicByte) throws IOException {
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			initHeader(fc, magicByte);
		}
	}

	private void initHeader(FileChannel fc, String magicByte) throws IOException {
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, 0, HEADER_LAYOUT.byteSize(), arena);
		HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(HEADER_FILE_TYPE_KEY)).set(memorySegment, 0, magicByte);
		HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(VERSION_KEY)).set(memorySegment, 0, (int) VERSION_V1);
		HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(LAST_OFFSET_KEY)).set(memorySegment, 0, MemorySegment.NULL);
	}

	public void setLastOffset(FileChannel fc, long offset) throws IOException {
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, 0, HEADER_LAYOUT.byteSize(), arena);
		HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(LAST_OFFSET_KEY)).set(memorySegment, 0, (long) offset);
	}

}
