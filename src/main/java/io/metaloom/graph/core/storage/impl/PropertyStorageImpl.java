package io.metaloom.graph.core.storage.impl;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

public class PropertyStorageImpl {
	
	private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("id"),
		ValueLayout.JAVA_BOOLEAN.withName("free"));
	
	public void store(long id,  String key, String value) {
		
	}

}
