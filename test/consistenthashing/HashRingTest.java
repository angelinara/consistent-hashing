package consistenthashing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class HashRingTest {
    @Test
    void testAddNodes() {
        // test nodes not added to empty hashring
        Node node = new Node("node1");
        HashRing ring = new HashRing(0);
        ring.addNode(node);
        assertEquals("{}", ring.toString());

        // test node added to hashring
        ring = new HashRing(1);
        ring.addNode(node);
        String temp = "{2114402076=VirtualNode{id='node1#0', "
                + "node=node1, hashPosition=2114402076}}";
        assertEquals(temp, ring.toString());

        // test multiple nodes added to hashring
        Node node2 = new Node("node2");
        Node node3 = new Node("node3");
        ring = new HashRing(2);
        ring.addNode(node2);
        ring.addNode(node3);
        temp = "{2114403037=VirtualNode{id='node2#0', node=node2, hashPosition=2114403037}, "
                + "2114403038=VirtualNode{id='node2#1', node=node2, hashPosition=2114403038}, "
                + "2114403998=VirtualNode{id='node3#0', node=node3, hashPosition=2114403998}, "
                + "2114403999=VirtualNode{id='node3#1', node=node3, hashPosition=2114403999}}";
        assertEquals(temp, ring.toString());
    }

    @Test
    void removeNodes() {
        // test one node removed
        Node node = new Node("node1");
        HashRing ring = new HashRing(1);
        ring.addNode(node);
        String temp = "{2114402076=VirtualNode{id='node1#0', "
                + "node=node1, hashPosition=2114402076}}";
        assertEquals(temp, ring.toString());
        ring.removeNode(node);
        assertEquals("{}", ring.toString());

        // test multiple nodes removed
        Node node2 = new Node("node2");
        Node node3 = new Node("node3");
        ring = new HashRing(2);
        ring.addNode(node2);
        ring.addNode(node3);
        temp = "{2114403037=VirtualNode{id='node2#0', node=node2, hashPosition=2114403037}, "
                + "2114403038=VirtualNode{id='node2#1', node=node2, hashPosition=2114403038}, "
                + "2114403998=VirtualNode{id='node3#0', node=node3, hashPosition=2114403998}, "
                + "2114403999=VirtualNode{id='node3#1', node=node3, hashPosition=2114403999}}";
        assertEquals(temp, ring.toString());
        ring.removeNode(node2);
        temp = "{2114403998=VirtualNode{id='node3#0', node=node3, hashPosition=2114403998}, "
                + "2114403999=VirtualNode{id='node3#1', node=node3, hashPosition=2114403999}}";
        assertEquals(temp, ring.toString());
    }

    @Test
    void addKeys() {
        // add one key
        HashRing ring = new HashRing(1);
        Node node2 = new Node("node2");
        ring.addNode(node2);
        Key key = new Key("key1");
        ring.addKey(key);
        Map<Key, Node> keyOwnership = new HashMap<>();
        keyOwnership.put(key, node2);
        assertEquals(keyOwnership, ring.getOwnership());

        // add another key
        Node node3 = new Node("node3");
        ring.addNode(node3);
        // This key hashes to node3#0's exact token, so it must map to node3.
        Key key2 = new Key("node3#0");
        ring.addKey(key2);
        keyOwnership.put(key2, node3);
        assertEquals(keyOwnership, ring.getOwnership());
    }

    @Test
    void removeKeys() {
        HashRing ring = new HashRing(1);
        Node node = new Node("node");
        ring.addNode(node);
        Key key = new Key("key");
        ring.addKey(key);
        Map<Key, Node> keyOwnership = new HashMap<>();
        keyOwnership.put(key, node);
        assertEquals(keyOwnership, ring.getOwnership());
        keyOwnership.remove(key);
        ring.removeKey(key);
        assertEquals(keyOwnership, ring.getOwnership());
    }

    @Test
    void wraparoundMapsToFirstToken() {
        HashRing ring = new HashRing(1);
        Node node = new Node("node1");
        ring.addNode(node);

        // Find a key hash greater than the node token so lookup
        // must wrap around to the first token on the ring.
        long token = ring.hash("node1#0");
        String wrapKeyId = null;
        for (int i = 0; i < 1_000_000; i++) {
            String candidate = "k" + i;
            if (ring.hash(candidate) > token) {
                wrapKeyId = candidate;
                break;
            }
        }
        assertNotNull(wrapKeyId);

        // Create a key whose hash is greater than
        // the hash of the last token on the ring.
        Key wrapKey = new Key(wrapKeyId);
        ring.addKey(wrapKey);

        // If wrap-around works, ownership should still resolve
        // to the first token's node.
        assertEquals(node, ring.getOwnership().get(wrapKey));
    }

    @Test
    void emptyRingLookupThrowsException() {
        HashRing ring = new HashRing(1);
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ring.addKey(new Key("key")));

        assertEquals("Cannot lookup key on an empty ring", exception.getMessage());
    }
}