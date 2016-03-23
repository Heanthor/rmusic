package music;

/**
 * Represents a specific note, containing information about its exact pitch.
 */
public class Note implements Comparable<Note> {
    private NoteValue basePitch;
    private NoteValue.Accidental modification;
    private int pitchValue;
    private Octave octave;

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     *
     * @param basePitch    The base pitch, A-G
     * @param accidental Any modification on the pitch
     * @param o            The octave it exists in, in scientific pitch notation
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental, Octave o) {
        this.basePitch = basePitch;
        this.modification = accidental;
        this.pitchValue = Pitch.getPitchValue(basePitch, accidental);
        this.octave = o;
    }

    /**
     * Parses a note string and creates its Note object representation.
     * Format: [Base note (uppercase)][Optional: # or b][Octave number]
     * @param noteString Note representation string
     */
    public Note(String noteString) {
        NoteValue nv;
        NoteValue.Accidental mod;
        Octave oct;

        try {
            nv = NoteValue.valueOf("" + noteString.charAt(0));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Note string format incorrect (Note name not found in first position).");
        }

        if (noteString.length() == 2) {
            // Note is natural, so last character must be octave
            mod = NoteValue.Accidental.NATURAL;

            try {
                // Octave constructor handles out of range octaves
                oct = new Octave(Integer.parseInt("" + noteString.charAt(1)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Note string format incorrect (No octave found).");
            }

        } else if (noteString.length() == 3) {
            // Note has modifications
            char modification = noteString.charAt(1);

            switch (modification) {
                case '#':
                    mod = NoteValue.Accidental.SHARP;
                    break;
                case 'b':
                    mod = NoteValue.Accidental.FLAT;
                    break;
                default:
                    throw new IllegalArgumentException("Note string format incorrect (note modification not found).");
            }

            try {
                oct = new Octave(Integer.parseInt("" + noteString.charAt(2)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Note string format incorrect (No octave found).");
            }

        } else {
            throw new IllegalArgumentException("Note string format incorrect (incorrect length).");
        }

        this.basePitch = nv;
        this.modification = mod;
        this.octave = oct;
        this.pitchValue = Pitch.getPitchValue(nv, mod);
    }

    public String toString() {
        String toReturn = basePitch.name();

        switch (modification) {
            case SHARP:
                toReturn += "#";
                break;
            case FLAT:
                toReturn += "b";
                break;
            default:
                // Add nothing for natural
                break;
        }

        toReturn += octave.toString();

        return toReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Note)) {
            return false;
        }

        Note n = (Note) obj;

        // Notes must be in the same octave to be equal

        // No corner case; notes are equal, octaves aren't
        if (!(this.octave.equals(n.octave)) && this.basePitch == n.basePitch) {
            return false;
        }

        // Octave changes on C, corner case
        if (this.basePitch == NoteValue.C || n.basePitch == NoteValue.C) {
            // Not more than one octave separates them
            if (Math.abs(n.octave.getNumberValue() - this.octave.getNumberValue()) > 1) {
                return false;
            }
        }

        // Enharmonic notes are equal pitch even though notated differently
        // Special case for wrapping around A# and Gb, or G# and Ab
        return this.pitchValue == 12 && n.pitchValue == 0
                || this.pitchValue == 0 && n.pitchValue == 12
                || this.pitchValue == n.pitchValue;
    }

    /**
     * A note is "less than" another note when it is a lower pitch, and higher when its pitch is higher.
     *
     * @param o Note to compare to
     * @return Negative, zero, or positive based on comparison
     */
    @Override
    public int compareTo(Note o) {
        // Covers enharmonic equivalence
        if (this.equals(o)) {
            return 0;
        }

        if (this.octave.getNumberValue() == o.octave.getNumberValue()) {
            // In same octave
            return this.pitchValue - o.pitchValue;
        } else {
            // In different octaves and not enharmonic
            return this.octave.getNumberValue() - o.octave.getNumberValue();
        }
    }
}
