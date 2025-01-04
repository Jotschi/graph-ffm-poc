package io.metaloom.graph.core.storage.data;

import io.metaloom.graph.core.storage.node.NodeStorage;
import io.metaloom.graph.core.storage.prop.PropertyStorage;
import io.metaloom.graph.core.storage.rel.RelationshipStorage;

public interface DataStorage extends AutoCloseable {

	RelationshipStorage rel();

	NodeStorage node();

	PropertyStorage prop();

}
