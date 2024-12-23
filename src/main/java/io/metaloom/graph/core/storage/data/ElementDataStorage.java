package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.util.Deque;

public interface ElementDataStorage extends AutoCloseable {

	Deque<Long> getFreeIds();

	void delete(long id) throws IOException;

	/**
	 * Return a free id for a new element.
	 * 
	 * @return
	 */
	long id();
}
