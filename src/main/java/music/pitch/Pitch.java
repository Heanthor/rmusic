package music.pitch;

import com.google.common.collect.HashBiMap;
import music.pitch.interval.*;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Class that associates a number with a particular combination of base note and accidental.
 *
 * @author reedt
 */
public class Pitch {
    private Pitch() {
    }

    private static final HashBiMap<NoteValue, Integer> numValues = HashBiMap.create();
    private static final HashMap<NoteValue.Accidental, Integer> modificationValues = new HashMap<>();

    static {
        // Create association between pitch and integer
        // Each increment represents one half-step
        numValues.put(NoteValue.C, 1);
        numValues.put(NoteValue.D, 3);
        numValues.put(NoteValue.E, 5);
        numValues.put(NoteValue.F, 6);
        numValues.put(NoteValue.G, 8);
        numValues.put(NoteValue.A, 10);
        numValues.put(NoteValue.B, 12);

        // Associate note modifications to values
        modificationValues.put(NoteValue.Accidental.NATURAL, 0);
        modificationValues.put(NoteValue.Accidental.SHARP, 1);
        modificationValues.put(NoteValue.Accidental.FLAT, -1);
    }

    /**
     * Combine a NoteValue and Accidental into an integer pitch value
     * @param p note value
     * @param t accidental
     * @return the internal representation of these note parts
     */
    public static int getPitchValue(NoteValue p, NoteValue.Accidental t) {
        if (p == null || t == null) {
            return -1;
        } else {
            return numValues.get(p) + modificationValues.get(t);
        }
    }

    /**
     * Allows inverse gets of noteValue from integer. The Note returned will either be natural or sharp.
     *
     * @param pitchValue Pitch value
     * @return a Note (will null octave and duration) represented by the pitch value.
     */
    public static Note pitchFromPitchValue(int pitchValue) {
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
