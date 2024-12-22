package io.metaloom.graph.core;

public class Old {

	
//	public long storeNode(long id, String label) throws IOException {
//
//		FileChannel fc = file.getChannel();
//		long offset = id * NODE_LAYOUT.byteSize();
//
//		long value = 10;
//
//		VarHandle xHandle = NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id"));
//		MemorySegment segment = arena.allocate(NODE_LAYOUT);
//		// MemorySegment segment = fc.map(MapMode.READ_WRITE, offset, NODE_LAYOUT.byteSize(), arena);
//		xHandle.set(segment, 0, (long) value);
//		// long xValue = (long) xHandle.get(segment, 0);
//		// System.out.println("Read Value: " + xValue);
//		return segment.address();
//
//	}
}
