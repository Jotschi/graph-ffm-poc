package io.metaloom.graph.core.internal.rel;

import io.metaloom.graph.core.uuid.GraphUUID;

public record RelationshipInternal(GraphUUID uuid, GraphUUID fromId, GraphUUID toId, String label, long[] propIds) {

}
