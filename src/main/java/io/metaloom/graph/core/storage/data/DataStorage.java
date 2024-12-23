package io.metaloom.graph.core.storage.data;

public interface DataStorage extends AutoCloseable {

	RelationshipDataStorage rel();

	NodeDataStorage node();

	PropertyDataStorage prop();

}
