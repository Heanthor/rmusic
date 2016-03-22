package music;

/**
 * Represents a specific note, containing information about its exact pitch.
 */
public class Note {
    private final Pitch p;
    private final Pitch.Type t;
    private final Octave o;

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     * @param p The base pitch, A-G
     * @param t Any modification on the pitch
     * @param o The octave it exists in, in scientific pitch notation
     */
    public Note(Pitch p, Pitch.Type t, Octave o) {
        this.p = p;
        this.t = t;
        this.o = o;
    }

    public String toString() {
        String toReturn = p.name();

        switch (t) {
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

        toReturn += o.toString();

        return toReturn;
    }
}
