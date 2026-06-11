package consistenthashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Redistributor {
    // Compare ownership before and after a topology change.
    // Returns one RangeChange per key whose assigned node changed.
    public static List<RangeChange> afterChange(Map<Key, Node> before, Map<Key, Node> after) {
        List<RangeChange> changes = new ArrayList<>();
        for (Map.Entry<Key, Node> entry : before.entrySet()) {
            Key key = entry.getKey();
            Node previousOwner = entry.getValue();
            Node newOwner = after.get(key);
            if (newOwner != null && previousOwner != newOwner) {
                changes.add(new RangeChange(previousOwner, newOwner));
            }
        }
        return changes;
    }
}
