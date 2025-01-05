package io.metaloom.graph.core.internal;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHeader {

	private static final Logger logger = LoggerFactory.getLogger(FileHeader.class);

	protected static final String VERSION_KEY = "version";

	private static final String COUNT_KEY = "count";

	protected static final int VERSION_V1 = 1;

	protected static final String HEADER_MAGIC_BYTES_KEY = "magic_bytes";

	private final Path path;

	private final Arena arena;

	public static final GroupLayout HEADER_LAYOUT = MemoryLayout.structLayout(
		MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_BYTE).withName(HEADER_MAGIC_BYTES_KEY),
		ValueLayout.JAVA_INT.withName(VERSION_KEY),
		ValueLayout.JAVA_LONG.withName(COUNT_KEY),
		MemoryLayout.paddingLayout(512));

	public FileHeader(Path path, String magicString) throws IOException {
		this.arena = Arena.ofAuto();
		this.path = path;

		if (!Files.exists(path)) {
			init(magicString);
		} else {
			logger.info("Not updating header since storage file {} already found", path);
		}

	}

	public long loadCount() throws IOException {
		logger.info("Loading element count from storage file header {}", path);
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
			MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, 0, HEADER_LAYOUT.byteSize(), arena);
			long count = (long) HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(COUNT_KEY)).get(memorySegment, 0);
			logger.info("Loaded element count {} from storage file {}", count, path);
			return count;
		}
	}

	public void setCount(FileChannel fc, long count) throws IOException {
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, 0, HEADER_LAYOUT.byteSize(), arena);
		HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(COUNT_KEY)).set(memorySegment, 0, (long) count);
	}

	/**
	 * Initialize the data file by writing the initial header.
	 * 
	 * @param magicString
	 * @throws IOException
	 */
	protected void init(String magicString) throws IOException {
		logger.info("Initialize storage file {} for type {}", path, magicString);
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, 0, HEADER_LAYOUT.byteSize(), arena);
			VarHandle magicBytesHandle = HEADER_LAYOUT.varHandle(
				MemoryLayout.PathElement.groupElement(HEADER_MAGIC_BYTES_KEY),
				MemoryLayout.PathElement.sequenceElement());

			byte[] magicBytes = magicString.getBytes();
			for (int i = 0; i < magicBytes.length; i++) {
				magicBytesHandle.set(memorySegment, 0, i, magicBytes[i]);
			}

			HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(VERSION_KEY)).set(memorySegment, 0, (int) VERSION_V1);
			HEADER_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(COUNT_KEY)).set(memorySegment, 0, (long) 0);
		}
	}

	/**
	 * Size of the header area in bytes.
	 * 
	 * @return
	 */
	public long size() {
		return HEADER_LAYOUT.byteSize();
	}

}
