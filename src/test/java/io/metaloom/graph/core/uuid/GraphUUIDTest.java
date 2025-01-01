package io.metaloom.graph.core.uuid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class GraphUUIDTest {

	@Test
	public void testWithZero() {
		GraphUUID uuid = GraphUUID.uuid(0L);
		assertNotNull(uuid);
		assertEquals(0L, uuid.offset());
	}

	@Test
	public void testWithNonZero() {
		GraphUUID uuid = GraphUUID.uuid(Long.MAX_VALUE);
		assertNotNull(uuid);
		assertEquals(Long.MAX_VALUE, uuid.offset());
	}

	@Test
	public void testWithHalf() {
		long offset = Long.MAX_VALUE / 2;
		GraphUUID uuid = GraphUUID.uuid(offset);
		assertNotNull(uuid);
		assertEquals(offset, uuid.offset());
	}
	

	@Test
	public void testWithInt() {
		long offset = Integer.MAX_VALUE;
		GraphUUID uuid = GraphUUID.uuid(offset);
		assertNotNull(uuid);
		assertEquals(offset, uuid.offset());
	}
}
