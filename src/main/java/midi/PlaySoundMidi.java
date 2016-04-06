package midi;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import music.pitch.Note;
import music.play.Tempo;
import music.rhythm.Duration;

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
        m.playNote(new Note("C4:H"), t);
        m.playNote(new Note("D4:Q"), t);
        m.playNote(new Note("E4:Q"), t);
        m.playNote(new Note("F4:Q"), t);
        m.playNote(new Note("G4:Q"), t);
        m.playNote(new Note("A4:Q"), t);
        m.playNote(new Note("B4:Q"), t);
        m.playNote(new Note("C5:W"), t);
    }

    private void playNote(Note n, Tempo tempo) {
        int channelNumber = 0;
        int velocity = 50;

        int noteValue = MidiUtils.getMidiNote(n);

        MidiChannel[] channels = synthesizer.getChannels();
        MidiChannel currentChannel = channels[channelNumber];

        currentChannel.noteOn(noteValue, velocity);

        int noteDuration = MidiUtils.getDurationMiliseconds(n.duration, tempo);

        try {
            Thread.sleep(noteDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        currentChannel.noteOff(noteValue);
    }
}
