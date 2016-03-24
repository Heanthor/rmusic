package music.pitch;

/**
 * Represents an octave on a traditional keyboard. Just a wrapper on an integer.
 * Uses a singleton pattern to cut down on allocations.
 * @author reedt
 */
public enum Octave {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT;

    private final int value;
    Octave() {
        this.value = ordinal() + 1;
    }

    public static Octave octaveFromInteger(int number) {
        try {
            return Octave.values()[number - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid octave (range 1-8).");
        }
    }

    public int getNumberValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
