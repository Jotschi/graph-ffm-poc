package io.metaloom.graph.core.storage.data;

import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

import io.metaloom.graph.core.uuid.GraphUUID;

public class AbstractGraphStorage extends AbstractElementStorage implements ElementStorage {

	protected static final String LABEL_KEY = "label";

	protected static final String PROPS_KEY = "props";

	protected static final String FREE_KEY = "free";

	public static final int MAX_LABEL_LEN = 32;

	public static final int MAX_PROP_IDS = 128;

	protected final MemoryLayout layout;

	protected final Deque<Long> freeOffsets = new ArrayDeque<>();

	protected final VarHandle labelHandle;

	protected final VarHandle propsHandle;

	protected final AtomicLong offsetProvider;
	
	public AbstractGraphStorage(Path path, MemoryLayout layout, String magicByte) throws IOException {
		super(path, magicByte);
		this.layout = layout;

		this.labelHandle = layout.varHandle(
			MemoryLayout.PathElement.groupElement(LABEL_KEY),
			MemoryLayout.PathElement.sequenceElement());

		this.propsHandle = layout.varHandle(
			MemoryLayout.PathElement.groupElement(PROPS_KEY),
			MemoryLayout.PathElement.sequenceElement());

		try {
			long lastOffset = loadFreeOffsets(layout);
			this.offsetProvider = new AtomicLong(lastOffset);
		} catch (Exception e) {
			throw new RuntimeException("Error while loading free ids", e);
		}
	}
	
	protected void writePropIds(MemorySegment segment, long propIds[]) {
		if (propIds == null) {
			return;
		}

		for (int i = 0; i < propIds.length; i++) {
			propsHandle.set(segment, 0, (long) i, propIds[i]);
		}

		// Pad the remaining space with -1
		for (int i = propIds.length; i < 32; i++) {
			propsHandle.set(segment, 0, (long) i, (byte) -1);
		}

	}

	public void writeLabel(MemorySegment segment, String label) {
		byte[] bytes = label.getBytes(StandardCharsets.UTF_8);
		if (bytes.length > MAX_LABEL_LEN) {
			throw new IllegalArgumentException("Label exceeds " + MAX_LABEL_LEN + " bytes when encoded.");
		}

		// System.out.println("Writing: " + label);

		// Write the label bytes into the sequence
		for (int i = 0; i < bytes.length; i++) {
			labelHandle.set(segment, 0, (long) i, bytes[i]);
		}

		// Pad the remaining space with 0s
		for (int i = bytes.length; i < 32; i++) {
			labelHandle.set(segment, 0, (long) i, (byte) 0);
		}
	}

	public String readLabel(MemorySegment segment) {
		byte[] labelBytes = new byte[MAX_LABEL_LEN];
		for (int i = 0; i < labelBytes.length; i++) {
			labelBytes[i] = (byte) labelHandle.get(segment, 0, i);
			if (labelBytes[i] == 0) {
				break;
			}
		}
		return new String(labelBytes, StandardCharsets.UTF_8).trim();
	}

	public long[] readPropIds(MemorySegment segment) {
		long[] ids = new long[MAX_PROP_IDS];
		int nValues = 0;
		for (int i = 0; i < MAX_PROP_IDS; i++) {
			ids[i] = (long) propsHandle.get(segment, 0, i);
			if (ids[i] == -1) {
				long result[] = new long[nValues];
				System.arraycopy(ids, 0, result, 0, nValues);
				return result;
			}
			nValues++;
		}
		return ids;
	}

	@Override
	public void delete(GraphUUID uuid) throws IOException {
		// Calculate the offset
		long offset = uuid.offset();
		if (offset > raFile.length()) {
			return;
		}

		// Map the memory segment
		FileChannel fc = raFile.getChannel();
		MemorySegment memorySegment = fc.map(MapMode.READ_WRITE, offset, layout.byteSize(), arena);
		layout.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).set(memorySegment, 0, true);
		freeOffsets.add(uuid.offset());
	}

	private long loadFreeOffsets(MemoryLayout layout) throws IOException {
		if (raFile.length() == 0) {
			return HEADER_LAYOUT.byteSize();
		}

		try (FileChannel fc = raFile.getChannel()) {
			long lastOffset = HEADER_LAYOUT.byteSize();
			for (long offset = HEADER_LAYOUT.byteSize(); offset < raFile.length(); offset += layout.byteSize()) {
				MemorySegment memorySegment = fc.map(MapMode.READ_ONLY, offset, layout.byteSize(), arena);
				boolean free = (boolean) layout.varHandle(MemoryLayout.PathElement.groupElement(FREE_KEY)).get(memorySegment, 0);
				lastOffset = offset / layout.byteSize();
				if (free) {
					freeOffsets.add(lastOffset);
				}
			}
			return lastOffset;
		}
	}

	@Override
	public long nextOffset() {
		if (freeOffsets.isEmpty()) {
			return offsetProvider.get();
		} else {
			return freeOffsets.pop();
		}
	}

	@Override
	public Deque<Long> getFreeIds() {
		return freeOffsets;
	}

}
