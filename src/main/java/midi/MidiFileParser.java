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

    /**
     * Time signature of the MIDI file. Defaults to 4/4.
     */
    private TimeSignature timeSignature = new TimeSignature(4, TimeSignature.DenominatorChoices._4);
    /**
     * number of ticks per quarter note in ppq
     */
    private float resolution;
    /**
     * Note, time it started sounding.
     */
    private Map<Note, Long> soundingNotes;
    /**
     * Dynamically grow based on need for more voices
     */
    private ArrayList<Voice> voices;

    /**
     * Tick when the last note event was found.
     * Used to determine when to insert rests on all voices.
     */
    private long lastNoteAction = 0;

    /**
     * Track number of processed notes
     */
    private int noteNum = 0;

    /**
     * Parse a midi file into a Staff. Attempts to match event time intervals to the closest note value this program can model.
     *
     * @param f A File object representing a .midi file.
     * @return A Staff representing the data in the midi file.
     * @throws InvalidMidiDataException If the file provided is not a valid MIDI file.
     * @throws IOException              If a file reading error occurs.
     */
    public Staff loadAndParseFile(File f) throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(f);

        return parseMidiFile(sequence);
    }

    private Staff parseMidiFile(Sequence sequence) {
        if (sequence.getDivisionType() != Sequence.PPQ) {
            System.err.println("Can't parse this MIDI format into notes!");
            System.exit(-1);
        }

        resolution = sequence.getResolution();

        int trackNumber = 0;
        soundingNotes = new HashMap<>();

        voices = new ArrayList<>();
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
                        processNoteOn(tickTime, sm);
                    } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                        processNoteOff(tickTime, sm);
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

    private void processNoteOn(long eventTickTime, ShortMessage sm) {
        int key = sm.getData1();
        int octave = (key / 12) - 1;
        int note = key % 12;
        String noteName = NOTE_NAMES[note];
        int velocity = sm.getData2();

        // some midi files encode a NOTE_OFF event as NOTE_ON with velocity 0
        if (velocity == 0) {
            processNoteOff(eventTickTime, sm);
            return;
        }

        // This note is currently playing, push to stack
        // Give note a temporary quarter note time, this will change
        String noteString = noteName + octave + ":N";
        Note temp = new Note(noteString);

        if (noteNum > 0 && soundingNotes.size() == 0) {
            // if there are no notes playing, calculate a rest to fill the time between now and the last note off
            long restTime = eventTickTime - lastNoteAction;

            for (Voice v: voices) {
                v.addNote(new Rest(ticksToApproxDuration(restTime)));
            }
        }

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

        soundingNotes.put(temp, eventTickTime);

        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
    }

    private void processNoteOff(long eventTickTime, ShortMessage sm) {
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
        long startTime;
        if (soundingNotes.containsKey(temp)) {
            startTime = soundingNotes.remove(temp);
        } else {
            return;
        }

        long noteTicks = eventTickTime - startTime;
        Duration approxDuration = ticksToApproxDuration(noteTicks);

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

        lastNoteAction = eventTickTime;
        noteNum++;

        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
    }

    private Duration ticksToApproxDuration(long noteTicks) {
        double fractionOfQuarterNote = roundDoubleToCleanFraction(noteTicks / resolution);
        return Duration.getDurationByRatio(new Duration("Q"), fractionOfQuarterNote);
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
     * Calculates the length of a tick based on resolution read earlier in the file.
     *
     * @param divisionType The MIDI code representing the division type, either PPQ or SMPTE.
     * @return The tick size, as fractions of a second.
     */
    private double calculateTickSize(float divisionType) {
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

    /**
     * Catches the second voice up to the first voice in beats, by adding rests as necessary.
     * If the second voice is ahead of the first, this method does nothing.
     *
     * @param ahead  The first voice
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

    /* Helper methods for MIDI file parsing */

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

    /**
     * Rounds fractions up or down to clean intervals, as used in musical notation.
     * These durations are (with quarter note == 1):
     * <ol>
     * <li>Whole (4)</li>
     * <li>Dotted half (3)</li>
     * <li>Half (2)</li>
     * <li>Dotted quarter (1.5)</li>
     * <li>Quarter (1)</li>
     * <li>Eighth (0.5)</li>
     * <li>Dotted eighth (0.75)</li>
     * <li>Sixteenth (0.25)</li>
     * <li>Dotted sixteenth (0.1875)</li>
     * <li>Thirty-second (0.125)</li>
     * <li>Sixty-fourth (0.0625)</li>
     * </ol>
     * <p>
     * as well as dotted versions of each up to thirty-second note.
     *
     * @param d The floating point number to round
     * @return The floating point fraction in the list closest to d.
     */
    private double roundDoubleToCleanFraction(double d) {
        // Based on max sixty fourth second resolution
        double[] acceptableFractionsOfQuarterNote = {
                4, // whole
                3, // dotted half
                2, // half
                1.5, // dotted quarter
                1, // quarter
                0.75, // dotted eigth
                0.5, // eighth
                0.375, // dotted sixteenth
                0.25, // sixteenth
                0.1875, // dotted thirty-second
                0.125, // thirty-second
                0.0625, // sixty-fourth
                0 // too short to count
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

        // The smallest resolution accepted falls through
        return 0;
    }

    /**
     * Find the index of the max element in the given list.
     *
     * @param d List of doubles
     * @return The index of the maximum element in the list, or -1 if the list is empty.
     */
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

    public static void main(String[] args) throws Exception {
        File furElise = new File("bin/midifiles/for_elise_by_beethoven.mid");
        File tchaikovskyVC1 = new File("bin/midifiles/tchop35a.mid");
        File test = new File("bin/midifiles/test.txt");
        Staff f = new MidiFileParser().loadAndParseFile(furElise);

        System.out.println(f.toString());
        System.out.println(f.getAlignedStaff());
    }
}