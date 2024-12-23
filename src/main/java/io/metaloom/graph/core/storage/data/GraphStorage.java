package io.metaloom.graph.core.storage.data;

import java.io.IOException;

import io.metaloom.graph.core.element.Node;
import io.metaloom.graph.core.element.Relationship;

public interface GraphStorage extends AutoCloseable {

	long store(Relationship rel) throws IOException;

	long store(Node node);

	Relationship loadRelationship(long id) throws IOException;

}
