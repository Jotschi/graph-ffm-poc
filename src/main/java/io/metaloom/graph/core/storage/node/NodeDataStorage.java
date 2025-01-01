package io.metaloom.graph.core.storage.node;

import java.io.IOException;

import io.metaloom.graph.core.storage.data.ElementStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface NodeDataStorage extends ElementStorage {

	void store(GraphUUID uuid, String label, long propIds[]) throws IOException;

	NodeData load(GraphUUID uuid) throws IOException;

	/**
	 * Return the next free UUID.
	 * 
	 * @return
	 */
	GraphUUID uuid();

}
