package io.metaloom.graph.core.internal.rel;

import java.io.IOException;
import java.util.List;

public interface NodeRelationshipStorage extends AutoCloseable {

	long create(long startOffset, long relationshipOffset, long nodeOffset) throws IOException;

	/**
	 * Load all internal relationship references from the given offset.
	 * 
	 * @param startOffset
	 * @return
	 */
	List<RelationshipReferenceInternal> load(long startOffset) throws IOException;

}
