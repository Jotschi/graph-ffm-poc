package io.metaloom.graph.core.storage.rel;

public record RelationshipData(long fromId, long toId, String label, long[] propIds) {

}
