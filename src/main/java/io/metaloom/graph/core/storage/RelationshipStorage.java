package io.metaloom.graph.core.storage;

import java.io.IOException;
import java.util.Set;

public interface RelationshipStorage {

	void store(long relId, long nodeAId, long nodeBId, String label) throws IOException;

	long[] load(long relId) throws IOException;

	void delete(long relId) throws IOException;

	Set<Long> getFreeIds();

}
