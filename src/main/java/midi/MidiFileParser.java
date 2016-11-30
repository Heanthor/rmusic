package midi;

import music.pitch.*;
import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import music.play.Voice;
import music.play.key.Key;
import music.play.key.KeySignature;
import music.rhythm.Duration;
import music.rhythm.Rest;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.sound.midi.*;

public class MidiFileParser {
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

    /**
     * Represents the current tempo of the parsed file. Defaults to quarter = 120bpm.
     */
    private Tempo fileTempo = new Tempo(120);
    private TimeSignature timeSignature = new TimeSignature(4, TimeSignature.DenominatorChoices._4);

    public static void main(String[] args) throws Exception {
        File furElise = new File("bin/midifiles/for_elise_by_beethoven.mid");
        File tchaikovskyVC1 = new File("bin/midifiles/tchop35a.mid");
        Staff f = new MidiFileParser().loadAndParseFile(furElise);

        System.out.println(f.toString());
    }

    public Staff loadAndParseFile(File f) throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(f);

        return parseMidiFile(sequence);
    }

    private Staff parseMidiFile(Sequence sequence) {
        if (sequence.getDivisionType() != Sequence.PPQ) {
            System.err.println("Can't parse this MIDI format into notes!");
            System.exit(-1);
        }
        // number of ticks per quarter note in ppq
        float resolution = sequence.getResolution();

        int trackNumber = 0;
        // Note, start time
        Map<Note, Long> soundingNotes = new HashMap<>();

        // Dynamically grow based on need for more voices
        ArrayList<Voice> voices = new ArrayList<>();
        // add primary voice
        voices.add(new Voice(new ArrayList<>()));

        // loops chunks
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                long tickTime = event.getTick();
                System.out.print("@" + tickTime + " ");
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

                        // This note is currently playing, push to stack
                        // Give note a temporary quarter note time, this will change
                        String noteString = noteName + octave + ":N";
                        Note temp = new Note(noteString);

                        // Push note with dummy duration to voice arrays, to lock in its position
                        // notes are added to available voices as needed, or a new voice is added
                        int voiceToPlayIndex = soundingNotes.size();

                        if (voices.size() <= voiceToPlayIndex) {
                            // Allocate a new voice
                            Voice v = new Voice(new ArrayList<>(), voices.size());
                            // Fill rests up to the current duration
                            catchUpVoices(voices.get(0), v);

                            // add note that created new voice
                            v.addNote(temp);

                            // and add the voice
                            voices.add(v);
                        } else {
                            // Give the lowest voice the note
                            Voice v = voices.get(voiceToPlayIndex);

                            // then add note
                            v.addNote(temp);
                        }

                        soundingNotes.put(temp, tickTime);

                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                        processNoteOff(resolution, soundingNotes, voices, tickTime, sm);
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

        return new Staff(fileTempo, timeSignature, voices);
    }

    private void processNoteOff(float resolution, Map<Note, Long> soundingNotes, ArrayList<Voice> voices, long tickTime, ShortMessage sm) {
        int key = sm.getData1();
        int octave = (key / 12) - 1;
        int note = key % 12;
        String noteName = NOTE_NAMES[note];
        int velocity = sm.getData2();

        // Create a note equal to stored one, to get its value
        String noteString = noteName + octave + ":N";
        Note temp = new Note(noteString);

        // Find the associated closing event for this note, and calculate its duration
        // Remove it as well, since it is no longer sounding
        long startTime = soundingNotes.remove(temp);

        long noteTicks = tickTime - startTime;
        double fractionOfQuarterNote = roundDoubleToCleanFraction(noteTicks / resolution);
        Duration approxDuration = Duration.getDurationByRatio(new Duration("Q"), fractionOfQuarterNote);

        // Have all info about note, calculate timing
        boolean done = false;
        Voice voiceAddedTo = null;

        for (Voice v : voices) {
            if (!done) {
                for (int j = v.size() - 1; j >= 0; j--) {
                    // Find (best guess) temporary note corresponding to this one
                    BasicNote temp2 = v.getNote(j);
                    if (temp2.equals(temp)) {
                        temp2.setDuration(approxDuration);
                        done = true;
                        voiceAddedTo = v;

                        break;
                    }
                }
            }
        }


        // catch all voices up to voice 0
        for (Voice v : voices) {
            // if any sounding note's duration is not known, do not catch up voices
            boolean unknown = false;

            for (BasicNote n : v.melody) {
                if (n.getDuration().equals(new Duration(Duration.DurationValue.NULL, false))) {
                    unknown = true;
                    break;
                }
            }

            if (!unknown) {
                catchUpVoices(voiceAddedTo, v);
            }
        }


        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
    }

    // Might be unused
    private double calculateTickSize(float divisionType, float resolution) {
        double tickSize;

        if (divisionType == Sequence.PPQ) {
            System.out.println("Division Type: PPQ");
            double ticksPerSecond = resolution * (fileTempo.bpm / 60);
            tickSize = 1.0 / ticksPerSecond;
        } else {
            double framesPerSecond =
                    (divisionType == Sequence.SMPTE_24 ? 24
                            : (divisionType == Sequence.SMPTE_25 ? 25
                            : (divisionType == Sequence.SMPTE_30 ? 30
                            : (divisionType == Sequence.SMPTE_30DROP ?

                            29.97 : -1))));
            double ticksPerSecond = resolution * framesPerSecond;
            tickSize = 1.0 / ticksPerSecond;
        }
        return tickSize;
    }

    private void parseMetaMessage(MetaMessage message) {
        byte[] raw = message.getData();

        int[] hexBytes = signedToUnsignedBytes(raw);

        if (message.getType() == TEMPO_MARKING) {
            // microseconds per quarter note
            // tempo = tt tt tt
            int tempo = bigEndianByteArrayToDecimal(raw);
            // convert to beats per minute
            int bpm = (int) Math.round(60000000 / (double) tempo);
            System.out.printf("Tempo marking: %d bpm\n", bpm);

            // save file tempo
            fileTempo = new Tempo(bpm);
        } else if (message.getType() == TIME_SIGNATURE) {
            int numerator = hexBytes[0];
            int denominator = (int) Math.pow(2, hexBytes[1]);
            // MIDI clock ticks per click
            int metronomeTicks = hexBytes[2];
            int thirtySecondNotesPerBeat = hexBytes[3];

            System.out.println("Time signature: " + numerator + "/" + denominator);
            // update time signature
            timeSignature = new TimeSignature(numerator, TimeSignature.DenominatorChoices.getByInt(hexBytes[1]));
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

    /**
     * Catches the second voice up to the first voice in beats, by adding rests as necessary.
     * If the second voice is ahead of the first, this method does nothing.
     *
     * @param ahead The first voice
     * @param behind The second voice
     */
    private void catchUpVoices(Voice ahead, Voice behind) {
        double aheadTime = ahead.getTotalDurationValue();
        double behindTIme = behind.getTotalDurationValue();

        if (behindTIme >= aheadTime) {
            return;
        }

        double difference = aheadTime - behindTIme;

        Duration[] rests = Duration.generateMultipleDurations(difference);

        for (Duration d : rests) {
            behind.addNote(new Rest(d));
        }
    }
//    private void catchUpVoices(Voice v0, Voice v1) {
//        double v0Time = v0.getTotalDurationValue();
//        double v1Time = v1.getTotalDurationValue();
//
//        if (v0Time == v1Time) {
//            return;
//        }
//
//        Voice behind = v1Time < v0Time ? v1 : v0;
//
//        double difference = Math.abs(v0Time - v1Time);
//
//        Duration[] rests = Duration.generateMultipleDurations(difference);
//
//        for (Duration d : rests) {
//            behind.addNote(new Rest(d));
//        }
//    }

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

    private double roundDoubleToCleanFraction(double d) {
        // Based on max sixty fourth second resolution
        double[] acceptableFractionsOfQuarterNote = {
                0.5, // eighth
                0.25, // sixteenth
                0.125, // thirty-second
                0.0625 // sixty-fourth
        };

        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < acceptableFractionsOfQuarterNote.length; i++) {
            double distance = Math.abs(d - acceptableFractionsOfQuarterNote[i]);
            if (distance < minDistance) {
                minDistance = distance;
            } else {
                // found our closest match
                return acceptableFractionsOfQuarterNote[i - 1];
            }
        }

        return 0.0625;
    }

    private int findMaxElementIndex(List<Double> d) {
        double max = -1;
        int maxIndex = -1;

        for (int i = 0; i < d.size(); i++) {
            double temp = d.get(i);
            if (temp > max) {
                max = temp;
                maxIndex = i;
            }
        }

        return maxIndex;
    }
}