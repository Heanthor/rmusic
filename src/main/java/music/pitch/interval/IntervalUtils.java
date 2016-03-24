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
     * Get the note located interval above baseNote.
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
            combinedValue = combinedValue - 12;

            Note tmp = Pitch.pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.modification, Octave.octaveFromInteger(baseNote.octave.getNumberValue() + 1), baseNote.duration);
        } else {
            Note tmp = Pitch.pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.modification, baseNote.octave, baseNote.duration);
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
                break;
            case "minor":
                break;
            case "diminished":
                break;
            case "augmented":
                break;
            default:
                throw new IllegalArgumentException("Invalid interval string (invalid interval type '" + intervalType + "').");
        }

        // TODO
        return null;
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
}
