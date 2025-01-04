package io.metaloom.graph.core.element;

import java.util.Map;

import io.metaloom.graph.core.uuid.GraphUUID;

public interface Element {

	String label();

	GraphUUID uuid();

	void setUuid(GraphUUID uuid);

	Map<String, String> props();

	void set(String key, String value);

	String get(String key);

	void putAll(Map<String, String> map);
}
