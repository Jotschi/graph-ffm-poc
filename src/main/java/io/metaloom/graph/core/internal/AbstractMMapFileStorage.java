package io.metaloom.graph.core.internal;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractMMapFileStorage implements AutoCloseable {


	protected final Path path;

	public AbstractMMapFileStorage(Path path) throws IOException {
		this.path = path;
	}

	@Override
	public void close() throws Exception {
	}

}
