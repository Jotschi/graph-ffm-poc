package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public abstract class AbstractMMapFileStorage implements AutoCloseable {

	protected Arena arena;

	protected RandomAccessFile raFile;

	protected final Path path;

	public AbstractMMapFileStorage(Path path) throws IOException {
		this.arena = Arena.ofShared();
		this.path = path;
		this.raFile = new RandomAccessFile(path.toFile(), "rw");
	}

	@Override
	public void close() throws Exception {
		if (arena != null) {
			arena.close();
		}
		raFile.close();
	}

	protected void ensureFileCapacity(FileChannel fc, long offset, MemoryLayout layout) throws IOException {
		ensureFileCapacity(fc, offset, layout.byteSize());
	}

	protected void ensureFileCapacity(FileChannel fc, long offset, long size) throws IOException {
		if (raFile.length() < offset + size) {
			// Write zeros to extend the file
			byte[] zeros = new byte[(int) (offset + size - raFile.length())];
			fc.position(raFile.length());
			fc.write(ByteBuffer.wrap(zeros));
			// System.out.println("Adding: " + zeros.length + " bytes to the file");
		}
	}

}
