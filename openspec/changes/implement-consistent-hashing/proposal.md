## Why

The repository already has a baseline consistent-hashing specification and partial Java model classes, but it does not yet have a complete in-memory implementation or a task breakdown that can drive delivery. This change turns the existing spec into an executable implementation plan.

## What Changes

- Implement the baseline `consistent-hashing` capability as an in-memory Java library centered on a hash ring, physical nodes, virtual nodes, and key ownership tracking.
- Clarify implementation-critical behavior that is currently implicit, including configurable virtual-node counts, empty-ring lookup behavior, and redistribution reporting for topology changes.
- Add a trackable task list that covers the missing domain classes, ring operations, redistribution logic, and tests.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `consistent-hashing`: define initial in-memory implementation details needed to turn the existing requirements into a complete, testable Java implementation.

## Impact

- `src/consistenthashing/` Java classes for the ring model and redistribution logic
- `test/` coverage for clockwise lookup, wraparound, node topology changes, and key lifecycle behavior
- Existing build and test workflow in `justfile`
