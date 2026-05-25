## ADDED Requirements

### Requirement: Configure a default virtual-node count for the ring
The system MUST allow a hash ring instance to be created with a configured virtual-node count that is applied when physical nodes are added.

#### Scenario: Add node using configured replica count
- **GIVEN** a hash ring configured with a virtual-node count of 3
- **WHEN** a physical node is added to the ring
- **THEN** the system MUST create 3 virtual nodes for that physical node
- **AND** place each virtual node on the ring according to its hash position

### Requirement: Reject ownership lookup when the ring is empty
The system MUST fail explicitly when ownership is requested before any virtual nodes exist on the ring.

#### Scenario: Resolve owner with no nodes present
- **GIVEN** an empty hash ring
- **WHEN** ownership is requested for a key
- **THEN** the system MUST report that the ring has no available owners

### Requirement: Report redistribution as ownership-range changes
The system MUST expose topology-change results as changed ownership ranges that identify the previous owner and the new owner for each affected segment.

#### Scenario: Report changed ranges after node addition
- **GIVEN** a hash ring before and after a physical node is added
- **WHEN** redistribution information is requested for the topology change
- **THEN** the system MUST return the token ranges whose owner changed
- **AND** each changed range MUST include both the previous owner and the new owner

#### Scenario: Report changed ranges after node removal
- **GIVEN** a hash ring before and after a physical node is removed
- **WHEN** redistribution information is requested for the topology change
- **THEN** the system MUST return the token ranges whose owner changed
- **AND** each changed range MUST include both the previous owner and the new owner
