package consistenthashing;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class HashRing {
    private final Map<Key, Node> keyOwnership = new HashMap<>();
    private final NavigableMap<Long, VirtualNode> ring = new TreeMap<>();

    private final int virtualNodeCount;

    public HashRing(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
    }

    public void addNode(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            // compute hash of virtual node id
            String vnodeId = getVirtualNodeId(node.getId(), i);
            long token = hash(vnodeId);

            VirtualNode vnode = new VirtualNode(vnodeId, node, token);

            // add virtual node to ring
            ring.put(token, vnode);
        }
    }

    public void removeNode(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            // compute hash of virtual node id
            String vnodeId = getVirtualNodeId(node.getId(), i);
            long token = hash(vnodeId);

            // remove virtual node from ring
            ring.remove(token);
        }
    }

    private static String getVirtualNodeId(String id, int i) {
        return id + "#" + i;
    }

    // FIXME: hash code not ideal since only 32 bits and distribution is weaker
    public long hash(String input) {
        return Math.abs(input.hashCode());
    }

    public void addKey(Key key) {
        keyOwnership.put(key, clockwiseLookup(key));
    }

    public Node clockwiseLookup(Key key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("Cannot lookup key on an empty ring");
        }

        // key id converted into long for ring position
        long keyHash = hash(key.getKey());

        // find next virtual node in terms of its position on the ring
        var entry = ring.ceilingEntry(keyHash);

        // no token exists clockwise from that position,
        // so wrap around to the start
        if (entry == null) {
            entry = ring.firstEntry();
        }

        // return node from entry
        return entry.getValue().getNode();
    }

    public void removeKey(Key key) {
        keyOwnership.remove(key);
    }

    public Map<Key, Node> getOwnership() {
        return new HashMap<>(keyOwnership);
    }

    public NavigableMap<Long, VirtualNode> getRing() {
        return new TreeMap<>(ring);
    }

    @Override
    public String toString() {
        return ring.toString();
    }
}
