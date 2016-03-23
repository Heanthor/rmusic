import music.Note;
import music.Octave;
import music.NoteValue;

import static music.NoteValue.*;

/**
 * Test driver
 */
public class Main {
    public static void main(String[] args) {
        Note cs4 = new Note(NoteValue.C, Accidental.SHARP, new Octave(4));
        Note db4 = new Note(NoteValue.D, Accidental.FLAT, new Octave(4));

        System.out.println(cs4);
        System.out.println(db4);
    }
}
