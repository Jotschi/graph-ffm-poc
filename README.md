# Graph Storage FFM PoC

This project contains a PoC implementation for a Graph storage API which primarily utilizes the Foreign Function and Memory API in Java.

## API

```java
    Path basePath = …
	try (GraphStorage st = new GraphStorageImpl(basePath)) {
			Node nodeA = new NodeImpl("Person");
			nodeA.set("name", "Wes Anderson");

			Node nodeB = new NodeImpl("Vehicle");
			nodeB.set("name", "VW Beetle");

			Relationship rel = new RelationshipImpl(nodeA, "HAS_RELATIONSHIP", nodeB);
			rel.set("name", "relName");

			long id = st.store(rel);

			Relationship loadedRel = st.loadRelationship(id);
    }

```