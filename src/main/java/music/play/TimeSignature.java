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
