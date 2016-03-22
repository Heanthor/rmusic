package music;

/**
 * Represents an octave on a traditional keyboard.
 */
public class Octave implements Comparable<Octave>{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Octave octave1 = (Octave) o;

        return octave == octave1.octave;
    }

    @Override
    public int hashCode() {
        return octave;
    }

    @Override
    public int compareTo(Octave o) {
        return o.octave - this.octave;
    }

    public int getNumberValue() {
        return this.octave;
    }
}
