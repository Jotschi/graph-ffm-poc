package io.metaloom.graph.core.element;

import java.util.Map;

public interface Element {

	String label();

	Long id();

	void setId(long id);

	Map<String, String> props();

	void set(String key, String value);

	String get(String key);

	void putAll(Map<String, String> map);
}
