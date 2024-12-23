package io.metaloom.graph.core.storage.data;

import java.io.IOException;

import io.metaloom.graph.core.storage.data.impl.NodeData;

public interface NodeDataStorage extends ElementDataStorage {

	void store(long id, String label, long propIds[]) throws IOException;

	NodeData load(long nodeId) throws IOException;

}
