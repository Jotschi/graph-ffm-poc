package io.metaloom.graph.core.internal.node;

import java.io.IOException;

import io.metaloom.graph.core.internal.ElementStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface NodeStorage extends ElementStorage<NodeInternal> {

	NodeInternal create(String label, long propIds[]) throws IOException;

	void update(GraphUUID uuid, String label, long propIds[]) throws IOException;

	long getNodeRelOffset(GraphUUID uuid) throws IOException;

	void setNodeRelOffset(GraphUUID uuid, long nodeRelOffset) throws IOException;

}
