package io.metaloom.graph.core.element.impl;

import static io.metaloom.graph.core.utils.GraphUtils.propsToString;

import io.metaloom.graph.core.element.AbstractElement;
import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;
import io.metaloom.graph.core.uuid.GraphUUID;

public class RelationshipImpl extends AbstractElement implements Relationship {

	private Node to;

	private Node from;

	private GraphUUID fromUuid;

	private GraphUUID toUuid;

	public RelationshipImpl(Node from, String label, Node to) {
		super(label);
		this.from = from;
		this.to = to;
	}

	public RelationshipImpl(GraphUUID fromUuid, String label, GraphUUID toUuid) {
		super(label);
		this.fromUuid = fromUuid;
		this.toUuid = toUuid;
	}

	@Override
	public GraphUUID fromUuid() {
		if (from != null) {
			return from.uuid();
		} else {
			return fromUuid;
		}
	}

	@Override
	public GraphUUID toUuid() {
		if (to != null) {
			return to.uuid();
		} else {
			return toUuid;
		}
	}

	@Override
	public Node to() {
		return to;
	}

	@Override
	public Node from() {
		return from;
	}

	@Override
	public String toString() {
		return "(" + from.uuid() + ":" + from.label() + " " + propsToString(from.props()) + ")-[" + uuid() + ":" + label() + " "
			+ propsToString(props())
			+ "]->("
			+ to.uuid() + ":" + to.label() + " " + propsToString(to.props()) + ")";
	}

}
