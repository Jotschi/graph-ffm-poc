package io.metaloom.graph.core.storage.data.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.element.impl.NodeImpl;
import io.metaloom.graph.core.element.impl.RelationshipImpl;
import io.metaloom.graph.core.storage.data.GraphStorage;

public class GraphStorageImpl implements GraphStorage {

	private static final String NODES_FILENAME = "nodes.bin";

	private static final String RELS_FILENAME = "relationships.bin";

	private static final String PROPS_FILENAME = "properties.bin";

	private final DataStorageImpl data;

	public GraphStorageImpl(Path basePath) throws FileNotFoundException {
		Path nodesPath = basePath.resolve(NODES_FILENAME);
		Path relsPath = basePath.resolve(RELS_FILENAME);
		Path propsPath = basePath.resolve(PROPS_FILENAME);
		this.data = new DataStorageImpl(nodesPath, relsPath, propsPath);
	}

	@Override
	public void close() throws Exception {
		if (data != null) {
			data.close();
		}
	}

	@Override
	public Relationship loadRelationship(long id) throws IOException {
		RelationshipData relData = data.rel().load(id);

		// FROM
		long fromId = relData.fromId();
		NodeData fromData = data.node().load(fromId);
		Node from = new NodeImpl(fromData.label());
		from.putAll(data.prop().getAll(fromData.propIds()));
		from.setId(fromId);

		// TO
		long toId = relData.toId();
		NodeData toData = data.node().load(toId);
		Node to = new NodeImpl(toData.label());
		to.putAll(data.prop().getAll(toData.propIds()));
		to.setId(toId);

		// REL
		String label = relData.label();
		RelationshipImpl rel = new RelationshipImpl(from, label, to);
		rel.setId(id);
		rel.putAll(data.prop().getAll(relData.propIds()));
		return rel;
	}

	@Override
	public Set<Relationship> loadRelationships(long fromId) throws IOException {
		long[] relIds = data.rel().loadRelationshipIds(fromId);
		Set<Relationship> relationships = new HashSet<>();
		for (int i = 0; i < relIds.length; i++) {
			relationships.add(loadRelationship(relIds[i]));
		}
		return relationships;
	}

	@Override
	public long store(Relationship rel) throws IOException {
		Node nodeA = rel.from();
		if (nodeA.id() == null) {
			long id = data.node().id();
			long propIds[] = data.prop().store(nodeA.props());
			data.node().store(id, nodeA.label(), propIds);
			nodeA.setId(id);
		}
		Node nodeB = rel.to();
		if (nodeB.id() == null) {
			long id = data.node().id();
			long propIds[] = data.prop().store(nodeB.props());
			data.node().store(id, nodeB.label(), propIds);
			nodeB.setId(id);
		}

		if (rel.id() == null) {
			String label = rel.label();
			long id = data.rel().id();
			long propIds[] = data.prop().store(rel.props());
			data.rel().store(id, nodeA.id(), nodeB.id(), label, propIds);
			rel.setId(id);
		}

		return rel.id();
	}
}
