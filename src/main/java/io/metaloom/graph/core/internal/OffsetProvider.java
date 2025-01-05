package io.metaloom.graph.core.internal;

import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffsetProvider {

	private static Logger logger = LoggerFactory.getLogger(OffsetProvider.class);

	protected final AtomicLong elementCount = new AtomicLong(0);

	private final FileHeader header;

	private final MemoryLayout layout;

	protected final Deque<Long> freeOffsets = new ArrayDeque<>();

	public OffsetProvider(FileHeader header, MemoryLayout elementLayout) throws IOException {
		this.header = header;
		this.layout = elementLayout;

		// Load the initial count
		elementCount.set(header.loadCount());
	}

	/**
	 * Return the next free offset.
	 * 
	 * @param fc
	 * @return
	 */
	public long next(FileChannel fc) throws IOException {
		long offset = nextOffset();
		header.setCount(fc, elementCount.get());

		// Ensure the file is large enough
		ensureFileCapacity(fc, offset, layout);

		return offset;
	}

	public long getElementCount() {
		return elementCount.get();
	}

	public Deque<Long> getFreeOffsets() {
		return freeOffsets;
	}

	public void add(long offset) {
		freeOffsets.add(offset);
	}

	private long nextOffset() {
		if (freeOffsets.isEmpty()) {
			return elementCount.getAndIncrement() * layout.byteSize() + header.size();
		} else {
			logger.debug("Selecting free offset from queue. Currently {} offsets free", freeOffsets.size());
			return freeOffsets.pop();
		}
	}

	protected void ensureFileCapacity(FileChannel fc, long offset, MemoryLayout layout) throws IOException {
		ensureFileCapacity(fc, offset, layout.byteSize());
	}

	private void ensureFileCapacity(FileChannel fc, long offset, long size) throws IOException {
		if (fc.size() < offset + size) {
			// Write zeros to extend the file
			byte[] zeros = new byte[(int) (offset + size - fc.size())];
			fc.position(fc.size());
			fc.write(ByteBuffer.wrap(zeros));
			// System.out.println("Adding: " + zeros.length + " bytes to the file");
		}
	}

}
