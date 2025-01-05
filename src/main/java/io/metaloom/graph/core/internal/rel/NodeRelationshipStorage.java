package io.metaloom.graph.core.internal.rel;

import java.io.IOException;

public interface NodeRelationshipStorage extends AutoCloseable {

	void create(long nodeOffset, long relationshipOffset) throws IOException;

}
