package music.pitch;

import music.pitch.interval.MajorIntervals;
import music.pitch.interval.MinorIntervals;
import music.pitch.interval.PerfectIntervals;
import music.rhythm.Duration;
import music.rhythm.Rest;
import org.junit.Test;
import util.NoteBank;

import static org.junit.Assert.*;

/**
 * Created by reedt on 3/22/2016.
 */
public class ChordTest {
    // Minor 3rd
    Chord c1 = new Chord(NoteBank.a4, NoteBank.c4);
    Chord c2 = new Chord(NoteBank.c5, NoteBank.a4, NoteBank.c4);

    Chord c2_1 = new Chord(NoteBank.a4, MinorIntervals._3rd);

    // Cmaj triad
    Chord cmaj = new Chord(NoteBank.c4, NoteBank.e, new Note("G4:Q"));
    Chord cmaj_2 = new Chord(NoteBank.c4, MajorIntervals._3rd, PerfectIntervals.PERFECT_FIFTH);

    // Gm7
    Chord gmin7 = new Chord(NoteBank.g3, new Note("Bb4:Q"), new Note("D4:Q"), new Note("F4:Q"));
    Chord gmin7_2 = new Chord(NoteBank.g3, MinorIntervals._3rd, PerfectIntervals.PERFECT_FIFTH, MinorIntervals._7th);

    @Test
    public void testIntervalConstructor() {
        assertEquals(c1, c2_1);
        assertEquals(cmaj, cmaj_2);

        assertEquals(gmin7, gmin7_2);

        assertEquals("C4-E4-G4", cmaj_2.toString());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("A4-C4", c1.toString());
        assertEquals("A4-C4-C5", c2.toString());


    }
}