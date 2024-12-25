package io.metaloom.graph.core.ffm;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StringStorage {

	private static long alignment = 7;

	public static void main(String[] args) throws IOException {
		Path filePath = Path.of("target", "data.mmap");
		Files.deleteIfExists(filePath);

		Arena session = Arena.ofAuto();
		try (FileChannel fileChannel = FileChannel.open(filePath,
			StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

			// Initialize memory-mapped file
			long fileSize = 512; // 1 MB initial size
			fileChannel.truncate(fileSize);
			MemorySegment segment = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize, session);

			// Write records
			long offset1 = writeRecord(segment, 0, 1L, "keya", "valu");
			System.out.println(offset1);
			long offset2 = writeRecord(segment, offset1, 2L, "keyb", "vals");
			long offset3 = writeRecord(segment, offset2, 2L, "key", "value");

			// Read records
			readRecord(segment, 0);
			readRecord(segment, offset1);
			readRecord(segment, offset2);

		} catch (IOException e) {
			e.printStackTrace();
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

	private static long readRecord(MemorySegment segment, long offset) {
		System.out.println();
		System.out.println("READ: " + offset);
		// Read ID
		long id = segment.get(ValueLayout.JAVA_LONG, offset);
		offset += Long.BYTES;
		System.out.println("ReadID: " + id);

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
		System.out.println("R VLV: " + valueLength);
		offset += Integer.BYTES;
		// MemorySegment valueSegment = segment.asSlice(offset, valueLength);
		String value = segment.getString(offset, StandardCharsets.UTF_8).substring(0, valueLength);
		offset += valueLength;

		// Print the record
		System.out.printf("Record: [id=%d, key=%s, value=%s]%n", id, key, value);
		offset = (offset + alignment) & ~alignment;

		return offset;
	}

}
