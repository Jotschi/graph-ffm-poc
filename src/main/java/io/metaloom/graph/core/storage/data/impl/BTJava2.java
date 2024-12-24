package io.metaloom.graph.core.storage.data.impl;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

public class BTJava2 {

	private static final GroupLayout NODE_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("key"),
		ValueLayout.JAVA_LONG.withName("value"),
		ValueLayout.JAVA_LONG.withName("leftChildOffset"),
		ValueLayout.JAVA_LONG.withName("rightChildOffset"));

	private Arena arena = Arena.ofAuto();

	class Node {
		private MemorySegment left;
		private MemorySegment right;
		private MemorySegment segment;

		public Node(long key, long value) {
			this.segment = arena.allocate(NODE_LAYOUT);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("key")).set(segment, 0, (long) key);
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("value")).set(segment, 0, (long) value);
			this.left = null;
			this.right = null;
		}

		public Node(MemorySegment segment) {
			Objects.requireNonNull(segment);
			this.segment = segment;
		}

		public long getKey() {
			return (long) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("key")).get(segment, 0);
		}

		public long getValue() {
			return (long) NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("value")).get(segment, 0);
		}

		public void setValue(long value) {
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("value")).set(segment, 0, (long) value);
		}

		public Node getLeft() {
			if (left == null) {
				return null;
			}
			return new Node(left);
		}

		public Node getRight() {
			if (right == null) {
				return null;
			}
			return new Node(right);
		}

		public void setLeft(Node node) {
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("leftChildOffset")).set(segment, 0, (long) node.segment.address());
			left = node.segment;
		}

		public void setRight(Node node) {
			NODE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rightChildOffset")).set(segment, 0, (long) node.segment.address());
			right = node.segment;
		}
	}

	Node root;

	// Constructor
	public BTJava2() {
		this.root = null;
	}

	// Method to insert a new node
	public void insert(long key, long value) {
		root = insertRecursive(root, key, value);
	}

	// Recursive helper method for insertion
	private Node insertRecursive(Node current, long key, long value) {
		if (current == null) {
			return new Node(key, value);
		}

		if (key < current.getKey()) {
			current.setLeft(insertRecursive(current.getLeft(), key, value));
		} else if (key > current.getKey()) {
			current.setRight(insertRecursive(current.getRight(), key, value));
		} else {
			// If the key already exists, update its value
			current.setValue(value);
		}

		return current;
	}

	// Method to search for a key
	public Long search(long key) {
		return searchRecursive(root, key);
	}

	// Recursive helper method for search
	private Long searchRecursive(Node current, long key) {
		if (current == null) {
			return null; // Key not found
		}

		if (key == current.getKey()) {
			return current.getValue(); // Key found, return its value
		}

		if (key < current.getKey()) {
			return searchRecursive(current.getLeft(), key);
		} else {
			return searchRecursive(current.getRight(), key);
		}
	}

	// Method for in-order traversal
	public void inOrderTraversal() {
		inOrderTraversalRecursive(root);
	}

	// Recursive helper method for in-order traversal
	private void inOrderTraversalRecursive(Node current) {
		if (current != null) {
			inOrderTraversalRecursive(current.getLeft());
			System.out.println("Key: " + current.getKey() + ", Value: " + current.getValue());
			inOrderTraversalRecursive(current.getRight());
		}
	}

	// Method for pre-order traversal
	public void preOrderTraversal() {
		preOrderTraversalRecursive(root);
	}

	// Recursive helper method for pre-order traversal
	private void preOrderTraversalRecursive(Node current) {
		if (current != null) {
			System.out.println("Key: " + current.getKey() + ", Value: " + current.getValue());
			preOrderTraversalRecursive(current.getLeft());
			preOrderTraversalRecursive(current.getRight());
		}
	}

	// Method for post-order traversal
	public void postOrderTraversal() {
		postOrderTraversalRecursive(root);
	}

	// Recursive helper method for post-order traversal
	private void postOrderTraversalRecursive(Node current) {
		if (current != null) {
			postOrderTraversalRecursive(current.getLeft());
			postOrderTraversalRecursive(current.getRight());
			System.out.println("Key: " + current.getKey() + ", Value: " + current.getValue());
		}
	}

	// Main method for example usage
	public static void main(String[] args) {
		BTJava2 tree = new BTJava2();
		tree.insert(8, 800);
		tree.insert(3, 300);
		tree.insert(10, 1000);
		tree.insert(1, 100);
		tree.insert(6, 600);
		tree.insert(14, 1400);
		tree.insert(4, 400);
		tree.insert(7, 700);
		tree.insert(13, 1300);

		System.out.println("In-order Traversal:");
		tree.inOrderTraversal();

		System.out.println("\nPre-order Traversal:");
		tree.preOrderTraversal();

		System.out.println("\nPost-order Traversal:");
		tree.postOrderTraversal();

		System.out.println("\nSearching for key 10:");
		Long value = tree.search(10);
		if (value != null) {
			System.out.println("Value found: " + value);
		} else {
			System.out.println("Key not found.");
		}
	}
}
