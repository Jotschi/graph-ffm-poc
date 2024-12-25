package io.metaloom.graph.core.ffm;

import org.junit.jupiter.api.Test;

import io.metaloom.graph.core.storage.data.impl.LongBinaryTree;

public class LongBinaryTreeTest {

	@Test
	public void testBinary() {
		LongBinaryTree tree = new LongBinaryTree();
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
