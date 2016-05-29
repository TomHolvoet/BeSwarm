package bebopbehavior;

/**
 * @author Hoang Tung Dinh
 */
public enum Direction {
    FORWARD((byte) 0),
    BACKWARD((byte) 1),
    RIGHT((byte) 2),
    LEFT((byte) 3);

    private final byte code;

    Direction(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
