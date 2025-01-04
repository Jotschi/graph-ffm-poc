package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.nio.file.Path;

import io.metaloom.graph.core.storage.node.NodeStorage;
import io.metaloom.graph.core.storage.node.NodeStorageImpl;
import io.metaloom.graph.core.storage.prop.PropertyStorage;
import io.metaloom.graph.core.storage.prop.PropertyStorageImpl;
import io.metaloom.graph.core.storage.rel.RelationshipStorage;
import io.metaloom.graph.core.storage.rel.RelationshipStorageImpl;

public class DataStorageImpl implements DataStorage {

	private final RelationshipStorageImpl relationshipStorage;

	private final NodeStorageImpl nodesStorage;

	private final PropertyStorageImpl propertyStorage;

	public DataStorageImpl(Path nodesPath, Path relsPath, Path propsPath) throws IOException {
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
		try {
			propertyStorage.close();
		} catch (Exception e) {
			throw new RuntimeException("Failure while closing property storage", e);
		}
	}

	@Override
	public RelationshipStorage rel() {
		return relationshipStorage;
	}

	@Override
	public NodeStorage node() {
		return nodesStorage;
	}

	@Override
	public PropertyStorage prop() {
		return propertyStorage;
	}
}
