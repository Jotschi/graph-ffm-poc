package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.nio.file.Path;

public abstract class AbstractMMapFileStorage implements AutoCloseable {

	protected Arena arena;

	protected final Path path;

	public AbstractMMapFileStorage(Path path) throws IOException {
		this.arena = Arena.ofShared();
		this.path = path;
	}

	@Override
	public void close() throws Exception {
		if (arena != null) {
			arena.close();
		}
	}

}
