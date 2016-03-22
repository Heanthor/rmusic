package music;

/**
 * Represents a specific note, containing information about its exact pitch.
 *
 */
public class Note implements Comparable<Note> {
    private NoteValues basePitch;
    private NoteValues.Type modification;
    private int pitchValue;
    private Octave octave;

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     * @param basePitch The base pitch, A-G
     * @param modification Any modification on the pitch
     * @param o The octave it exists in, in scientific pitch notation
     */
    public Note(NoteValues basePitch, NoteValues.Type modification, Octave o) {
        this.basePitch = basePitch;
        this.modification = modification;
        this.pitchValue = Pitch.getPitchValue(basePitch, modification);
        this.octave = o;
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
        if (this.basePitch == NoteValues.C || n.basePitch == NoteValues.C) {
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
