package music.pitch;

/**
 * Represents an octave on a traditional keyboard. Just a wrapper on an integer.
 * Uses a singleton pattern to cut down on allocations.
 * @author reedt
 */
public class Octave implements Comparable<Octave> {
    private int octave;

    private static final Octave[] instances = new Octave[8];

    static {
        // Create static Octave instances
        for (int i = 1; i < 8; i++) {
            instances[i] = new Octave(i);
        }
    }

    private Octave(int octave) {
        this.octave = octave;
    }

    /**
     * Returns the object representing this octave.
     * @param octave Octave to get reference to
     * @return the Octave object
     */
    public static Octave getInstance(int octave) {
        return instances[octave];
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

    public int getNumberValue() {
        return this.octave;
    }

    @Override
    public int hashCode() {
        return octave;
    }

    @Override
    public int compareTo(Octave o) {
        return this.octave - o.octave;
    }
}
