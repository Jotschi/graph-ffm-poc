package io.metaloom.graph.core.storage.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

import io.metaloom.graph.core.storage.AbstractMMapFileStorage;
import io.metaloom.graph.core.storage.PropertyStorage;

public class PropertyStorageImpl extends AbstractMMapFileStorage implements PropertyStorage {

	private static long alignment = 7;
	// private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
	// ValueLayout.JAVA_LONG.withName("id"),
	// ValueLayout.JAVA_BOOLEAN.withName("free"));

	private AtomicLong nextFreeOffset = new AtomicLong();

	public PropertyStorageImpl(Path path) throws FileNotFoundException {
		super(path);

	}

	@Override
	public String[] get(long id) throws IOException {

		long fileSize = Files.size(path);
		try (FileChannel fileChannel = FileChannel.open(path,
			StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

			MemorySegment segment = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize, arena);
			long offset = id;
			return readRecord(segment, offset);
		}

	}

	@Override
	public long store(String key, String value) throws IOException {
		try (FileChannel fileChannel = FileChannel.open(path,
			StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

			long offset = nextFreeOffset.get();

			// ID + key len + key data + value len + value data
			long dataSize = 1 + 1 + key.length() + 1 + value.length();
			ensureFileCapacity(fileChannel, 0, dataSize);

			long fileSize = Files.size(path);
			fileSize = 512;
			System.out.println("SIZE: " + fileSize);
			MemorySegment segment = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize, arena);

			long nextOffset = writeRecord(segment, offset, 1L, key, value);
			nextFreeOffset.set(nextOffset);
			return offset;
		}
	}

	private static long writeRecord(MemorySegment segment, long offset, long id, String key, String value) {

		// Write ID
		System.out.println("WRITE: " + offset);
		segment.set(ValueLayout.JAVA_LONG, offset, id);
		offset += Long.BYTES;

		// Write key
		System.out.println("W KL: " + offset);
		segment.set(ValueLayout.JAVA_INT, offset, key.length());
		offset += Integer.BYTES;
		System.out.println("W KV: " + offset);
		segment.setString(offset, key, StandardCharsets.UTF_8);
		// segment.asSlice(offset, keyBytes.length).copyFrom(MemorySegment.ofArray(keyBytes));
		offset += key.length();

		offset = (offset + alignment) & ~alignment;

		// Write value
		System.out.println("W VL: " + offset);
		segment.set(ValueLayout.JAVA_INT, offset, value.length());
		offset += Integer.BYTES;
		System.out.println("W VV: " + offset);
		segment.setString(offset, value, StandardCharsets.UTF_8);
		// segment.asSlice(offset, valueBytes.length).copyFrom(MemorySegment.ofArray(valueBytes));
		offset += value.length();

		offset = (offset + alignment) & ~alignment;

		return offset;
	}

	private static String[] readRecord(MemorySegment segment, long offset) {
		System.out.println();
		System.out.println("READ[offset]: " + offset);
		// Read ID
		long id = segment.get(ValueLayout.JAVA_LONG, offset);
		offset += Long.BYTES;
		System.out.println("ReadID[id]: " + id);

		// Read key
		System.out.println("R KL: " + offset);
		int keyLength = segment.get(ValueLayout.JAVA_INT, offset);
		offset += Integer.BYTES;
		System.out.println("R KLV: " + keyLength);

		System.out.println("R KV: " + offset);
		MemorySegment slice = segment.asSlice(offset, keyLength);
		String key = new String(slice.toArray(ValueLayout.JAVA_BYTE));
		// System.out.println("SLICE: " + slice.getString(0));
		// String key = segment.getString(offset, StandardCharsets.UTF_8).substring(0, keyLength);
		offset += keyLength;

		offset = (offset + alignment) & ~alignment;

		// Read value
		System.out.println("R V: " + offset);
		int valueLength = segment.get(ValueLayout.JAVA_INT, offset);
		System.out.println("R VLV[len]: " + valueLength);
		offset += Integer.BYTES;
		// MemorySegment valueSegment = segment.asSlice(offset, valueLength);
		String value = segment.getString(offset, StandardCharsets.UTF_8).substring(0, valueLength);
		offset += valueLength;

		// Print the record
		System.out.printf("Record: [id=%d, key=%s, value=%s]%n", id, key, value);
		offset = (offset + alignment) & ~alignment;

		return new String[] { key, value };
	}

	@Override
	public void close() throws Exception {
		// try {
		// arena.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

}
