package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.util.Set;

import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface GraphStorage extends AutoCloseable {

	/**
	 * Store the relationship in the graph.
	 * 
	 * @param relationship
	 * @return UUID of the created relationship.
	 * @throws IOException
	 */
	GraphUUID create(Relationship relationship) throws IOException;

	/**
	 * Store the node in the graph.
	 * 
	 * @param node
	 * @return UUID of the created node.
	 * @throws IOException
	 */
	GraphUUID create(Node node) throws IOException;

	/**
	 * Load the relationship of the given uuid.
	 * 
	 * @param uuid
	 * @return Read relationship
	 * @throws IOException
	 */
	Relationship readRelationship(GraphUUID uuid) throws IOException;

	/**
	 * Traverse the relationships for the given node from nodeUuid.
	 * 
	 * @param nodeUuid
	 * @param maxDepth
	 * @return
	 */
	Set<Relationship> traverse(GraphUUID nodeUuid, int maxDepth) throws IOException;

	Node readNode(GraphUUID uuid) throws IOException;

}
