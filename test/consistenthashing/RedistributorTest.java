package consistenthashing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class RedistributorTest {
    @Test
    void testRedistributor() {
        // test with empty maps
        Map<Key, Node> before = new HashMap<>();
        Map<Key, Node> after = new HashMap<>();

        List<RangeChange> list = Redistributor.afterChange(before, after);
        assertEquals(0, list.size());

        // test with empty before map
        Key key = new Key("key");
        Node node = new Node("node");
        after.put(key, node);

        list = Redistributor.afterChange(before, after);
        assertEquals(0, list.size());

        // test with empty after map
        after = new HashMap<>();
        before.put(key, node);

        list = Redistributor.afterChange(before, after);
        assertEquals(0, list.size());

        // test with no change
        after.put(key, node);

        list = Redistributor.afterChange(before, after);
        assertEquals(0, list.size());

         // test with a change
        Node node2 = new Node("node2");
        after.put(key, node2);

        list = Redistributor.afterChange(before, after);
        assertEquals(1, list.size());
    }
}
