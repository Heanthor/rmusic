import music.Note;
import music.Octave;
import music.NoteValues;

import static music.NoteValues.*;

/**
 * Test driver
 */
public class Main {
    public static void main(String[] args) {
        Note cs4 = new Note(NoteValues.C, Type.SHARP, new Octave(4));
        Note db4 = new Note(NoteValues.D, Type.FLAT, new Octave(4));

        System.out.println(cs4);
        System.out.println(db4);
    }
}
