package io.metaloom.graph.core.storage.data;

import java.io.IOException;

import io.metaloom.graph.core.storage.data.impl.RelationshipData;

public interface RelationshipDataStorage extends ElementDataStorage {

	void store(long relId, long nodeAId, long nodeBId, String label, long propIds[]) throws IOException;

	RelationshipData load(long relId) throws IOException;

	long[] loadRelationshipIds(long fromId);

}
