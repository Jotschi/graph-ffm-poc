package io.metaloom.graph.core.storage.data.impl;

public record RelationshipData(long fromId, long toId, String label, long[] propIds) {

}
