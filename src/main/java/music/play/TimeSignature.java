package music.play;

/**
 * Mutable class representing a time signature. As time can change during a piece of music,
 * this class can reassign numerator or denominator to represent that change.
 * @author reedt
 */
public class TimeSignature {
    private int numerator;
    private int denominator;

    /**
     * Enum restricting the choices of a denominator in a time signature.
     */
    public enum DenominatorChoices {
        _2(2),
        _4(4),
        _8(8);

        private int value;

        DenominatorChoices(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        /**
         * Get the choice represented by the given int.
         * Throws ArrayOutOfBoundsException if the int is not valid.
         * @param choice 2, 4, or 8.
         * @return The corresponding enum.
         */
        public static DenominatorChoices getByInt(int choice) {
            int c = (int)Math.pow(2, choice);

            for (DenominatorChoices d: values()) {
                if (d.value == c) {
                    return d;
                }
            }

            return null;
        }
    }

    public TimeSignature(int numerator, DenominatorChoices denominator) {
        update(numerator, denominator);
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    /**
     * Update this object to represent a new time signature
     * @param numerator The new numerator
     * @param denominator The new denominator
     */
    public void update(int numerator, DenominatorChoices denominator) {
        this.numerator = numerator;
        this.denominator = denominator.getValue();
    }
}
