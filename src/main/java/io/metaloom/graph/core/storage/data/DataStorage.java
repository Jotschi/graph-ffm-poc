package io.metaloom.graph.core.storage.data;

import io.metaloom.graph.core.storage.node.NodeDataStorage;
import io.metaloom.graph.core.storage.prop.PropertyDataStorage;
import io.metaloom.graph.core.storage.rel.RelationshipDataStorage;

public interface DataStorage extends AutoCloseable {

	RelationshipDataStorage rel();

	NodeDataStorage node();

	PropertyDataStorage prop();

}
