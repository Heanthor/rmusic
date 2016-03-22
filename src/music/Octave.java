package music;

/**
 * Represents an octave on a traditional keyboard.
 */
public class Octave {
    private int octave;

    public Octave(int octave) {
        if (octave < 1 || octave > 8) {
            throw new IllegalArgumentException("Octave out of range (1 - 8)");
        }

        this.octave = octave;
    }

    public String toString() {
        return "" + octave;
    }
}
