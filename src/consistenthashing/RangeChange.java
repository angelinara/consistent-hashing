package consistenthashing;

public class RangeChange {
    private final long rangeStart;
    private final long rangeEnd;
    private final Node previousOwner;
    private final Node newOwner;

    public RangeChange(long rangeStart, long rangeEnd, Node previousOwner, Node newOwner) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    public long getRangeStart() {
        return rangeStart;
    }

    public long getRangeEnd() {
        return rangeEnd;
    }

    public Node getPreviousOwner() {
        return previousOwner;
    }

    public Node getNewOwner() {
        return newOwner;
    }
}
