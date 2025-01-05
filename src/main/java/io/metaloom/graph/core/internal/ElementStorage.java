package io.metaloom.graph.core.internal;

import java.io.IOException;

import io.metaloom.graph.core.uuid.GraphUUID;

/**
 * 
 * @param <T>
 *            Return type
 */
public interface ElementStorage<T> extends AutoCloseable {

	/**
	 * Return the file header of the storage file.
	 * 
	 * @return
	 */
	FileHeader header();

	/**
	 * Return the offset provider which keeps track of free and new offsets for storage action purposes.
	 * 
	 * @return
	 */
	OffsetProvider offsetProvider();

	/**
	 * Read the element with the given uuid from the storage.
	 * 
	 * @param uuid
	 * @return
	 */
	T read(GraphUUID uuid) throws IOException;

	/**
	 * Delete the element with the provided uuid from the storage.
	 * 
	 * @param uuid
	 * @throws IOException
	 */
	void delete(GraphUUID uuid) throws IOException;

}
