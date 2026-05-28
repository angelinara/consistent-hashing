package consistenthashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
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
            // compute hash
            String vnodeId = getVirtualNodeId(node, i);
            long token = hash(vnodeId);

            // add virtual node to ring
            ring.put(token, new VirtualNode(vnodeId, node, token));
        }
    }

    public void removeNode(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            // compute hash
            String vnodeId = getVirtualNodeId(node, i);
            long token = hash(vnodeId);

            // remove virtual node from ring
            ring.remove(token);
        }
    }

    private static String getVirtualNodeId(Node node, int i) {
        return node.getId() + "#" + i;
    }

    // TODO: simplify
    private long hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            long h = 0;
            // convert MD5 into a 8 bit long for the position
            for (int i = 0; i < 8; i++) {
                h = (h << 8) | (bytes[i] & 0xFF);
            }
            return h;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void addKey(Key key) {
        keyOwnership.put(key, clockwiseLookup(key));
    }

    private Node clockwiseLookup(Key key) {
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
        return Collections.unmodifiableMap(keyOwnership);
    }
}
