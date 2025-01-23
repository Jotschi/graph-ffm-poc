package io.metaloom.graph.core.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;

public class FFMLibraryTest {

	@Test
	public void testFunctionCall() throws Throwable {
		// Path to the shared library (update with the correct path)
		String libPath = "native-rust-lib/target/release/librust_lib.so";

		// Load the native library
		Arena session = Arena.ofAuto();
		// SymbolLookup library = SymbolLookup.loaderLookup().orElseThrow();
		SymbolLookup rustLibrary = SymbolLookup.libraryLookup(libPath, session);

		// Lookup the function
		MethodHandle addNumbersHandle = Linker.nativeLinker()
			.downcallHandle(
				rustLibrary.findOrThrow("add_numbers"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

		// Call the Rust function
		int result = (int) addNumbersHandle.invokeExact(5, 7);
		System.out.println("Result of add_numbers(5, 7): " + result);

	}
}
