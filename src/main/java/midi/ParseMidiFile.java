package midi;

import music.play.key.Key;
import music.play.key.KeySignature;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.*;

public class ParseMidiFile {
    // Sami Koivu on StackOverflow for skeleton

    // meta flags
    private static final int CHANNEL_PREFIX = 0x20;
    private static final int COPYRIGHT_NOTICE = 0x02;
    private static final int TRACK_NAME = 0x03;
    private static final int INSTRUMENT_NAME = 0x04;
    private static final int TEMPO_MARKING = 0x51;
    private static final int TIME_SIGNATURE = 0x58;
    private static final int KEY_SIGNATURE = 0x59;
    private static final int END_OF_TRACK = 0x2f;
    private static final int SEQUENCE_SPECIFIER = 0x7f;

    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    /**
     * Currently active channel - changes during execution!
     */
    private int channelNumber = -1;

    public static void main(String[] args) throws Exception {
        new ParseMidiFile().init();
    }

    private void init() throws InvalidMidiDataException, IOException {
        Sequence furElise = MidiSystem.getSequence(new File("bin/midifiles/for_elise_by_beethoven.mid"));
        Sequence tchaikovskyVC1 = MidiSystem.getSequence(new File("bin/midifiles/tchop35a.mid"));

        parseMidiFile(tchaikovskyVC1);
    }

    private void parseMidiFile(Sequence sequence) {
        int trackNumber = 0;
        // loops chunks
        for (Track track : sequence.getTracks()) {

            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();

                // Channel voice, channel mode, system common, system realtime
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;

                    System.out.print("Channel: " + sm.getChannel() + " ");

                    if (sm.getCommand() == ShortMessage.NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();

                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();

                        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else {
                        System.out.println("Command: " + sm.getCommand());
                    }
                } else {
                    // Meta events
                    if (message instanceof MetaMessage) {
                        parseMetaMessage((MetaMessage) message);
                    } else {
                        // seems to never happen
                        System.out.println("Other message: " + message.getClass());
                    }
                }
            }

            System.out.println();
        }
    }

    private void parseMetaMessage(MetaMessage message) {
        byte[] raw = message.getData();

        int[] hexBytes = signedToUnsignedBytes(raw);

        if (message.getType() == TEMPO_MARKING) {
//                            int tempo = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);

            // microseconds per quarter note
            // tempo = tt tt tt
            int tempo = bigEndianByteArrayToDecimal(raw);
            // convert to beats per minute
            int bpm = 60000000 / tempo;
            System.out.printf("Tempo marking: %d bpm\n", bpm);
        } else if (message.getType() == TIME_SIGNATURE) {
            int numerator = hexBytes[0];
            int denominator = (int) Math.pow(2, hexBytes[1]);
            // MIDI clock ticks per click
            int metronomeTicks = hexBytes[2];
            int thirtySecondNotesPerBeat = hexBytes[3];

            System.out.println("Time signature: " + numerator + "/" + denominator);
        } else if (message.getType() == KEY_SIGNATURE) {
            boolean major = hexBytes[1] == 0;
            boolean sharps = hexBytes[0] > 0;
            Key keySignature = new KeySignature(hexBytes[0], sharps, major).getKey();
            System.out.println("Key signature: " + keySignature.toString());
        } else if (message.getType() == TRACK_NAME) {
            System.out.println("Track name: " + decimalToString(hexBytes));
        } else if (message.getType() == CHANNEL_PREFIX) {
            // Might be unused, if channel info can be extracted from command message status
            channelNumber = hexBytes[0];
        } else if (message.getType() == END_OF_TRACK) {
            System.out.println("End of track.");
        } else if (message.getType() == COPYRIGHT_NOTICE) {
            System.out.println("Copyright notice: " + decimalToString(hexBytes));
        } else if (message.getType() == INSTRUMENT_NAME) {
            System.out.println("Instrument name: " + decimalToString(hexBytes));
        } else if (message.getType() == SEQUENCE_SPECIFIER) {
            System.out.println("<sequence specifier>");
        } else {
            System.out.printf("Unhandled meta message type %x\n", message.getType());
        }
    }

    private String decimalToString(int[] bytes) {
        String toReturn = "";

        for (int rawChar : bytes) {
            toReturn += (char) rawChar;
        }

        return toReturn;
    }

    private int bigEndianByteArrayToDecimal(byte[] in) {
        int numBytes = in.length;
        int toReturn = 0;

        for (int i = 0; i < numBytes; i++) {
            toReturn |= (in[i] & 0xFF) << (3 - i - 1) * 8;
        }

        return toReturn;
    }

    private int[] signedToUnsignedBytes(byte[] in) {
        int[] toReturn = new int[in.length];

        for (int i = 0; i < in.length; i++) {
            toReturn[i] = (in[i] & 0xFF);
        }

        return toReturn;
    }
}