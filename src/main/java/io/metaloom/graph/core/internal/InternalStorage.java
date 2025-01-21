package io.metaloom.graph.core.internal;

import io.metaloom.graph.core.internal.node.NodeStorage;
import io.metaloom.graph.core.internal.prop.PropertyStorage;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorage;
import io.metaloom.graph.core.internal.rel.RelationshipStorage;

public interface InternalStorage extends AutoCloseable {

	RelationshipStorage rel();

	NodeStorage node();

	PropertyStorage prop();

	NodeRelationshipStorage nodeRel();

}
