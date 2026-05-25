## 1. Existing groundwork

- [x] 1.1 Add separate `Node` and `VirtualNode` classes so physical nodes and virtual nodes are modeled as distinct concepts.
- [x] 1.2 Scaffold the `Redistributor` class as the entry point for topology-change reporting.

## 2. Core ring model

- [ ] 2.1 Expand the domain model with the missing types needed by the design, including `Key`, richer `VirtualNode` ownership data, and any value objects required to describe redistribution ranges.
- [ ] 2.2 Add a `HashRing` implementation backed by a sorted token map and a configurable default virtual-node count.

## 3. Ownership and lifecycle behavior

- [ ] 3.1 Implement clockwise key lookup with wraparound and explicit failure when ownership is requested on an empty ring.
- [ ] 3.2 Implement physical-node add and remove operations so virtual nodes are inserted and removed consistently for each configured replica.
- [ ] 3.3 Implement key add and remove operations plus an ownership-inspection view that reports which physical node owns each tracked key.

## 4. Redistribution reporting

- [ ] 4.1 Implement `Redistributor` so it compares ring states before and after topology changes and emits the ownership ranges whose assigned node changed.
- [ ] 4.2 Cover both node addition and node removal cases, including wraparound ownership changes, in the redistribution model.

## 5. Verification

- [ ] 5.1 Add tests for virtual-node creation, clockwise lookup, and wraparound assignment.
- [ ] 5.2 Add tests for empty-ring lookup, key lifecycle operations, and ownership inspection.
- [ ] 5.3 Add tests for redistribution output after node addition and node removal so only affected ownership ranges are reported as changed.
