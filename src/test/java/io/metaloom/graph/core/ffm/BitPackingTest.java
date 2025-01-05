package io.metaloom.graph.core.ffm;

import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class BitPackingTest {

	private static final Random RND = new Random();

	@Test
	public void testIdPacking() {

		long value = Long.MAX_VALUE;
		long randomValue = new Random().nextLong(); // Generate a random long value

		// Create a UUID using bitpacking
		UUID uuid = createUUID(value, randomValue);
		System.out.println("Generated UUID: " + uuid);

		// Unpack the UUID back into the long values
		long unpackedValue = unpackValue(uuid);

		System.out.printf("Original Value: 0x%X, Unpacked Value: 0x%X\n", value, unpackedValue);
	}

	// Creates a UUID by intertwining bits from value and randomValue
	public static UUID createUUID(long value, long randomValue) {
		long mostSignificantBits = intertwineBits(value >>> 32, randomValue >>> 32);
		long leastSignificantBits = intertwineBits(value & 0xFFFFFFFFL, randomValue & 0xFFFFFFFFL);
		return new UUID(mostSignificantBits, leastSignificantBits);
	}

	// Unpack the original value from the UUID
	public static long unpackValue(UUID uuid) {
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		return (extractOddBits(mostSignificantBits) << 32) | extractOddBits(leastSignificantBits);
	}

	// Unpack the random value from the UUID
	public static long unpackRandomValue(UUID uuid) {
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		return (extractEvenBits(mostSignificantBits) << 32) | extractEvenBits(leastSignificantBits);
	}

	// Intertwines bits from two 32-bit values
	private static long intertwineBits(long value, long randomValue) {
		long intertwined = 0;
		for (int i = 0; i < 32; i++) {
			intertwined |= ((value >>> i) & 1L) << (2 * i + 1);
			intertwined |= ((randomValue >>> i) & 1L) << (2 * i);
		}
		return intertwined;
	}

	// Extracts odd bits (original value) from intertwined bits
	private static long extractOddBits(long intertwined) {
		long value = 0;
		for (int i = 0; i < 32; i++) {
			value |= ((intertwined >>> (2 * i + 1)) & 1L) << i;
		}
		return value;
	}

	// Extracts even bits (random value) from intertwined bits
	private static long extractEvenBits(long intertwined) {
		long randomValue = 0;
		for (int i = 0; i < 32; i++) {
			randomValue |= ((intertwined >>> (2 * i)) & 1L) << i;
		}
		return randomValue;
	}
}
