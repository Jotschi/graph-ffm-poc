package io.metaloom.graph.core;

@FunctionalInterface
public interface TimeableAction<T> {

	T invoke() throws Exception;
}
