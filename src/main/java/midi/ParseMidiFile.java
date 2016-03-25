package midi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.*;

public class ParseMidiFile {
    // Sami Koivu on StackOverflow for skeleton
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final int TRACK_NAME = 0x03;
    public static final int TEMPO_MARKING = 0x51;
    public static final int TIME_SIGNATURE = 0x58;
    public static final int KEY_SIGNATURE = 0x59;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {
        new ParseMidiFile().init();
    }

    private void init() throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(new File("for_elise_by_beethoven.mid"));

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

                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();

                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else if (sm.getCommand() == NOTE_OFF) {
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
                        MetaMessage m = (MetaMessage) message;
                        byte[] raw = m.getData();

                        int[] hexBytes = signedToUnsignedBytes(raw);

                        if (m.getType() == TEMPO_MARKING) {
//                            int tempo = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);

                            // microseconds per quarter note
                            // tempo = tt tt tt
                            int tempo = bigEndianByteArrayToDecimal(raw);
                            // convert to beats per minute
                            int bpm = 60000000 / tempo;
                            System.out.printf("Tempo marking: %d bpm\n", bpm);
                        } else if (m.getType() == TIME_SIGNATURE) {
                            int numerator = hexBytes[0];
                            int denominator = (int)Math.pow(2, hexBytes[1]);
                            // MIDI clock ticks per click
                            int metronomeTicks = hexBytes[2];
                            int thirtySecondNotesPerBeat = hexBytes[3];

                            System.out.println("Time signature: " + numerator + "/" + denominator);
                        } else if (m.getType() == KEY_SIGNATURE) {
                            System.out.println();
                        } else if (m.getType() == TRACK_NAME) {
                            String name = "";

                            for (int rawChar: hexBytes) {
                                name += (char)rawChar;
                            }

                            System.out.println("Track name: " + name);
                        } else {
                            System.out.printf("Unhandled meta message type %x\n", m.getType());
                        }

                    } else {
                        System.out.println("Other message: " + message.getClass());

                    }
                }
            }

            System.out.println();
        }
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