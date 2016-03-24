package music.pitch;

import com.google.common.collect.HashBiMap;
import music.pitch.interval.Interval;

import java.util.HashMap;

/**
 * Class that associates a number with a particular combination of base note and modification.
 * @author reedt
 */
public class Pitch {
    private Pitch() {}

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

    public static Note getNoteAbove(Note baseNote, Interval interval) {
        int intervalHalfSteps = interval.getNumHalfSteps();
        int baseNotePitchValue = getPitchValue(baseNote.basePitch, baseNote.modification);

        int combinedValue = intervalHalfSteps + baseNotePitchValue;
        if (combinedValue > 12) {
            combinedValue = combinedValue - 12;

            Note tmp = pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.modification, new Octave(baseNote.octave.getNumberValue() + 1), baseNote.duration);
        } else {
            Note tmp = pitchFromPitchValue(combinedValue);
            return new Note(tmp.basePitch, tmp.modification, baseNote.octave, baseNote.duration);
        }

    }

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
