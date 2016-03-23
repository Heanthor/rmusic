package music.rhythm;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class representing an amount of time a note should sound for.
 * Represents time as note values found in most music.
 * Currently supports common non-triplet note values, up to sixty-fourth notes.
 */
public class Duration {
    public enum NoteValue {
        WHOLE,
        HALF,
        QUARTER,
        EIGHTH,
        SIXTEENTH,
        THIRTYSECONDTH,
        SIXTYFOURTH
    }


    /**
     * Duration values are represented internally as a fraction akin to the time signature of the measure.
     * E.g. a dotted half note in 3/4 time would be represented as the Fraction 3/4.
     * <p/>
     * An eigth note is represented by the Fraction 1/8 in both 4/4 and 6/8 time, as another example.
     */
    private static class Fraction {
        public int numerator;
        public int denominator;

        public Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        /**
         * Adds this Fraction to the given Fractions. Uses Euclidian algorithm. Does not modify the current object.
         *
         * @param f Fraction to add to current object
         * @return The result
         */
        public Fraction add(Fraction f) {
            return simplify(new Fraction(numerator * f.denominator + denominator * f.numerator,
                    denominator * f.denominator));
        }

        /**
         * Divides this Fraction by the given Fraction. Does not modify the current object.
         *
         * @param f Fraction to divide by
         * @return The result
         */
        public Fraction divide(Fraction f) {
            return simplify(new Fraction(numerator * f.denominator, denominator * f.numerator));
        }

        /**
         * Multiplies this Fraction by the given Fraction. Does not modify the current object.
         *
         * @param f Fraction to multiply by
         * @return The result
         */
        public Fraction multiply(Fraction f) {
            return simplify(new Fraction(numerator * f.numerator, denominator * f.denominator));
        }

        /**
         * Get the numeric value of this Fraction
         */
        public double getDoubleValue() {
            return (double) numerator / (double) denominator;
        }

        /**
         * Computes the greatest common denominator of two numbers
         *
         * @param x First number
         * @param y Second number
         * @return The GCD
         */
        private int gcd(int x, int y) {
            if (y == 0) {
                return x;
            }

            return gcd(y, x % y);
        }

        /**
         * Simplifies the given Fraction, transforming it into lowest form.
         *
         * @param f The Fraction to modify.
         * @return the modified fraction for convenience.
         */
        private Fraction simplify(Fraction f) {
            int gcd = gcd(f.numerator, f.denominator);

            f.numerator /= gcd;
            f.denominator /= gcd;

            return f;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Fraction fraction = (Fraction) o;

            return numerator == fraction.numerator && denominator == fraction.denominator;

        }

        @Override
        public int hashCode() {
            int result = numerator;
            result = 31 * result + denominator;
            return result;
        }

        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }
    }
    public static final ArrayList<String> strRepresentations = new ArrayList<String>();

    private NoteValue value;
    private boolean dot;
    private Fraction durationValue;

    private static HashBiMap<NoteValue, Fraction> durationValues = HashBiMap.create();

    static {
        strRepresentations.addAll(Arrays.asList("W", "H", "Q", "E", "S", "T", "X"));
        // Convert enum into numeric duration value
        // Values are fractions of total beats in measure
        durationValues.put(NoteValue.WHOLE, new Fraction(1, 1));
        durationValues.put(NoteValue.HALF, new Fraction(1, 2));
        durationValues.put(NoteValue.QUARTER, new Fraction(1, 4));
        durationValues.put(NoteValue.EIGHTH, new Fraction(1, 8));
        durationValues.put(NoteValue.SIXTEENTH, new Fraction(1, 16));
        durationValues.put(NoteValue.THIRTYSECONDTH, new Fraction(1, 32));
        durationValues.put(NoteValue.SIXTYFOURTH, new Fraction(1, 64));
    }

    /**
     * Create a Duration object, consisting of a note value, and an optional dot.
     *
     * @param duration Amount of time the Duration should last.
     * @param dot      If true, add a dot to this Duration, adding half its value to its total.
     */
    public Duration(NoteValue duration, boolean dot) {
        if (duration == NoteValue.SIXTYFOURTH && dot) {
            throw new IllegalArgumentException("Duration resolution too great (cannot exceed sixty-fourth note).");
        }
        this.value = duration;
        this.dot = dot;
        durationValue = durationValues.get(duration);

        // Add half the note's value again
        if (dot) {
            durationValue = durationValue.multiply(new Fraction(3, 2));
        }
    }

    /**
     * Converts a fraction representing a Duration into that Duration object.
     * @param f Fraction to convert
     * @return The Duration object represented by that Fraction, with note and dot.
     */
    private static Duration fractionToDuration(Fraction f) {
        if (durationValues.containsValue(f)) {
            // Fraction represents a simple note, so return it
            return new Duration(durationValues.inverse().get(f), false);
        } else {
            // Duration has a dot, so simplify it
            NoteValue baseNote = durationValues.inverse().get(new Fraction(1, f.denominator / 2));

            // Duration will be the next largest note, plus a dot
            // E.g. 3/4 -> dotted half (1/2 + dot)
            // 3/8 -> dotted quarter (1/4 + dot)
            return new Duration(baseNote, true);
        }
    }

    /**
     * Sums multiple durations to return the larger duration.
     *
     * @param in Multiple Durations to be summed
     * @return A new Duration representing the combined values of all Durations passed in.
     */
    public static Duration addDurations(Duration... in) {
        Fraction total = in[0].durationValue;

        for (int i = 1; i < in.length; i++) {
            total = total.add(in[i].durationValue);
        }

        return fractionToDuration(total);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;

        return durationValue.equals(duration.durationValue);

    }

    @Override
    public int hashCode() {
        return durationValue.hashCode();
    }

    @Override
    public String toString() {
        return "Duration: " + (dot ? "Dotted " : "") + value.name().toLowerCase();
    }
}
