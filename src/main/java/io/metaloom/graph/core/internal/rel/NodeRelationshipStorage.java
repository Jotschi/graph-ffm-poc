package io.metaloom.graph.core.internal.rel;

import java.io.IOException;
import java.util.List;

import io.metaloom.graph.core.internal.FileHeader;

public interface NodeRelationshipStorage extends AutoCloseable {

	public static final long NO_NEXT_SEGMENT = -1;

	/**
	 * Create a new node<->relationship connection.
	 * 
	 * @param startOffset
	 * @param relationshipOffset
	 * @param nodeOffset
	 * @return
	 * @throws IOException
	 */
	long create(long startOffset, long relationshipOffset, long nodeOffset) throws IOException;

	/**
	 * Load all internal relationship references from the given offset.
	 * 
	 * @param startOffset
	 * @return
	 */
	List<RelationshipReferenceInternal> load(long startOffset) throws IOException;

	/**
	 * Delete the relationship from the chain at the given start location.
	 * 
	 * @param startOffset
	 * @param relationshipOffset
	 * @throws IOException
	 */
	void deleteByRelOffset(long startOffset, long relationshipOffset) throws IOException;

	FileHeader header();

}
