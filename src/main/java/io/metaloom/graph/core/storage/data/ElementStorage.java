package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.util.Deque;

import io.metaloom.graph.core.uuid.GraphUUID;

public interface ElementStorage extends AutoCloseable {

	Deque<Long> getFreeIds();

	void delete(GraphUUID uuid) throws IOException;

	/**
	 * Return a free id for a new element.
	 * 
	 * @return
	 */
	long nextOffset();
}
