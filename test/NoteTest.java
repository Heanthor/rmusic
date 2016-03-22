import music.Note;
import music.NoteValues;
import music.Octave;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by reedtrevelyan on 3/22/16.
 */
public class NoteTest {
    @Test
    public void testEnharmonic() {
        // C# and Db
        Note cs = new Note(NoteValues.C, NoteValues.Type.SHARP, new Octave(4));
        Note db = new Note(NoteValues.D, NoteValues.Type.FLAT, new Octave(4));

        assertEquals(cs, db);

        // E and Fb
        Note e = new Note(NoteValues.E, NoteValues.Type.NATURAL, new Octave(4));
        Note fb = new Note(NoteValues.F, NoteValues.Type.FLAT, new Octave(4));

        assertEquals(e, fb);

        // Ab and G# are enharmonic, also wraps around the number representation
        Note ab = new Note(NoteValues.A, NoteValues.Type.FLAT, new Octave(4));
        Note gs = new Note(NoteValues.G, NoteValues.Type.SHARP, new Octave(4));

        assertEquals(ab, gs);
    }

    @Test
    public void testOctaveEquals() {
        // Octaves should not be equal
        Note cs4 = new Note(NoteValues.C, NoteValues.Type.SHARP, new Octave(4));
        Note cs5 = new Note(NoteValues.C, NoteValues.Type.SHARP, new Octave(5));

        assertNotEquals(cs4, cs5);

        // B4 and Cb5 should be enharmonic, since C denotes the new octave
        Note b4 = new Note(NoteValues.B, NoteValues.Type.NATURAL, new Octave(4));
        Note cb5 = new Note(NoteValues.C, NoteValues.Type.FLAT, new Octave(5));

        assertEquals(b4, cb5);

        // B#4 and C5
        Note bs4 = new Note(NoteValues.B, NoteValues.Type.SHARP, new Octave(4));
        Note c5 = new Note(NoteValues.C, NoteValues.Type.NATURAL, new Octave(5));

        assertEquals(bs4, c5);

        // B3 and Cb5 are not equal
        Note b3 = new Note(NoteValues.B, NoteValues.Type.NATURAL, new Octave(3));

        assertNotEquals(b3, cb5);

        // B#3 and C4 are enharmonic
        Note bs3 = new Note(NoteValues.B, NoteValues.Type.SHARP, new Octave(3));
        Note c4 = new Note(NoteValues.C, NoteValues.Type.NATURAL, new Octave(4));

        assertEquals(bs3, c4);

        // G3 and G5, general case
        Note g3 = new Note(NoteValues.G, NoteValues.Type.NATURAL, new Octave(3));
        Note g5 = new Note(NoteValues.G, NoteValues.Type.NATURAL, new Octave(5));

        assertNotEquals(g3, g5);
    }
}