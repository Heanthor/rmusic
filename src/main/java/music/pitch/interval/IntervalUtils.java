package music.pitch.interval;

import music.pitch.Note;
import music.pitch.Octave;
import music.pitch.Pitch;

import java.util.Arrays;

/**
 * Contains static methods useful for working with Intervals
 * @author reedt
 */
public class IntervalUtils {
    private IntervalUtils() {}
    /**
     * Returns an Interval representing the distance between two notes.
     * Searches for an interval in this order:
     * <ol>
     * <li>Perfect</li>
     * <li>Major</li>
     * <li>Minor</li>
     * <li>Diminished</li>
     * <li>Augmented</li>
     * </ol>
     * <p>
     * Note: the two notes cannot be more than one octave apart.
     *
     * @param low  The low Note
     * @param high the high Note
     * @return An Interval representing the distance between the two notes
     */
    public static Interval getIntervalBetween(Note low, Note high) {
        if (high.compareTo(low) < 0) {
            throw new IllegalArgumentException("High note lower than low note.");
        } else if (high.octave.getNumberValue() - low.octave.getNumberValue() > 1) {
            throw new IllegalArgumentException("Notes can be at most one octave apart.");
        }

        int differenceValue;

        if (low.octave == high.octave) {
            differenceValue = high.pitchValue - low.pitchValue;
        } else {
            // High must be (one) octave higher than low
            differenceValue = high.pitchValue + 12 - low.pitchValue;
        }

        // First look for perfect interval, then major, then minor, then diminished, then augmented
        for (Interval[] i : Arrays.asList(PerfectIntervals.values(), MajorIntervals.values(), MinorIntervals.values(),
                DiminishedIntervals.values(), AugmentedIntervals.values())) {
            for (Interval j : i) {
                if (j.getNumHalfSteps() == differenceValue) {
                    return j;
                }
            }
        }

        // No nullpointer here
        return PerfectIntervals.ERROR_INTERVAL;
    }

    /**
     * Get the Note located interval above baseNote.
     * the returned Note has the same Duration as baseNote.
     *
     * @param baseNote base note.
     * @param interval Interval to use.
     * @return A Note which represents the pitch located the interval above the base note.
     */
    public static Note getNoteAbove(Note baseNote, Interval interval) {
        int intervalHalfSteps = interval.getNumHalfSteps();

        int combinedValue = intervalHalfSteps + baseNote.pitchValue;
        if (combinedValue > 12) {
            // Up one octave
            combinedValue -= 12;

            Note tmp = Pitch.pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.accidental, Octave.octaveFromInteger(baseNote.octave.getNumberValue() + 1),
                    baseNote.getDuration());
        } else {
            Note tmp = Pitch.pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.accidental, baseNote.octave, baseNote.getDuration());
        }
    }

    public static Note getNoteBelow(Note baseNote, Interval interval) {
        int intervalHalfSteps = interval.getNumHalfSteps();

        int combinedValue = baseNote.pitchValue - intervalHalfSteps;

        if (combinedValue < 0) {
            // Down one octave
            combinedValue += 12;

            Note tmp = Pitch.pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.accidental, Octave.octaveFromInteger(baseNote.octave.getNumberValue() - 1),
                    baseNote.getDuration());
        } else {
            Note tmp = Pitch.pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.accidental, baseNote.octave, baseNote.getDuration());
        }
    }

    public static Interval stringToInterval(String intervalString) {
        if (intervalString.indexOf(' ') == -1) {
            throw new IllegalArgumentException("Invalid interval string (missing space - invalid format).");
        }

        String intervalType = intervalString.substring(0, intervalString.indexOf(' ')).toLowerCase();
        String interval = intervalString.substring(intervalString.indexOf(' ') + 1);

        switch (intervalType) {
            case "perfect":
                return parsePerfectInterval(interval);
            case "major":
                return parseMajorMinorIntervals(interval, true);
            case "minor":
                return parseMajorMinorIntervals(interval, false);
            case "diminished":
                return parseDiminishedAugmentedIntervals(interval, true);
            case "augmented":
                return parseDiminishedAugmentedIntervals(interval, false);
            default:
                throw new IllegalArgumentException("Invalid interval string (invalid interval type '" + intervalType + "').");
        }
    }

    private static Interval parsePerfectInterval(String intervalString) {
        Interval toReturn;

        switch (intervalString) {
            case "unison":
                toReturn = PerfectIntervals.PERFECT_UNISON;
                break;
            case "fourth":
            case "4th":
                toReturn = PerfectIntervals.PERFECT_FOURTH;
                break;
            case "fifth":
            case "5th":
                toReturn = PerfectIntervals.PERFECT_FIFTH;
                break;
            case "octave":
            case "8th":
                toReturn = PerfectIntervals.OCTAVE;
                break;
            default:
                throw new IllegalArgumentException("Invalid interval string (invalid perfect interval '" + intervalString + "').");
        }

        return toReturn;
    }

    private static Interval parseMajorMinorIntervals(String intervalString, boolean major) {
        Interval toReturn;

        switch (intervalString) {
            case "second":
            case "2nd":
                toReturn = major ? MajorIntervals._2nd : MinorIntervals._2nd;
                break;
            case "third":
            case "3rd":
                toReturn = major ? MajorIntervals._3rd : MinorIntervals._3rd;
                break;
            case "sixth":
            case "6th":
                toReturn = major ? MajorIntervals._6th : MinorIntervals._6th;
                break;
            case "seventh":
            case "7th":
                toReturn = major ? MajorIntervals._7th : MinorIntervals._7th;
                break;
            default:
                throw new IllegalArgumentException("Invalid interval string (invalid perfect interval '" + intervalString + "').");
        }

        return toReturn;
    }

    private static Interval parseDiminishedAugmentedIntervals(String intervalString, boolean diminished) {
        Interval toReturn;

        switch (intervalString) {
            case "second":
            case "2nd":
                toReturn = diminished ? DiminishedIntervals._2nd : AugmentedIntervals._2nd;
                break;
            case "third":
            case "3rd":
                toReturn = diminished ? DiminishedIntervals._3rd : AugmentedIntervals._3rd;
                break;
            case "fourth":
            case "4th":
                toReturn = diminished ? DiminishedIntervals._4th : AugmentedIntervals._4th;
                break;
            case "fifth":
            case "5th":
                toReturn = diminished ? DiminishedIntervals._5th : AugmentedIntervals._5th;
                break;
            case "sixth":
            case "6th":
                toReturn = diminished ? DiminishedIntervals._6th : AugmentedIntervals._6th;
                break;
            case "seventh":
            case "7th":
                toReturn = diminished ? DiminishedIntervals._7th : AugmentedIntervals._7th;
                break;
            default:
                throw new IllegalArgumentException("Invalid interval string (invalid perfect interval '" + intervalString + "').");
        }

        return toReturn;
    }
}
