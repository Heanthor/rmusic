package midi;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * @author reedt
 */
public class PlaySoundMidi {

    public static void main(String[] args) {
        int channelNumber = 0;
        int noteValue = 60;
        int velocity = 50;

        Synthesizer synthesizer = null;

        try {
            synthesizer = MidiSystem.getSynthesizer();

            synthesizer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        if (synthesizer != null) {
            MidiChannel[] channels = synthesizer.getChannels();
            MidiChannel currentChannel = channels[channelNumber];

            currentChannel.noteOn(noteValue ,50);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentChannel.noteOff(noteValue);
        }
    }
}
