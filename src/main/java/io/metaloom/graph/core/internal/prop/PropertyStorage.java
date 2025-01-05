package io.metaloom.graph.core.internal.prop;

import java.io.IOException;
import java.util.Map;

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

	/**
	 * Load all key value pairs and return the map.
	 * 
	 * @param propIds
	 * @return
	 */
	Map<String, String> getAll(long[] propIds) throws IOException;

	/**
	 * Store the provided props and return the prop ids.
	 * 
	 * @param props
	 * @return
	 */
	long[] store(Map<String, String> props) throws IOException;

}
