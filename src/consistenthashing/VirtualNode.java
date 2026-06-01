package consistenthashing;

public class VirtualNode {
    private final String id;
    private final Node node;
    private final long hashPosition;

    public VirtualNode(String id, Node node, long hashPosition) {
        this.id = id;
        this.node = node;
        this.hashPosition = hashPosition;
    }

    public String getId() {
        return id;
    }

    public Node getNode() {
        return node;
    }

    public long getHashPosition() {
        return hashPosition;
    }

    @Override
    public String toString() {
        return "VirtualNode{id='" + id + "', node=" + node + ", hashPosition=" + hashPosition + "}";
    }
}
