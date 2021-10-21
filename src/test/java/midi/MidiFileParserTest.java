package midi;

import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import music.play.Voice;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by reedtrevelyan on 12/9/17.
 */
public class MidiFileParserTest {
    private File basic0 = null;
    private File basic1 = null;
    private File basic2 = null;
    private File basic3 = null;
    private File basic4 = null;
    private File basic5 = null;
    private File basic6 = null;
    private File basic7 = null;

    @Before
    public void setUp() {
        basic0 = new File("bin/midifiles/test/test_basicnotes.mid");
        basic1 = new File("bin/midifiles/test/test_simplemelody.mid");
        basic2 = new File("bin/midifiles/test/test_multiplevoices.mid");
        basic3 = new File("bin/midifiles/test/test_singlevoiceoctaves.mid");
        basic4 = new File("bin/midifiles/test/test_multiplevoicesrest.mid");
        basic5 = new File("bin/midifiles/test/test_chordrests.mid");
        basic6 = new File("bin/midifiles/test/test_repeatednotes.mid");
        basic7 = new File("bin/midifiles/test/test_longrest.mid");
    }

    @Test
    public void testBasicNotes() {
        testMidi(basic0, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("D4:H,R:Q,D4:Q")}
        ));
    }

    @Test
    public void testSimpleMelody() {
        testMidi(basic1, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("G4:Q,D4:Q,R:Q,G4:Q")}
        ));
    }

    @Test
    public void testMultipleVoices() {
        testMidi(basic2, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{
                        new Voice("C4:H", 0),
                        new Voice("R:E,E4:Qd", 1),
                        new Voice("R:Q,G4:Q", 2),
                        new Voice("R:Qd,C5:E", 3)}
        ));
    }

    @Test
    public void testSingleVoiceOctaves() {
        testMidi(basic3, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("C4:S,C5:S,B3:S,B4:S,A#3:S,A#4:S,A3:S,A4:S")}
        ));
    }

    @Test
    public void testMultipleVoicesWithRest() {
        testMidi(basic4, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{
                        new Voice("D4:H,R:E,D#4:Qd", 0),
                        new Voice("R:Q,D#4:E,R:Qd,D4:Q", 1)}
        ));
    }

    @Test
    public void testChordsRests() {
        // TODO should midi sort voices by octave to keep lower voices in the bass always?
        testMidi(basic5, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{
                        new Voice("F4:Q,R:Q,F4:E,R:E,F4:E", 0),
                        new Voice("B3:Q,R:Q,B3:E,R:E,B3:E", 1),
                        new Voice("D4:Q,R:Q,D4:E,R:E,D4:E", 2),
                        new Voice("G3:Q,R:Q,G3:E,R:E,G3:E", 3)}
        ));
    }

    @Test
    public void testRepeatedNotes() {
        testMidi(basic6, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("D4:H,D4:E,D4:E,D4:S,D4:S,D4:S,D4:S")}
        ));
    }

    @Test
    public void testLongRest() {
        // ensure that extra voices which need more than a whole note of rest wait long enough
        testMidi(basic7, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("D3:Q,D#3:Q,E3:Q,F3:Q,F#3:Q,G3:Q,A3:Q,B3:Q,C4:Q", 0),
                            new Voice("R:W,D3:Q,E3:Q,F#3:Q,G3:Q,A3:Q", 1),
                            new Voice("R:W,R:W,F#3:Q", 2),
                            new Voice("R:W,R:W,D3:Q", 3)}
        ));
    }

    /**
     * Test helper.
     *
     * @param f        Midi file to open
     * @param expected Expected conversion
     */
    private void testMidi(File f, Staff expected) {
        MidiFileParser m = new MidiFileParser();
        Staff parsed = null;

        try {
            parsed = m.loadAndParseFile(f);
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, parsed);
    }
}
