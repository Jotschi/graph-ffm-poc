package io.metaloom.graph.core.internal;

import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractElementStorage<T> extends AbstractMMapFileStorage implements ElementStorage<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractElementStorage.class);

	protected static final String FREE_KEY = "free";

	protected static final String HEADER_FILE_TYPE_KEY = "file_type";

	private final MemoryLayout elementLayout;

	private final FileHeader header;

	private final OffsetProvider offsetProvider;

	public AbstractElementStorage(Path path, String magicByte, MemoryLayout elementLayout) throws IOException {
		super(path);
		this.elementLayout = elementLayout;
		this.header = new FileHeader(path, magicByte);
		this.offsetProvider = new OffsetProvider(header(), elementLayout);
	}

	@Override
	public FileHeader header() {
		return header;
	}

	@Override
	public OffsetProvider offsetProvider() {
		return offsetProvider;
	}

}
