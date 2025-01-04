package io.metaloom.graph.core.element.impl;

import static io.metaloom.graph.core.utils.GraphUtils.propsToString;

import io.metaloom.graph.core.element.AbstractElement;
import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;

public class RelationshipImpl extends AbstractElement implements Relationship {

	private final Node to;

	private final Node from;

	public RelationshipImpl(Node from, String label, Node to) {
		super(label);
		this.from = from;
		this.to = to;
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
		return "(" + from.uuid() + ":" + from.label() + " " + propsToString(from.props()) + ")-[" + uuid() + ":" + label() + " " + propsToString(props())
			+ "]->("
			+ to.uuid() + ":" + to.label() + " " + propsToString(to.props()) + ")";
	}

}
