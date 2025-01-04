package io.metaloom.graph.core.storage.error;

import io.metaloom.graph.core.uuid.GraphUUID;

public class ElementStorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private GraphUUID uuid;

	public ElementStorageException(GraphUUID uuid, String msg) {
		super(msg);
		this.uuid = uuid;
	}

	public GraphUUID getUuid() {
		return uuid;
	}

}
