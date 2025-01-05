package io.metaloom.graph.core.internal.rel;

import java.io.IOException;

import io.metaloom.graph.core.internal.ElementStorage;
import io.metaloom.graph.core.uuid.GraphUUID;

public interface RelationshipStorage extends ElementStorage<RelationshipInternal> {

	RelationshipInternal create(GraphUUID fromNodeUuid, String label, GraphUUID toNodeUuid, long propIds[]) throws IOException;

	void update(GraphUUID uuid, GraphUUID fromNodeUuid, String label, GraphUUID toNodeUuid, long propIds[]) throws IOException;

	long[] loadRelationshipIds(GraphUUID fromUuid);

}
