package io.metaloom.graph.core.storage.node;

import java.io.IOException;

import io.metaloom.graph.core.storage.data.ElementStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface NodeStorage extends ElementStorage<NodeInternal> {

	NodeInternal create(String label, long propIds[]) throws IOException;

	void update(GraphUUID uuid, String label, long propIds[]) throws IOException;

}
