package io.metaloom.graph.core.storage.node;

import io.metaloom.graph.core.uuid.GraphUUID;

public record NodeInternal(GraphUUID uuid, String label, long[] propIds) {

}
