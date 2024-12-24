package io.metaloom.graph.core.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class GraphUtils {

	public static String propsToString(Map<String, String> props) {
		StringBuilder b = new StringBuilder();

		Iterator<Entry<String, String>> it = props.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			b.append(entry.getKey() + ": '" + entry.getValue() + "'");
			if (it.hasNext()) {
				b.append(", ");
			}
		}
		return "{" + b.toString() + "}";
	}

}
