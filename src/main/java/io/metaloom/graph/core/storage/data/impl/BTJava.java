//package io.metaloom.graph.core.storage.data.impl;
//
////Define the Node class
//class Node {
//	long key;
//	long value;
//	Node left;
//	Node right;
//
//	public Node(long key, long value) {
//		this.key = key;
//		this.value = value;
//		this.left = null;
//		this.right = null;
//	}
//}
//
//public class BTJava {
//	Node root;
//
//	// Constructor
//	public BTJava() {
//		this.root = null;
//	}
//
//	// Method to insert a new node
//	public void insert(long key, long value) {
//		root = insertRecursive(root, key, value);
//	}
//
//	// Recursive helper method for insertion
//	private Node insertRecursive(Node current, long key, long value) {
//		if (current == null) {
//			return new Node(key, value);
//		}
//
//		if (key < current.key) {
//			current.left = insertRecursive(current.left, key, value);
//		} else if (key > current.key) {
//			current.right = insertRecursive(current.right, key, value);
//		} else {
//			// If the key already exists, update its value
//			current.value = value;
//		}
//
//		return current;
//	}
//
//	// Method to search for a key
//	public Long search(long key) {
//		return searchRecursive(root, key);
//	}
//
//	// Recursive helper method for search
//	private Long searchRecursive(Node current, long key) {
//		if (current == null) {
//			return null; // Key not found
//		}
//
//		if (key == current.key) {
//			return current.value; // Key found, return its value
//		}
//
//		if (key < current.key) {
//			return searchRecursive(current.left, key);
//		} else {
//			return searchRecursive(current.right, key);
//		}
//	}
//
//	// Method for in-order traversal
//	public void inOrderTraversal() {
//		inOrderTraversalRecursive(root);
//	}
//
//	// Recursive helper method for in-order traversal
//	private void inOrderTraversalRecursive(Node current) {
//		if (current != null) {
//			inOrderTraversalRecursive(current.left);
//			System.out.println("Key: " + current.key + ", Value: " + current.value);
//			inOrderTraversalRecursive(current.right);
//		}
//	}
//
//	// Method for pre-order traversal
//	public void preOrderTraversal() {
//		preOrderTraversalRecursive(root);
//	}
//
//	// Recursive helper method for pre-order traversal
//	private void preOrderTraversalRecursive(Node current) {
//		if (current != null) {
//			System.out.println("Key: " + current.key + ", Value: " + current.value);
//			preOrderTraversalRecursive(current.left);
//			preOrderTraversalRecursive(current.right);
//		}
//	}
//
//	// Method for post-order traversal
//	public void postOrderTraversal() {
//		postOrderTraversalRecursive(root);
//	}
//
//	// Recursive helper method for post-order traversal
//	private void postOrderTraversalRecursive(Node current) {
//		if (current != null) {
//			postOrderTraversalRecursive(current.left);
//			postOrderTraversalRecursive(current.right);
//			System.out.println("Key: " + current.key + ", Value: " + current.value);
//		}
//	}
//
//	// Main method for example usage
//	public static void main(String[] args) {
//		BTJava tree = new BTJava();
//		tree.insert(8, 800);
//		tree.insert(3, 300);
//		tree.insert(10, 1000);
//		tree.insert(1, 100);
//		tree.insert(6, 600);
//		tree.insert(14, 1400);
//		tree.insert(4, 400);
//		tree.insert(7, 700);
//		tree.insert(13, 1300);
//
//		System.out.println("In-order Traversal:");
//		tree.inOrderTraversal();
//
//		System.out.println("\nPre-order Traversal:");
//		tree.preOrderTraversal();
//
//		System.out.println("\nPost-order Traversal:");
//		tree.postOrderTraversal();
//
//		System.out.println("\nSearching for key 10:");
//		Long value = tree.search(10);
//		if (value != null) {
//			System.out.println("Value found: " + value);
//		} else {
//			System.out.println("Key not found.");
//		}
//	}
//}
