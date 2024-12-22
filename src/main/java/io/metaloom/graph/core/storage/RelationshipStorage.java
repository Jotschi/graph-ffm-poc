package io.metaloom.graph.core.storage;

import java.io.IOException;

public interface RelationshipStorage extends Storage {

	void store(long relId, long nodeAId, long nodeBId, String label) throws IOException;

	long[] load(long relId) throws IOException;

}
