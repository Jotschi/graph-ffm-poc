package io.metaloom.graph.core.uuid;

import java.util.Random;
import java.util.UUID;

public class GraphUUID {

	private static final Random RND = new Random();

	private final UUID uuid;

	public GraphUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public static GraphUUID uuid(long offset) {
		long random = RND.nextLong();
		UUID uuid = createUUID(offset, random);
		return new GraphUUID(uuid);
	}

	public static GraphUUID from(long offset, long randomValue) {
		return new GraphUUID(createUUID(offset, randomValue));
	}

	/**
	 * Creates a UUID by intertwining bits from value and randomValue.
	 * 
	 * @param offset
	 * @param randomValue
	 * @return
	 */
	private static UUID createUUID(long offset, long randomValue) {
		long mostSignificantBits = intertwineBits(offset >>> 32, randomValue >>> 32);
		long leastSignificantBits = intertwineBits(offset & 0xFFFFFFFFL, randomValue & 0xFFFFFFFFL);
		return new UUID(mostSignificantBits, leastSignificantBits);
	}

	public long offset() {
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		return (extractOddBits(mostSignificantBits) << 32) | extractOddBits(leastSignificantBits);
	}

	public long randomValue() {
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		return (extractEvenBits(mostSignificantBits) << 32) | extractEvenBits(leastSignificantBits);
	}

	private static long intertwineBits(long value, long randomValue) {
		long intertwined = 0;
		for (int i = 0; i < 32; i++) {
			intertwined |= ((value >>> i) & 1L) << (2 * i + 1);
			intertwined |= ((randomValue >>> i) & 1L) << (2 * i);
		}
		return intertwined;
	}

	private static long extractOddBits(long intertwined) {
		long value = 0;
		for (int i = 0; i < 32; i++) {
			value |= ((intertwined >>> (2 * i + 1)) & 1L) << i;
		}
		return value;
	}

	private static long extractEvenBits(long intertwined) {
		long randomValue = 0;
		for (int i = 0; i < 32; i++) {
			randomValue |= ((intertwined >>> (2 * i)) & 1L) << i;
		}
		return randomValue;
	}

	@Override
	public String toString() {
		return uuid.toString();
	}

}
