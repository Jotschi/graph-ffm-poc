package io.metaloom.graph.core.element;

import java.util.HashMap;
import java.util.Map;

public class AbstractElement implements Element {

	private Long id;

	private final String label;

	private final Map<String, String> props = new HashMap<>();

	public AbstractElement(String label) {
		this.label = label;
	}

	@Override
	public Long id() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public Map<String, String> props() {
		return props;
	}

	@Override
	public void putAll(Map<String, String> map) {
		props().putAll(map);
	}

	@Override
	public void set(String key, String value) {
		props.put(key, value);

	}

	@Override
	public String get(String key) {
		return props.get(key);
	}

	@Override
	public String label() {
		return label;
	}

}
