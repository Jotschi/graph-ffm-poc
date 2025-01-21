package io.metaloom.graph.core.internal;

import java.io.IOException;
import java.nio.file.Path;

import io.metaloom.graph.core.internal.node.NodeStorage;
import io.metaloom.graph.core.internal.node.NodeStorageImpl;
import io.metaloom.graph.core.internal.prop.PropertyStorage;
import io.metaloom.graph.core.internal.prop.PropertyStorageImpl;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorage;
import io.metaloom.graph.core.internal.rel.NodeRelationshipStorageImpl;
import io.metaloom.graph.core.internal.rel.RelationshipStorage;
import io.metaloom.graph.core.internal.rel.RelationshipStorageImpl;

public class InternalStorageImpl implements InternalStorage {

	private final RelationshipStorageImpl relationshipStorage;

	private final NodeStorageImpl nodesStorage;

	private final PropertyStorageImpl propertyStorage;

	private final NodeRelationshipStorageImpl nodeRelsStorage;

	public InternalStorageImpl(Path nodesPath, Path relsPath, Path nodeRelsPath, Path propsPath) throws IOException {
		this.nodesStorage = new NodeStorageImpl(nodesPath);
		this.nodeRelsStorage = new NodeRelationshipStorageImpl(nodeRelsPath);
		this.relationshipStorage = new RelationshipStorageImpl(relsPath, this.nodeRelsStorage);
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
			nodeRelsStorage.close();
		} catch (Exception e) {
			throw new RuntimeException("Failure while closing node rels storage", e);
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

	@Override
	public NodeRelationshipStorage nodeRel() {
		return nodeRelsStorage;
	}
}
