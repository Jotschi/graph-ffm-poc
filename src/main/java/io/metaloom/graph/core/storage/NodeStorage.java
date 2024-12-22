package io.metaloom.graph.core.storage;

import java.io.IOException;

public interface NodeStorage extends Storage {

	void store(long id, String label) throws IOException;

}
