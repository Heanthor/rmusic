package midi;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import music.play.Voice;
import music.play.key.MajorSharpKeys;
import music.rhythm.Duration;
import music.rhythm.Rest;

import java.util.ArrayList;

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

    public static void main(String[] args) {
        PlaySoundMidi m = new PlaySoundMidi();
        Tempo t = new Tempo(new Duration("Q"), Tempo.CommonTempos.ALLEGRO.bpm);

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

        Staff mozart545 = new Staff(t,
                MajorSharpKeys.C,
                new TimeSignature(4, TimeSignature.DenominatorChoices._4),
                new Voice[]{new Voice(notes)});

        m.playStaff(mozart545);

    }

    private void playNote(BasicNote n, Tempo tempo, int channelNumber) {
        int velocity = 50; // Volume

        int noteDuration = MidiUtils.getDurationMiliseconds(n.getDuration(), tempo);

        if (!(n instanceof Rest)) {
            Note temp = (Note)n;
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

    private void playStaff(Staff f) {
        Tempo t = f.tempo;

        // Warm up thread sleep
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Voice v: f.voices) {
            for (BasicNote n: v.melody) {
                playNote(n, t, 0);
            }
        }
    }
}
