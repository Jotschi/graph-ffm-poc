package io.metaloom.graph.core.storage.impl;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import io.metaloom.graph.core.storage.GraphStorage;
import io.metaloom.graph.core.storage.NodeStorage;
import io.metaloom.graph.core.storage.RelationshipStorage;

public class GraphStorageImpl implements GraphStorage {

	private final RelationshipStorageImpl relationshipStorage;

	private final NodeStorageImpl nodesStorage;

	private final PropertyStorageImpl propertyStorage;

	public GraphStorageImpl(Path nodesPath, Path relsPath, Path propsPath) throws FileNotFoundException {
		this.relationshipStorage = new RelationshipStorageImpl(relsPath);
		this.nodesStorage = new NodeStorageImpl(nodesPath);
		this.propertyStorage = new PropertyStorageImpl(propsPath);
	}

	public void close() {
		try {
			relationshipStorage.close();
		} catch (Exception e) {
			throw new RuntimeException("Failure while closing relationship storage", e);
		}
		try {
			nodesStorage.close();
		} catch (Exception e) {
			throw new RuntimeException("Failure while closing nodes storage", e);
		}
	}

	public RelationshipStorage rel() {
		return relationshipStorage;
	}

	public NodeStorage node() {
		return nodesStorage;
	}
}
