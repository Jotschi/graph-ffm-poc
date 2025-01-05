package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.element.impl.NodeImpl;
import io.metaloom.graph.core.element.impl.RelationshipImpl;
import io.metaloom.graph.core.internal.InternalStorageImpl;
import io.metaloom.graph.core.internal.node.NodeInternal;
import io.metaloom.graph.core.internal.rel.RelationshipInternal;
import io.metaloom.graph.core.uuid.GraphUUID;

public class GraphStorageImpl implements GraphStorage {

	private static final String NODES_FILENAME = "nodes.bin";

	private static final String RELS_FILENAME = "relationships.bin";

	private static final String PROPS_FILENAME = "properties.bin";

	private final InternalStorageImpl data;

	public GraphStorageImpl(Path basePath) throws IOException {
		Path nodesPath = basePath.resolve(NODES_FILENAME);
		Path relsPath = basePath.resolve(RELS_FILENAME);
		Path propsPath = basePath.resolve(PROPS_FILENAME);
		this.data = new InternalStorageImpl(nodesPath, relsPath, propsPath);
	}

	@Override
	public void close() throws Exception {
		if (data != null) {
			data.close();
		}
	}

	@Override
	public Node readNode(GraphUUID uuid) throws IOException {
		NodeInternal nodeData = data.node().read(uuid);
		if (nodeData == null) {
			return null;
		}
		Node node = new NodeImpl(nodeData.label());
		node.putAll(data.prop().getAll(nodeData.propIds()));
		node.setUuid(uuid);
		return node;
	}

	@Override
	public Relationship readRelationship(GraphUUID uuid) throws IOException {
		RelationshipInternal relData = data.rel().read(uuid);

		// FROM
		GraphUUID fromUuid = relData.fromId();
		NodeInternal fromData = data.node().read(fromUuid);
		Node from = new NodeImpl(fromData.label());
		from.putAll(data.prop().getAll(fromData.propIds()));
		from.setUuid(fromUuid);

		// TO
		GraphUUID toUuid = relData.toId();
		NodeInternal toData = data.node().read(toUuid);
		Node to = new NodeImpl(toData.label());
		to.putAll(data.prop().getAll(toData.propIds()));
		to.setUuid(toUuid);

		// REL
		String label = relData.label();
		Relationship rel = new RelationshipImpl(from, label, to);
		rel.setUuid(relData.uuid());
		rel.putAll(data.prop().getAll(relData.propIds()));
		return rel;
	}

	@Override
	public Set<Relationship> traverse(GraphUUID fromUuid, int maxDepth) throws IOException {
		long[] relIds = data.rel().loadRelationshipIds(fromUuid);
		Set<Relationship> relationships = new HashSet<>();
		for (int i = 0; i < relIds.length; i++) {
			long relId = relIds[i];
			GraphUUID relUuid = GraphUUID.uuid(relId);
			relationships.add(readRelationship(relUuid));
		}
		return relationships;
	}

	@Override
	public GraphUUID create(Node node) throws IOException {
		long propIds[] = data.prop().store(node.props());
		data.prop().store(node.props());
		NodeInternal nodeData = data.node().create(node.label(), propIds);
		// node.setUuid(nodeData.uuid());
		return nodeData.uuid();
	}

	@Override
	public GraphUUID create(Relationship rel) throws IOException {
		// Node nodeA = rel.from();
		// // Store Node A
		// if (nodeA.uuid() == null) {
		// long propIds[] = data.prop().store(nodeA.props());
		// data.prop().store(nodeA.props());
		// NodeInternal nodeData = data.node().create(nodeA.label(), propIds);
		// nodeA.setUuid(nodeData.uuid());
		// }
		//
		// // Store Node B
		// Node nodeB = rel.to();
		// if (nodeB.uuid() == null) {
		// long propIds[] = data.prop().store(nodeB.props());
		// NodeInternal nodeData = data.node().create(nodeB.label(), propIds);
		// nodeB.setUuid(nodeData.uuid());
		// }

		if (rel.uuid() == null) {
			String label = rel.label();
			long propIds[] = data.prop().store(rel.props());
			RelationshipInternal relData = data.rel().create(rel.fromUuid(), label, rel.toUuid(), propIds);
			rel.setUuid(relData.uuid());
		}

		return rel.uuid();
	}
}
