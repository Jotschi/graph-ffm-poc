package io.metaloom.graph.core.element;

import io.metaloom.graph.core.uuid.GraphUUID;

public interface Relationship extends Element {

	GraphUUID fromUuid();

	GraphUUID toUuid();

	Node to();

	Node from();

}
