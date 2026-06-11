# README

This is the repository for a consistent hashing implementation written in Java. To compile and run in `macOS`:

```bash
# install Java and just utility
brew install openjdk just

# install dependencies
just install

# compile and run the server
just run
```

## Architecture

```mermaid
flowchart TD
    A[Request] --> B{Operation}

    B -->|Add Node| C[Add node to hash ring]
    C --> D[Redistribute affected keys]

    B -->|Remove Node| E[Remove node from hash ring]
    E --> D

    B -->|Add Key| F[Locate responsible node]
    F --> G[Assign key to node]

    B -->|Remove Key| H[Remove key assignment]

    B -->|Topology Change| I[Determine ownership changes]
    I --> J[Generate redistribution plan]
```

```mermaid
flowchart TD
	A[Start request] --> B{Operation type}

	B -->|addNode| C[Generate vnode ids node#i]
	C --> D[Hash each vnode id to token]
	D --> E[Insert token -> VirtualNode in ring]
	E --> F[Rebalance existing keys]

	B -->|removeNode| G[Generate vnode ids node#i]
	G --> H[Hash each vnode id to token]
	H --> I[Remove matching tokens from ring]
	I --> F

	B -->|addKey| J[Hash key id to keyHash]
	J --> K{Ring empty?}
	K -->|yes| L[Throw IllegalStateException]
	K -->|no| M[Find ceilingEntry keyHash]
	M --> N{Entry found?}
	N -->|yes| O[Owner = entry.node]
	N -->|no| P[Wrap to firstEntry]
	P --> O
	O --> Q[Store key -> owner]

	B -->|removeKey| R[Delete key from ownership map]

	B -->|redistribute| S[Read ring before and after change]
	S --> T[Compute moved ranges]
	T --> U[Emit RangeChange list]
```
