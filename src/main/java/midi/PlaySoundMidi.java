package midi;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import music.play.Voice;
import music.play.key.MajorSharpKeys;
import music.rhythm.Rest;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * @author reedt
 */
public class PlaySoundMidi {
    private Synthesizer synthesizer;

    public PlaySoundMidi() {
        try {
            synthesizer = MidiSystem.getSynthesizer();

            synthesizer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        PlaySoundMidi m = new PlaySoundMidi();
        MidiFileParser parser = new MidiFileParser();
        Tempo t = new Tempo(Tempo.CommonTempos.ALLEGRO);

        ArrayList<BasicNote> notes = new ArrayList<>();
        notes.add(new Note("C5:H"));
        notes.add(new Note("E5:Q"));
        notes.add(new Note("G5:Q"));
        notes.add(new Note("B4:Qd"));
        notes.add(new Note("C5:S"));
        notes.add(new Note("D5:S"));
        notes.add(new Note("C5:Q"));
        notes.add(Rest.parseRestString("R:Q"));

        notes.add(new Note("A5:H"));
        notes.add(new Note("G5:Q"));
        notes.add(new Note("C6:Q"));
        notes.add(new Note("G5:Q"));
        notes.add(new Note("F5:T"));
        notes.add(new Note("G5:T"));
        notes.add(new Note("F5:T"));
        notes.add(new Note("G5:T"));
        notes.add(new Note("E5:S"));
        notes.add(new Note("F5:S"));
        notes.add(new Note("E5:H"));

        Staff mozartK545 = new Staff(t,
                MajorSharpKeys.C,
                new TimeSignature(4, TimeSignature.DenominatorChoices._4),
                new Voice[]{new Voice(notes)});

        try {
//            m.playStaff(mozartK545);
//            Staff s = parser.loadAndParseFile(new File("bin/midifiles/for_elise_by_beethoven.mid"));
//            Staff s = parser.loadAndParseFile(new File("bin/midifiles/tchop35a.mid"));
            Staff s = parser.loadAndParseFile(new File("bin/midifiles/mz_331_1.mid"));
//            Staff s = parser.loadAndParseFile(new File("bin/midifiles/test/test_longrest.mid"));
            System.out.println(s);
            m.playStaff(s);
        } catch (InvalidMidiDataException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playNote(BasicNote n, Tempo tempo, int channelNumber) {
        int velocity = 50; // Volume

        int noteDuration = MidiUtils.getDurationMiliseconds(n.getDuration(), tempo);

        if (!(n instanceof Rest)) {
            Note temp = (Note) n;
            int noteValue = MidiUtils.getMidiNote(temp);

            MidiChannel[] channels = synthesizer.getChannels();
            MidiChannel currentChannel = channels[channelNumber];

            currentChannel.noteOn(noteValue, velocity);

            try {
                Thread.sleep(noteDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentChannel.noteOff(noteValue);
        } else {
            // Rest
            try {
                Thread.sleep(noteDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void playStaff(Staff f) throws InterruptedException {
        Tempo t = f.tempo;

        // Warm up thread sleep
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // start all threads at same time for more precise playback
        CountDownLatch countDownLatch = new CountDownLatch(f.voices.size() - 1);

        for (Voice v : f.voices) {
            new Thread(() -> {
                try {
                    countDownLatch.countDown();
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (BasicNote n : v.melody) {
                    playNote(n, t, 0);
                }
            }).start();
        }
    }
}
