package io.metaloom.graph.core.internal.rel;

import java.lang.foreign.MemorySegment;

public record ChainInfo(MemorySegment prev, MemorySegment current, long currentOffset) {

}
