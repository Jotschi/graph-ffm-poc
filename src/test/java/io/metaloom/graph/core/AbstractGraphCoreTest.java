package io.metaloom.graph.core;

public class AbstractGraphCoreTest {

	protected <T> T measure(TimeableAction<T> action) throws Exception {
		long start = System.currentTimeMillis();
		T ret = action.invoke();
		System.out.println("Duration: " + (System.currentTimeMillis() - start));
		return ret;
	}

	protected void measure(TimeableRunnable runnable) throws Exception {
		long start = System.currentTimeMillis();
		runnable.invoke();
		System.out.println("Duration: " + (System.currentTimeMillis() - start));
	}
}
