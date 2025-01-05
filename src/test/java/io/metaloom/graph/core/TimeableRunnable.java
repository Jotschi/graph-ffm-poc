package io.metaloom.graph.core;

@FunctionalInterface
public interface TimeableRunnable {

	void invoke() throws Exception;
}
