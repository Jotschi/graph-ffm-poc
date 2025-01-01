package io.metaloom.graph.core.storage.node;

import io.metaloom.graph.core.uuid.GraphUUID;

public record NodeData(GraphUUID uuid, String label, long[] propIds) {

}
