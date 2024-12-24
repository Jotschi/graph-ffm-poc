package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.utils.GraphUtils;

public class GraphUtilsTest {

	@Test
	public void toStringTest() {
		Map<String, String> props = new HashMap<>();
		props.put("a", "b");
		props.put("c", "d");
		String str = GraphUtils.propsToString(props);
		assertEquals("{a: 'b', c: 'd'}", str);
	}
}
