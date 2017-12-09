package music.play.key;

import music.pitch.Note;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by reedtrevelyan on 3/24/16.
 */
public class KeySignatureTest {
    ArrayList<Note> sigDmaj = new ArrayList<>(Arrays.asList(new Note("F4"), new Note("C4")));
    ArrayList<Note> sigBbmaj = new ArrayList<>(Arrays.asList(new Note("B4"), new Note("E4")));
    ArrayList<Note> sigBmaj = new ArrayList<>(Arrays.asList(
            new Note("F4"),
            new Note("C4"),
            new Note("G4"),
            new Note("D4"),
            new Note("A4")));

    ArrayList<Note> abm = new ArrayList<>(Arrays.asList(
            new Note("B4"),
            new Note("E4"),
            new Note("A4"),
            new Note("D4"),
            new Note("G4"),
            new Note("C4"),
            new Note("F4")));

    @Test
    public void testGetKeySignature() {
        assertEquals(sigDmaj, new KeySignature(MajorSharpKeys.D).getKeySignature());
        assertEquals(sigBmaj, new KeySignature(MajorSharpKeys.B).getKeySignature());
        assertEquals(sigBbmaj, new KeySignature(MajorFlatKeys.Bb).getKeySignature());
        assertEquals(abm, new KeySignature(MinorFlatKeys.Ab).getKeySignature());

        // C major and A minor have no sharps or flats
        assertEquals(new ArrayList<Note>(), new KeySignature(MajorSharpKeys.C).getKeySignature());
        assertEquals(new ArrayList<Note>(), new KeySignature(MinorSharpKeys.A).getKeySignature());

    }

    @Test
    public void testFindKeySignature() {
        assertEquals(sigDmaj, new KeySignature(2, true, true).getKeySignature());
        assertEquals(sigBbmaj, new KeySignature(2, false, true).getKeySignature());
        assertEquals(sigBmaj, new KeySignature(5, true, true).getKeySignature());
    }
}