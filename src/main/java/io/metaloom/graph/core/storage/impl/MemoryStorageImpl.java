package io.metaloom.graph.core.storage.impl;

import java.io.File;
import java.io.FileNotFoundException;

import io.metaloom.graph.core.storage.NodeStorage;
import io.metaloom.graph.core.storage.RelationshipStorage;

public class MemoryStorageImpl implements AutoCloseable {

	private RelationshipStorageImpl relationshipStorage;

	private NodeStorageImpl nodesStorage;

	public MemoryStorageImpl(File nodesFile, File relsFile) throws FileNotFoundException {
		this.relationshipStorage = new RelationshipStorageImpl(relsFile);
		this.nodesStorage = new NodeStorageImpl(nodesFile);
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
