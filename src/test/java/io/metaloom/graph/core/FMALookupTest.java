package io.metaloom.graph.core;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.junit.jupiter.api.Test;

public class FMALookupTest {

	@Test
	public void testLookup() {
		Arena arena = Arena.ofAuto();
		// Allocate memory for a long
		MemorySegment segment = arena.allocate(8);

		// Write a long value to the memory
		segment.set(ValueLayout.JAVA_LONG, 0, 123L);

		// Read the long value from the memory
		long value = segment.get(ValueLayout.JAVA_LONG , 0);
		System.out.println("Value: " + value);

	}
}
