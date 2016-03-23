import music.Chord;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by reedt on 3/22/2016.
 */
public class ChordTest {
    // Major 3rd
    Chord c1 = new Chord(NoteBank.a4, NoteBank.c4);
    Chord c2 = new Chord(NoteBank.c5, NoteBank.a4, NoteBank.c4);

    @Test
    public void testToString() throws Exception {
        assertEquals(c1.toString(), "A4-C4");
        assertEquals(c2.toString(), "A4-C4-C5");
    }
}