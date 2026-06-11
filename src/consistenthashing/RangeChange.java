package consistenthashing;

public class RangeChange {
    private final Node previousOwner;
    private final Node newOwner;

    public RangeChange(Node previousOwner, Node newOwner) {
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    public Node getPreviousOwner() {
        return previousOwner;
    }

    public Node getNewOwner() {
        return newOwner;
    }

    @Override
    public String toString() {
        return "RangeChange{previousOwner=" + previousOwner
                + ", newOwner=" + newOwner + "}";
    }
}
