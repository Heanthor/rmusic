import music.Note;
import music.Octave;
import music.Pitch;

import static music.Pitch.*;

/**
 * Test driver
 */
public class Main {
    public static void main(String[] args) {
        Note n = new Note(Pitch.C, Type.SHARP, new Octave(4));
        System.out.println(n);
    }
}
