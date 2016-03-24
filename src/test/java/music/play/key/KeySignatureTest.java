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

    @Test
    public void testGetKeySignature() throws Exception {
        ArrayList<Note> sigDmaj = new ArrayList<>(Arrays.asList(new Note[]{new Note("F4:Q"), new Note("C4:Q")}));
        ArrayList<Note> sigBb = new ArrayList<>(Arrays.asList(new Note[]{new Note("B4:Q"), new Note("E4:Q")}));
        ArrayList<Note> sigBmaj = new ArrayList<>(Arrays.asList(
                new Note[]{
                        new Note("F4:Q"),
                        new Note("C4:Q"),
                        new Note("G4:Q"),
                        new Note("D4:Q"),
                        new Note("A4:Q")}));

        ArrayList<Note> abm = new ArrayList<>(Arrays.asList(
                new Note[]{
                        new Note("B4:Q"),
                        new Note("E4:Q"),
                        new Note("A4:Q"),
                        new Note("D4:Q"),
                        new Note("G4:Q"),
                        new Note("C4:Q"),
                        new Note("F4:Q")}));


        assertEquals(sigDmaj, new KeySignature(MajorSharpKeys.D).getKeySignature());
        assertEquals(sigBmaj, new KeySignature(MajorSharpKeys.B).getKeySignature());
        assertEquals(sigBb, new KeySignature(MajorFlatKeys.Bb).getKeySignature());
        assertEquals(abm, new KeySignature(MinorFlatKeys.Ab).getKeySignature());

        // C major and A minor have no sharps or flats
        assertEquals(new ArrayList<Note>(), new KeySignature(MajorSharpKeys.C).getKeySignature());
        assertEquals(new ArrayList<Note>(), new KeySignature(MinorSharpKeys.A).getKeySignature());

    }
}