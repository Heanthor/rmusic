package music.pitch;

/**
 * Represents an octave on a traditional keyboard. Contains methods useful for moving
 * between the enum ordinal and integer representation of an octave.
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
        // Sync value to enum value
        this.value = ordinal() + 1;
    }

    /**
     * Return the Octave corresponding to the given integer.
     * Octave must be in the range 1-8 inclusive.
     * @param number Integer octave representation
     * @return The enum ordinal
     */
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
