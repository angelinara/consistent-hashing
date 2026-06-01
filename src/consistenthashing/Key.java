package consistenthashing;

public class Key {
    private final String id;

    public Key(String id) {
        this.id = id;
    }

    public String getKey() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
