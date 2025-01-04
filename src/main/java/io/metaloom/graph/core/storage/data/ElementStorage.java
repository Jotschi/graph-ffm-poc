package io.metaloom.graph.core.storage.data;

import java.io.IOException;

import io.metaloom.graph.core.uuid.GraphUUID;

/**
 * 
 * @param <T>
 *            Return type
 */
public interface ElementStorage<T> extends AutoCloseable {

	FileHeader header();

	OffsetProvider offsetProvider();

	T read(GraphUUID uuid) throws IOException;

	void delete(GraphUUID uuid) throws IOException;

}
