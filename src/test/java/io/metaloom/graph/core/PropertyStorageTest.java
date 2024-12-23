package io.metaloom.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.storage.data.PropertyDataStorage;
import io.metaloom.graph.core.storage.data.impl.PropertyDataStorageImpl;

public class PropertyStorageTest {

	private static final String LONG_TEXT = "This is a longer text 12345678";
	private Path path = Path.of("target", "data.mmap");

	@BeforeEach
	public void setup() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void testBasics() throws Exception {
		try (PropertyDataStorage st = new PropertyDataStorageImpl(path)) {
			long id = st.store("keyA", "value");
			long id2 = st.store("keyB", "valz");
			long id3 = st.store("k", LONG_TEXT);

			assertId(st, id, "keyA", "value");
			assertId(st, id2, "keyB", "valz");
			assertId(st, id3, "k", LONG_TEXT);
		}
	}

	private void assertId(PropertyDataStorage st, long id, String key, String value) throws IOException {
		String[] values = st.get(id);
		assertEquals(key, values[0]);
		assertEquals(value, values[1]);
	}
}
