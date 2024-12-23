package io.metaloom.graph.core.storage;

import java.io.IOException;

public interface PropertyStorage extends AutoCloseable {

	/**
	 * Store the key and value and return the offset id for the stored entry
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 */
	long store(String key, String value) throws IOException;

	/**
	 * Load the key value pair for the id.
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	String[] get(long id) throws IOException;

}
