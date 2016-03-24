package music.pitch;

import com.google.common.collect.HashBiMap;
import music.pitch.interval.*;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Class that associates a number with a particular combination of base note and modification.
 *
 * @author reedt
 */
public class Pitch {
    private Pitch() {
    }

    private static final HashBiMap<NoteValue, Integer> numValues = HashBiMap.create();
    private static final HashMap<NoteValue.Accidental, Integer> modificationValues = new HashMap<NoteValue.Accidental, Integer>();

    static {
        // Create association between pitch and integer
        // Each increment represents one half-step
        numValues.put(NoteValue.A, 1);
        numValues.put(NoteValue.B, 3);
        numValues.put(NoteValue.C, 4);
        numValues.put(NoteValue.D, 6);
        numValues.put(NoteValue.E, 8);
        numValues.put(NoteValue.F, 9);
        numValues.put(NoteValue.G, 11);

        // Associate note modifications to values
        modificationValues.put(NoteValue.Accidental.NATURAL, 0);
        modificationValues.put(NoteValue.Accidental.SHARP, 1);
        modificationValues.put(NoteValue.Accidental.FLAT, -1);
    }

    public static int getPitchValue(NoteValue p, NoteValue.Accidental t) {
        if (p == null || t == null) {
            return -1;
        } else {
            return numValues.get(p) + modificationValues.get(t);
        }
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

            Note tmp = pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.modification, Octave.octaveFromInteger(baseNote.octave.getNumberValue() + 1), baseNote.duration);
        } else {
            Note tmp = pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.modification, baseNote.octave, baseNote.duration);
        }
    }

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
     * Allows inverse gets of noteValue from integer. The Note returned will either be natural or sharp.
     *
     * @param pitchValue Pitch value
     * @return a Note (will null octave and duration) represented by the pitch value.
     */
    private static Note pitchFromPitchValue(int pitchValue) {
        if (numValues.inverse().containsKey(pitchValue)) {
            // Note is natural, appears in our table
            return new Note(numValues.inverse().get(pitchValue), NoteValue.Accidental.NATURAL, null, null);
        } else {
            if (pitchValue == 0) {
                // Wrap around
                pitchValue = 12;
            }

            // Return lower pitch, plus a sharp
            // Can be converted into appropriate sharp/flat based on key signature
            return new Note(numValues.inverse().get(pitchValue - 1), NoteValue.Accidental.SHARP, null, null);
        }
    }
}
