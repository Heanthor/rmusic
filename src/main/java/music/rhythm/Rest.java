package music.rhythm;

import music.pitch.Note;
/**
 * Represents an amount of time with no pitch associated with it.
 *
 * @author reed
 */
public class Rest extends Note {
    /**
     * Create a Rest with a specified duration.
     * @param d The duration of the rest
     */
    public Rest(Duration d) {
        super(null, null, null, d);
    }

    @Override
    public String toString() {
        return "Rest, " + duration.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rest rest = (Rest) o;

        return this.duration.equals(rest.duration);
    }
}
