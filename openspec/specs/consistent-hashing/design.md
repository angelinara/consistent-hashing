# consistent-hashing Design

## Overview

This design describes a Java implementation of consistent hashing that assigns keys to physical nodes through virtual nodes. The core lookup rule is:

- hash the key to a ring position
- move clockwise on the ring
- assign the key to the first virtual node encountered
- treat that virtual node's physical node as the owner

The design separates behavioral requirements from implementation details:

- `spec.md` defines what the system must do
- `design.md` defines the proposed classes, data structures, and algorithms

## Core model

The system is built around five classes:

```text
Key ─────────────► HashRing ─────────────► VirtualNode ─────────────► Node
                        │
                        └────────────────► Redistributor
```

### Node

Represents a physical server in the system.

Responsibilities:

- provide a stable physical-node identity
- own one or more virtual nodes
- act as the final owner for assigned keys

Expected fields:

- `String id`
- optional metadata such as host, port, or label

### VirtualNode

Represents a single token on the ring for a physical node.

Responsibilities:

- reference exactly one physical node
- carry a replica or index identifier
- expose a stable token position on the ring

Expected fields:

- `Node owner`
- `int replicaIndex`
- `long token`

### Key

Represents a logical item that is assigned to a node.

Responsibilities:

- provide a stable identifier for hashing
- remain separate from hashing and lookup strategy

Expected fields:

- `String id`

### HashRing

Represents the ring and performs lookup.

Responsibilities:

- store virtual-node positions in sorted order
- add and remove nodes
- add and remove keys
- resolve key ownership
- expose ownership state for inspection

### Redistributor

Compares ring topology before and after node changes.

Responsibilities:

- identify ownership ranges affected by node addition
- identify ownership ranges affected by node removal
- report changed ownership without requiring a full scan of all keys

## Ring representation

The recommended abstraction is:

```text
NavigableMap<Long, VirtualNode>
```

The recommended initial implementation is:

```text
TreeMap<Long, VirtualNode>
```

Rationale:

- the map key is the token or ring position
- the map value is the virtual node at that position
- `ceilingEntry()` supports clockwise lookup
- `firstEntry()` supports wraparound when the lookup passes the largest token

This keeps the design flexible:

- the field type can remain `NavigableMap<Long, VirtualNode>`
- the implementation can begin as `TreeMap<Long, VirtualNode>`
- a later optimization could replace it without changing the higher-level design

## Ownership model

The design distinguishes between:

- the ring as a mapping of token positions to virtual nodes
- the assignment view as a mapping of keys to physical nodes

Conceptually:

```text
token 103 -> VirtualNode(Node A, replica 0)
token 411 -> VirtualNode(Node A, replica 1)
token 700 -> VirtualNode(Node B, replica 0)

key "alpha" -> hash 350 -> next token 411 -> Node A
```

This avoids treating physical nodes as ring positions directly. A physical node may own multiple virtual-node positions.

## Hashing strategy

Hashing should be owned by the ring logic rather than by `Key` or `Node`.

Design rule:

- `Key` and `Node` are domain objects
- `HashRing` is responsible for converting identifiers into token positions

Expected behavior:

- a key position is derived from a hash of the key identifier
- a virtual-node token is derived from a hash of the physical-node identifier plus replica index

Conceptually:

```text
key position   = hash(key.id)
token position = hash(node.id + "#" + replicaIndex)
```

The design does not require a fixed ring size. A full hash range mapped into `long` token space is sufficient for the initial implementation.

## Lookup algorithm

The ownership lookup algorithm is:

1. compute the hash position for the key
2. find the first token greater than or equal to that position
3. if no such token exists, wrap to the first token in the ring
4. return the owner of the selected virtual node

Illustration:

```text
Ring tokens:
103 -> A0
411 -> A1
700 -> B0

key hash = 350
clockwise successor = 411
owner = Node A
```

Wraparound case:

```text
Ring tokens:
103 -> A0
411 -> A1
700 -> B0

key hash = 900
clockwise successor = wrap to 103
owner = Node A
```

## Node addition

When a physical node is added:

1. create the configured number of virtual nodes
2. compute a token for each virtual node
3. insert each token into the ring
4. compare old and new ownership ranges
5. identify which ranges now belong to the new node

Conceptually:

```text
before:
[0..103)   -> A
[103..411) -> A
[411..700) -> B
[700..end) -> A

after adding Node C token at 500:
[411..500) -> B
[500..700) -> C
```

Only keys in the newly claimed ranges should move.

## Node removal

When a physical node is removed:

1. remove all virtual nodes belonging to that physical node
2. compare old and new ownership ranges
3. identify which ranges lost their prior owner
4. reassign affected keys to the next clockwise owners in the updated ring

Conceptually:

```text
before:
[411..500) -> B
[500..700) -> C

after removing Node C:
[411..700) -> B
```

Only keys in ranges formerly owned by the removed node should move.

## Redistribution model

The primary redistribution output should be token-range ownership changes rather than immediate key scanning.

Recommended model:

- `Redistributor` compares two ring states
- it emits changed ranges such as:
  - start token
  - end token
  - previous owner
  - new owner

Conceptually:

```text
[500..700) moved from Node B to Node C
```

This is preferable to using a key-by-key redistribution model as the core abstraction because:

- consistent hashing is fundamentally about ownership ranges
- key scanning can be layered on top later
- the design remains valid whether keys are held in memory, on disk, or externally

## Key lifecycle

### Adding a key

When a key is added:

1. hash the key identifier
2. resolve the owning virtual node by clockwise lookup
3. record the key-to-node assignment

### Removing a key

When a key is removed:

1. find the existing key assignment
2. remove it from the assignment view

Removing a key does not require rebalancing the ring.

## Ownership inspection

The design should expose an ownership view for debugging, testing, and visualization.

Likely forms:

- `Map<Key, Node>`
- string or structured summary for display

This inspection view is separate from the ring structure itself.

## Initial defaults

The following defaults are recommended for the first implementation:

- separate `Node` and `VirtualNode` classes
- ring stored as `NavigableMap<Long, VirtualNode>`
- initial implementation backed by `TreeMap<Long, VirtualNode>`
- redistribution reported as changed token ranges
- node weights deferred
- one configurable virtual-node count applied equally to all physical nodes

## Deferred decisions

The following decisions are intentionally left open for later refinement:

- exact hash function choice
- exact number of virtual nodes per physical node
- whether node weighting is supported
- whether redistribution also returns concrete affected keys in addition to changed ranges
- whether the system is purely in-memory or also exposes server endpoints
