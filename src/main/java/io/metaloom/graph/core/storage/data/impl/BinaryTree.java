package io.metaloom.graph.core.storage.data.impl;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class BinaryTree {

	private static Arena arena = Arena.ofAuto();

	private static final GroupLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("key"),
		ValueLayout.JAVA_LONG.withName("value"),
		ValueLayout.JAVA_LONG.withName("leftChildOffset"),
		ValueLayout.JAVA_LONG.withName("rightChildOffset"));

	// Allocate memory for a node
	private static MemorySegment allocateNode(long key, long value, long leftChildOffset, long rightChildOffset) {
		MemorySegment node = arena.allocate(NODE_LAYOUT.byteSize());
		NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("key")).set(node, 0, (long) key);
		NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("value")).set(node, 0, (long) value);
		NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("leftChildOffset")).set(node, 0, (long) leftChildOffset);
		NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rightChildOffset")).set(node, 0, (long) rightChildOffset);
		return node;
	}

	// Insert a new node into the BST
	private static long insertNode(long rootOffset, MemorySegment memory, long key, long value) {
		if (rootOffset == 0) {
			// Create a new root node
			MemorySegment newNode = allocateNode(key, value, 0, 0);
			return NODE_LAYOUT.byteSize();
		} else {
			// Compare the key with the root node's key
			long rootKey = memory.get(ValueLayout.JAVA_LONG, rootOffset);
			if (key < rootKey) {
				// Recursively insert into the left subtree
				long leftChildOffset = memory.get(ValueLayout.JAVA_LONG, rootOffset + 16);
				long newLeftChildOffset = insertNode(leftChildOffset, memory, key, value);
				memory.set(ValueLayout.JAVA_LONG, rootOffset + 16, newLeftChildOffset);
			} else if (key > rootKey) {
				// Recursively insert into the right subtree
				long rightChildOffset = memory.get(ValueLayout.JAVA_LONG, rootOffset + 24);
				long newRightChildOffset = insertNode(rightChildOffset, memory, key, value);
				memory.set(ValueLayout.JAVA_LONG, rootOffset + 24, newRightChildOffset);
			}
			return rootOffset;
		}
	}

	// Search for a node in the BST
	private static long searchNode(long rootOffset, MemorySegment memory, long key) {
		if (rootOffset == 0) {
			// Node not found
			return 0;
		} else {
			// Compare the key with the root node's key
			long rootKey = memory.get(ValueLayout.JAVA_LONG, rootOffset);
			if (key < rootKey) {
				// Recursively search in the left subtree
				long leftChildOffset = memory.get(ValueLayout.JAVA_LONG, rootOffset + 16);
				return searchNode(leftChildOffset, memory, key);
			} else if (key > rootKey) {
				// Recursively search in the right subtree
				long rightChildOffset = memory.get(ValueLayout.JAVA_LONG, rootOffset + 24);
				return searchNode(rightChildOffset, memory, key);
			} else {
				// Node found, return its value
				return memory.get(ValueLayout.JAVA_LONG, rootOffset + 8);
			}
		}
	}

	public static void main(String[] args) throws Throwable {
		// Allocate memory for the BST
		int numNodes = 100;
		MemorySegment memory = arena.allocate(numNodes * NODE_LAYOUT.byteSize());

		// Initialize the root offset to 0
		long rootOffset = 0;

		// Insert nodes into the BST
		for (int i = 0; i < numNodes; i++) {
			long key = (long) i * 10;
			long value = (long) i * 20;
			System.out.println("Inserting node " + key + " = " + value);
			rootOffset = insertNode(rootOffset, memory, key, value);
			System.out.println("New offset: " + rootOffset);
		}

		// Search for a node in the BST
		long key = 50L;
		long value = searchNode(rootOffset, memory, key);
		if (value != 0) {
			System.out.println("Value for key " + key + ": " + value);
		} else {
			System.out.println("Key " + key + " not found");
		}
	}
}
