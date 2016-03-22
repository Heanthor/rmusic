package music;

import java.util.HashMap;

/**
 * Class that associates a number with a particular combination of base note and modification.
 */
public class Pitch {
    private Pitch() {}

    private static final HashMap<NoteValues, Integer> numValues = new HashMap<NoteValues, Integer>();
    private static final HashMap<NoteValues.Type, Integer> modificationValues = new HashMap<NoteValues.Type, Integer>();

    static {
        // Create association between pitch and number
        // Each increment represents one half-step
        numValues.put(NoteValues.A, 1);
        numValues.put(NoteValues.B, 3);
        numValues.put(NoteValues.C, 4);
        numValues.put(NoteValues.D, 6);
        numValues.put(NoteValues.E, 8);
        numValues.put(NoteValues.F, 9);
        numValues.put(NoteValues.G, 11);

        // Associate note modifications to values
        modificationValues.put(NoteValues.Type.NATURAL, 0);
        modificationValues.put(NoteValues.Type.SHARP, 1);
        modificationValues.put(NoteValues.Type.FLAT, -1);
    }

    public static int getPitchValue(NoteValues p, NoteValues.Type t) {
        return numValues.get(p) + modificationValues.get(t);
    }
}
