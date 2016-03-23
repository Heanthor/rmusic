package music;

import java.util.HashMap;

/**
 * Class that associates a number with a particular combination of base note and modification.
 * @author reedt
 */
public class Pitch {
    private Pitch() {}

    private static final HashMap<NoteValue, Integer> numValues = new HashMap<NoteValue, Integer>();
    private static final HashMap<NoteValue.Accidental, Integer> modificationValues = new HashMap<NoteValue.Accidental, Integer>();

    static {
        // Create association between pitch and number
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
        return numValues.get(p) + modificationValues.get(t);
    }
}
