package consistenthashing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
        Node node1 = new Node("node1");
        ring.addNode(node1);
        Key key1 = new Key("key1");
        ring.addKey(key1);
        Map<Key, Node> keyOwnership = new HashMap<>();
        keyOwnership.put(key1, node1);
        assertEquals(keyOwnership, ring.getOwnership());

        // add another key
        Node node2 = new Node("node2");
        ring.addNode(node2);
        // This key hashes to node2#0's exact token, so it must map to node2
        Key key2 = new Key("node2#0");
        ring.addKey(key2);
        keyOwnership.put(key2, node2);
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
        HashRing ring = new HashRing(1) {
            @Override
            public long hash(String input) {
                return switch (input) {
                    case "node1#0" -> 3;
                    case "node2#0" -> 7;
                    case "wrap" -> 9;
                    default -> 0;
                };
            }
        };

        Node node1 = new Node("node1");
        Node node2 = new Node("node2");

        ring.addNode(node1);
        ring.addNode(node2);

        Key wrapKey = new Key("wrap");

        assertNull(ring.getRing().ceilingEntry(ring.hash("wrap")));

        assertEquals(node1, ring.clockwiseLookup(wrapKey));
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