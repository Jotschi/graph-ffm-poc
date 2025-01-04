package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.util.Set;

import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface GraphStorage extends AutoCloseable {

	GraphUUID create(Relationship rel) throws IOException;

	/**
	 * Load the relationship of the given uuid.
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	Relationship readRelationship(GraphUUID uuid) throws IOException;

	/**
	 * Load all relationships for the given from nodeUuid.
	 * 
	 * @param nodeUuid
	 * @return
	 */
	Set<Relationship> readRelationships(GraphUUID nodeUuid) throws IOException;

}
