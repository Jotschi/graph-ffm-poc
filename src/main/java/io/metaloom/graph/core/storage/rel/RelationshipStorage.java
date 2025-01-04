package io.metaloom.graph.core.storage.rel;

import java.io.IOException;

import io.metaloom.graph.core.storage.data.ElementStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface RelationshipStorage extends ElementStorage<RelationshipInternal> {

	RelationshipInternal create(GraphUUID fromNodeUuid, GraphUUID toNodeUuid, String label, long propIds[]) throws IOException;

	void update(GraphUUID uuid, GraphUUID fromNodeUuid, GraphUUID toNodeUuid, String label, long propIds[]) throws IOException;

	long[] loadRelationshipIds(GraphUUID fromUuid);

}
