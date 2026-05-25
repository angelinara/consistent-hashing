## Context

The repository already defines the target behavior in `openspec/specs/consistent-hashing/spec.md` and sketches the intended structure in `openspec/specs/consistent-hashing/design.md`. The codebase, however, currently contains only `Node`, `VirtualNode`, and an empty `Redistributor`, with no `HashRing`, `Key`, or test coverage. This change is about delivering the initial in-memory implementation rather than introducing a networked service or persistence layer.

## Goals / Non-Goals

**Goals:**

- Implement an in-memory hash ring that maps keys to physical nodes through virtual nodes.
- Make node addition, node removal, key addition, and key removal concrete and testable.
- Report redistribution as ownership-range changes so topology updates can be inspected without scanning every key.
- Keep the first version small enough to fit the repository's current structure and build tooling.

**Non-Goals:**

- Weighted nodes or heterogeneous replica counts per physical node
- Persistence, background services, or external storage
- HTTP endpoints or server behavior for ring operations
- Performance tuning beyond choosing appropriate in-memory data structures

## Decisions

### Use a `HashRing` class backed by `NavigableMap<Long, VirtualNode>`

This follows the existing design and gives the implementation direct access to `ceilingEntry()` for clockwise lookup and `firstEntry()` for wraparound. It also keeps the first version simple and aligned with Java's standard library.

Alternative considered: a sorted list of tokens with manual binary search. That would work, but it would add custom search and mutation code without giving this small implementation a clear benefit over `TreeMap`.

### Keep domain objects explicit: `Node`, `VirtualNode`, `Key`, and range-change value objects

The current repo already separates `Node` and `VirtualNode`. The implementation should continue that separation, introduce a lightweight `Key` type for ownership tracking, and add a value object for redistribution results so changed token ranges are explicit and testable.

Alternative considered: representing everything as strings and maps. That would reduce the number of classes, but it would blur the distinction between physical nodes, virtual nodes, and redistribution output that the baseline spec and design are trying to preserve.

### Make virtual-node count a ring-level configuration

The initial version should take one configurable replica count that applies to every physical node added to the ring. This matches the existing design's deferred decision to avoid per-node weighting while still making replica count testable and implementation-ready.

Alternative considered: hard-coding a replica count. That would be simpler, but it would lock an implementation detail into the code and make the system harder to validate under different distributions.

### Treat empty-ring lookup as an explicit error

The baseline spec defines lookup when virtual nodes exist but does not say what should happen when the ring is empty. The first implementation should fail explicitly when ownership is requested without any nodes present so callers cannot silently receive an invalid owner.

Alternative considered: returning `null` or an empty optional. That would push ambiguity onto callers and make ownership behavior less direct in the initial API.

### Report redistribution as changed ownership ranges

`Redistributor` should compare the ring before and after a topology change and emit changed token ranges with previous and new owners. This keeps redistribution aligned with consistent-hashing semantics and matches the baseline design.

Alternative considered: recalculating concrete key moves directly. That can be layered on later, but using key-level redistribution as the primary abstraction would couple the implementation too tightly to a particular storage strategy.

## Risks / Trade-offs

- **Hash collisions between virtual-node tokens** -> Decide on a deterministic collision strategy during implementation and cover it with tests if it becomes relevant.
- **Redistribution math around wraparound ranges is easy to get wrong** -> Add focused tests for both standard and wraparound ownership changes.
- **A richer public API could emerge while implementing** -> Keep the first version centered on the minimum operations already described by the spec and design.
- **The current README suggests server behavior that this change does not implement** -> Keep this change scoped to the in-memory library and let any server surface be proposed separately if needed.
