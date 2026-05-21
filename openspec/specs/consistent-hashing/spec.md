# consistent-hashing Specification

## Purpose

Provide a Java implementation of consistent hashing that assigns keys to physical nodes through virtual nodes and minimizes reassignment when nodes are added or removed.

## Requirements

### Requirement: Represent physical and virtual nodes separately

The system MUST model physical nodes and virtual nodes as distinct concepts.

#### Scenario: Virtual node belongs to one physical node

- **GIVEN** a physical node exists in the system
- **WHEN** the system creates virtual nodes for that physical node
- **THEN** each virtual node MUST reference exactly one physical node
- **AND** the physical node MAY be represented by multiple virtual nodes on the hash ring

### Requirement: Assign keys by clockwise lookup on the hash ring

The system MUST assign each key to the first virtual node clockwise from the key's hash position.

#### Scenario: Assign key to nearest clockwise virtual node

- **GIVEN** a hash ring containing virtual nodes at hashed positions
- **AND** a key with a computed hash position
- **WHEN** the system resolves ownership for the key
- **THEN** the key MUST be assigned to the first virtual node encountered in the clockwise direction
- **AND** the owning physical node of that virtual node MUST be treated as the key's assigned node

#### Scenario: Wrap around the ring

- **GIVEN** a key whose hash position is greater than all virtual node positions on the ring
- **WHEN** the system resolves ownership for the key
- **THEN** the system MUST wrap to the beginning of the ring
- **AND** assign the key to the first virtual node on the ring

### Requirement: Support adding and removing physical nodes

The system MUST support adding physical nodes and removing physical nodes from the hash ring.

#### Scenario: Add node to ring

- **GIVEN** an existing hash ring
- **WHEN** a physical node is added
- **THEN** the system MUST create that node's virtual nodes
- **AND** place those virtual nodes onto the ring according to their hash positions

#### Scenario: Remove node from ring

- **GIVEN** an existing hash ring containing a physical node
- **WHEN** that physical node is removed
- **THEN** the system MUST remove all virtual nodes associated with that physical node from the ring

### Requirement: Reassign only affected ownership after topology changes

The system MUST recalculate ownership when nodes are added or removed, while limiting changes to affected portions of the ring.

#### Scenario: Reassign keys after node addition

- **GIVEN** a hash ring with existing key assignments
- **WHEN** a physical node is added
- **THEN** the system MUST identify the ranges of the ring whose ownership changed
- **AND** only keys within those affected ranges MUST be reassigned

#### Scenario: Reassign keys after node removal

- **GIVEN** a hash ring with existing key assignments
- **WHEN** a physical node is removed
- **THEN** the system MUST identify the ranges previously owned through that node's virtual nodes
- **AND** reassign affected keys to the next clockwise owner

### Requirement: Support key lifecycle operations

The system MUST support adding keys and removing keys.

#### Scenario: Add key

- **GIVEN** a hash ring containing virtual nodes
- **WHEN** a key is added
- **THEN** the system MUST compute the key's hash position
- **AND** assign the key to a physical node using clockwise lookup

#### Scenario: Remove key

- **GIVEN** a key is currently assigned to a physical node
- **WHEN** the key is removed
- **THEN** the system MUST remove the key's assignment from the system

### Requirement: Expose key ownership information

The system MUST provide a way to inspect which physical node owns each key.

#### Scenario: View key ownership

- **GIVEN** keys have been assigned to physical nodes
- **WHEN** ownership information is requested
- **THEN** the system MUST return the association between keys and their assigned physical nodes
