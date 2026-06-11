package consistenthashing;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        HashRing ring = new HashRing(3, 10);
        Node node1 = new Node("node1");
        Node node2 = new Node("node2");
        ring.addNode(node1);
        ring.addNode(node2);

        // Add many keys so there is a good chance some move when nodes are added
        Key[] keys = new Key[10];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = new Key("key" + i);
            ring.addKey(keys[i]);
        }

        Map<Key, Node> before = ring.getOwnership();

        // Add two new nodes - some keys will move to them
        Node node3 = new Node("node3");
        Node node4 = new Node("node4");
        ring.addNode(node3);
        ring.addNode(node4);

        Map<Key, Node> after = ring.getOwnership();

        System.out.println("=== redistribution changes ===");
        List<RangeChange> changes = Redistributor.afterChange(before, after);
        if (changes.isEmpty()) {
            System.out.println("  (no keys moved)");
        } else {
            System.out.println(changes);
        }
    }
}
