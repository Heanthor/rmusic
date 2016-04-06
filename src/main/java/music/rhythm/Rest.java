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

    public static Rest parseRestString(String restString) {
        Duration d;
        if (restString.charAt(0) != 'R') {
            throw new IllegalArgumentException("Invalid rest string provided " + restString + ".");
        } else {
            d = Duration.parseDurationString(restString.substring(2));

            return new Rest(d);
        }
    }

    @Override
    public String toString() {
        return "Rest, " + super.getDuration().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rest rest = (Rest) o;

        return super.getDuration().equals(rest.getDuration());
    }

    public String toNoteString() {
        return "R:" + super.getDuration().getDurationCode();
    }
}
