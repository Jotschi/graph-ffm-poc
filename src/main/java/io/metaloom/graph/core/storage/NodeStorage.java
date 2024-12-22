package io.metaloom.graph.core.storage;

import java.io.IOException;

public interface NodeStorage {

	void store(long id, long y) throws IOException;

}
