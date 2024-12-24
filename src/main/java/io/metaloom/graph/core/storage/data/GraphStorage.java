package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.util.Set;

import io.metaloom.graph.core.element.Relationship;

public interface GraphStorage extends AutoCloseable {

	long store(Relationship rel) throws IOException;

	/**
	 * Load the relationship of the given id.
	 * 
	 * @param relId
	 * @return
	 * @throws IOException
	 */
	Relationship loadRelationship(long relId) throws IOException;

	/**
	 * Load all relationships for the given from nodeId.
	 * 
	 * @param fromId
	 * @return
	 */
	Set<Relationship> loadRelationships(long fromId) throws IOException;

}
