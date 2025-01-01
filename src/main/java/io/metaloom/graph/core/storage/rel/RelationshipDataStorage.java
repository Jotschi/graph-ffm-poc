package io.metaloom.graph.core.storage.rel;

import java.io.IOException;

import io.metaloom.graph.core.storage.data.ElementStorage;

public interface RelationshipDataStorage extends ElementStorage {

	void store(long relId, long nodeAId, long nodeBId, String label, long propIds[]) throws IOException;

	RelationshipData load(long relId) throws IOException;

	long[] loadRelationshipIds(long fromId);

}
