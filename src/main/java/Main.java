import music.pitch.Note;
import music.pitch.Octave;
import music.pitch.NoteValue;
import music.rhythm.Duration;

import static music.pitch.NoteValue.*;

/**
 * Test driver
 * @author reedt
 */
public class Main {
    public static void main(String[] args) {
        Note cs4 = new Note(NoteValue.C, Accidental.SHARP, Octave.getInstance(4), new Duration(Duration.DurationValue.QUARTER, false));
        Note db4 = new Note(NoteValue.D, Accidental.FLAT, Octave.getInstance(4), new Duration(Duration.DurationValue.QUARTER, false));

        System.out.println(cs4);
        System.out.println(db4);
    }
}
