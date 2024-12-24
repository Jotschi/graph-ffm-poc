package io.metaloom.graph.core.storage.data.impl;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import io.metaloom.graph.core.storage.data.DataStorage;
import io.metaloom.graph.core.storage.data.NodeDataStorage;
import io.metaloom.graph.core.storage.data.PropertyDataStorage;
import io.metaloom.graph.core.storage.data.RelationshipDataStorage;

public class DataStorageImpl implements DataStorage {

	private final RelationshipDataStorageImpl relationshipStorage;

	private final NodeDataStorageImpl nodesStorage;

	private final PropertyDataStorageImpl propertyStorage;

	public DataStorageImpl(Path nodesPath, Path relsPath, Path propsPath) throws FileNotFoundException {
		this.relationshipStorage = new RelationshipDataStorageImpl(relsPath);
		this.nodesStorage = new NodeDataStorageImpl(nodesPath);
		this.propertyStorage = new PropertyDataStorageImpl(propsPath);
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
		try {
			propertyStorage.close();
		} catch (Exception e) {
			throw new RuntimeException("Failure while closing property storage", e);
		}
	}

	@Override
	public RelationshipDataStorage rel() {
		return relationshipStorage;
	}

	@Override
	public NodeDataStorage node() {
		return nodesStorage;
	}

	@Override
	public PropertyDataStorage prop() {
		return propertyStorage;
	}
}
