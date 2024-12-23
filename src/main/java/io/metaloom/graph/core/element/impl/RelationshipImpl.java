package io.metaloom.graph.core.element.impl;

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

}
