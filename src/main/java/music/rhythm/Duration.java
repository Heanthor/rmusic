package music.rhythm;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.stream.Collectors;

/**
 * Class representing an amount of time a note should sound for.
 * Represents time as note values found in most music.
 * Currently supports common non-triplet note values, up to sixty-fourth notes.
 */
public class Duration {
    public enum DurationValue {
        WHOLE,
        HALF,
        QUARTER,
        EIGHTH,
        SIXTEENTH,
        THIRTY_SECONDTH,
        SIXTY_FOURTH
    }

    /**
     * Duration values are represented internally as a fraction akin to the time signature of the measure.
     * E.g. a dotted half note in 3/4 time would be represented as the Fraction 3/4.
     * <p>
     * An eighth note is represented by the Fraction 1/8 in both 4/4 and 6/8 time, as another example.
     */
    private static class Fraction {
        public int numerator;
        public int denominator;

        public Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        /**
         * Constructs a Fraction out of an irrational number.
         * Tries its best to round to the closest Fraction
         *
         * @param decimal The decimal to convert.
         */
        public Fraction(double decimal) {
            Fraction temp = doubleToFraction(decimal);

            this.numerator = temp.numerator;
            this.denominator = temp.denominator;
        }

        /**
         * Adds this Fraction to the given Fractions. Uses Euclidean algorithm. Does not modify the current object.
         *
         * @param f Fraction to add to current object
         * @return The resulting new Fraction
         */
        public Fraction add(Fraction f) {
            return simplify(new Fraction(numerator * f.denominator + denominator * f.numerator,
                    denominator * f.denominator));
        }

        /**
         * Divides this Fraction by the given Fraction. Does not modify the current object.
         *
         * @param f Fraction to divide by
         * @return The resulting new Fraction
         */
        public Fraction divide(Fraction f) {
            return simplify(new Fraction(numerator * f.denominator, denominator * f.numerator));
        }

        /**
         * Multiplies this Fraction by the given Fraction. Does not modify the current object.
         *
         * @param f Fraction to multiply by
         * @return The resulting new Fraction
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

        /**
         * Gets the fraction representation of a double.
         * Returns in simplest form.
         *
         * @param d The double to evaluate.
         * @return A fraction best matching this double.
         */
        private Fraction doubleToFraction(double d) {
            // ktm5124 on StackOverflow
            String s = String.valueOf(d);
            int digitsDec = s.length() - 1 - s.indexOf('.');

            int denom = 1;
            for (int i = 0; i < digitsDec; i++) {
                d *= 10;
                denom *= 10;
            }
            int num = (int) Math.round(d);

            return simplify(new Fraction(num, denom));
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

    // Allow mapping of custom strings to enum DurationValue enum
    public static final ArrayList<String> strRepresentations = new ArrayList<>();
    public static final ArrayList<String> enumNames = new ArrayList<>();

    private DurationValue value;
    private boolean dot;
    private Fraction durationValue;

    private static final HashBiMap<DurationValue, Fraction> durationValues = HashBiMap.create();

    static {
        strRepresentations.addAll(Arrays.asList("W", "H", "Q", "E", "S", "T", "X"));
        enumNames.addAll(Arrays.asList("WHOLE", "HALF", "QUARTER", "EIGHTH", "SIXTEENTH",
                "THIRTY_SECONDTH", "SIXTY_FOURTH"));

        // Convert enum into numeric duration value
        // Values are fractions of total beats in measure
        durationValues.put(DurationValue.WHOLE, new Fraction(1, 1));
        durationValues.put(DurationValue.HALF, new Fraction(1, 2));
        durationValues.put(DurationValue.QUARTER, new Fraction(1, 4));
        durationValues.put(DurationValue.EIGHTH, new Fraction(1, 8));
        durationValues.put(DurationValue.SIXTEENTH, new Fraction(1, 16));
        durationValues.put(DurationValue.THIRTY_SECONDTH, new Fraction(1, 32));
        durationValues.put(DurationValue.SIXTY_FOURTH, new Fraction(1, 64));
    }

    /**
     * Create a Duration object, consisting of a note value, and an optional dot.
     *
     * @param duration Amount of time the Duration should last.
     * @param dot      If true, add a dot to this Duration, adding half its value to its total.
     */
    public Duration(DurationValue duration, boolean dot) {
        if (duration == DurationValue.SIXTY_FOURTH && dot) {
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
     * Parses a duration string to create a Duration object.
     * The string takes this form: [Duration][(Optional) d]
     *
     * @param durationString Duration string
     */
    public Duration(String durationString) {
        Duration temp = Duration.parseDurationString(durationString);

        this.value = temp.value;
        this.dot = temp.dot;
        this.durationValue = temp.durationValue;
    }

    /**
     * Create a new Duration with the given decimal value.
     * This will fail if the decimal value is not a clean fraction
     * able to be made into a duration.
     * @param durationDecimal The duration decimal
     */
    public Duration(double durationDecimal) {
        Duration temp = fractionToDuration(new Fraction(durationDecimal));

        this.value = temp.value;
        this.dot = temp.dot;
        this.durationValue = temp.durationValue;
    }

    /**
     * Converts a fraction representing a Duration into that Duration object.
     *
     * @param f Fraction to convert
     * @return The Duration object represented by that Fraction, with note and dot.
     */
    private static Duration fractionToDuration(Fraction f) {
        if (durationValues.containsValue(f)) {
            // Fraction represents a simple note, so return it
            return new Duration(durationValues.inverse().get(f), false);
        } else {
            // Duration has a dot, so simplify it
            DurationValue baseNote = durationValues.inverse().get(new Fraction(1, f.denominator / 2));

            // Duration will be the next largest note, plus a dot
            // E.g. 3/4 -> dotted half (1/2 + dot)
            // 3/8 -> dotted quarter (1/4 + dot)
            return new Duration(baseNote, true);
        }
    }

    /**
     * Parses a duration string into its respective object.
     *
     * @param durationString The string to parse in form [Duration][(Optional) d]
     * @return The created object
     */
    public static Duration parseDurationString(String durationString) {
        int ordinal;

        if (durationString.length() == 1) {
            // No dot
            ordinal = Duration.strRepresentations.indexOf(durationString);
            if (ordinal == -1) {
                throw new IllegalArgumentException("Note string format incorrect (invalid duration string '" + durationString + "').");
            }

            return new Duration(DurationValue.valueOf(Duration.enumNames.get(ordinal)), false);
        } else if (durationString.length() == 2 && durationString.charAt(1) == 'd') {
            ordinal = Duration.strRepresentations.indexOf("" + durationString.charAt(0));

            if (ordinal == -1) {
                throw new IllegalArgumentException("Note string format incorrect (invalid duration string '" + durationString + "').");
            }

            return new Duration(DurationValue.valueOf(Duration.enumNames.get(ordinal)), true);
        } else {
            throw new IllegalArgumentException("Note string format incorrect (invalid duration string '" + durationString + "').");
        }
    }

    /**
     * Sums multiple durations to return the larger duration.
     *
     * @param in Multiple Durations to be summed
     * @return A new Duration[] representing the combined values of all Durations passed in.
     */
    public static Duration[] addDurations(Duration... in) {
        Fraction total = in[0].durationValue;
        ArrayList<Duration> toReturn = new ArrayList<>();

        for (int i = 1; i < in.length; i++) {
            total = total.add(in[i].durationValue);
        }
        double temp = total.getDoubleValue();
        ArrayList<Double> parts = new ArrayList<>();

        if (temp > 1.0) {
            while (temp != 0) {
                double t2 = temp > 1.0 ? 1.0 : temp;
                parts.add(t2);
                temp -= t2;
            }
        } else {
            parts.add(temp);
        }


        toReturn.addAll(parts.stream().map(Duration::new).collect(Collectors.toList()));

        Duration[] d = new Duration[toReturn.size()];

        return toReturn.toArray(d);
    }

    /**
     * Generate Durations (maximum size whole note) out of the given double value.
     * Doubel value must divide nicely into durations.
     * @param value Value to break into Durations
     * @return The generated Durations
     */
    public static Duration[] generateMultipleDurations(double value) {
        ArrayList<Duration> parts = new ArrayList<>();

        if (value > 1.0) {
            while (value != 0) {
                double t2 = value > 1.0 ? 1.0 : value;
                parts.add(new Duration(t2));
                value -= t2;
            }
        } else {
            parts.add(new Duration(value));
        }

        return parts.toArray(new Duration[parts.size()]);
    }

    /**
     * Compare the current object to the given Duration, and return the ratio.
     * <p>
     * For example, comparing the current object half note to a quarter note will return 0.5,
     * as a quarter note is half the duration of a half note.
     *
     * @param d The duration to compare the current object to
     * @return The ratio of the current object's value to the argument.
     */
    public double getDurationRatio(Duration d) {
        return durationValue.multiply(
                new Fraction(d.durationValue.denominator, d.durationValue.numerator)).getDoubleValue();
    }

    /**
     * Gets the Duration represented as a fraction of the current duration.
     * For example, if the current object is a quarter note, a ratio of 0.5 would return an eighth note.
     *
     * @param ratio The base note to calculate ratio over
     * @param ratio The ratio of the desired Duration to the current object.
     * @return The Duration best representing this ratio.
     */
    public static Duration getDurationByRatio(Duration base, double ratio) {
        Fraction ratioFraction = new Fraction(ratio);
        Fraction newDurationRatio = ratioFraction.multiply(base.durationValue);

        return fractionToDuration(newDurationRatio);
    }

    /**
     * Get the code for this Duration, E.g.
     * Quarter -> Q
     * Dotted eighth -> Ed
     *
     * @return The duration code for this Duration
     */
    public String getDurationCode() {
        int enumName = Duration.enumNames.indexOf(value.name());
        return strRepresentations.get(enumName) + (dot ? "d" : "");
    }

    /**
     * Get this Duration's value as a double.
     * @return The double representation of this duration.
     */
    public double getDoubleValue() {
        return durationValue.getDoubleValue();
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
        return "Duration: " + (dot ? "dotted " : "") + value.name().toLowerCase();
    }
}
