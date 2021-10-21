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
    // See https://www.csie.ntu.edu.tw/~r92092/ref/midi/ for information on tracks

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
     * List of indices to be skipped on the current track.
     * Reset during parsing (once per track)
     */
    private ArrayList<Integer> skipList;

    /**
     * Container which holds start and end absolute tick time for a Note.
     */
    private static class MidiNote {
        long tickStart;
        long tickEnd;
        Note note;

        public MidiNote(long tickStart, long tickEnd, Note note) {
            this.tickStart = tickStart;
            this.tickEnd = tickEnd;
            this.note = note;
        }
    }

    /**
     * Map of note start tick time to actual note
     */
    private Map<Long, List<MidiNote>> noteMap;
    /**
     * List of all times when a note starts.
     * These will be in chronological order and should be traversed back to front.
     */
    private List<Long> allNoteTimes;

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

        Staff staff = parseMidiFile(sequence);
        Staff s2 = parseIntermediate();

        return s2;
    }

    private Staff parseIntermediate() {
        // each voice has a tick time which represents the end of the last note
        var voiceTimes = new ArrayList<Long>();
        voiceTimes.add((long) 0);

        for (long eventTime : allNoteTimes) {
            var notesAtTime = noteMap.get(eventTime);

            for (int i = 0; i < notesAtTime.size(); i++) {
                MidiNote n = notesAtTime.get(i);
                if (voices.size() <= i) {
                    // we have multiple notes starting on the same time, and voices are not allocated
                    // add a new one
                    addVoice(voiceTimes, i, n);

                    voices.get(i).addNote(n.note);
                    voiceTimes.set(i, n.tickEnd);
                    continue;
                }

                boolean placed = false;
                for (int j = 0; j < voices.size() && !placed; j++) {
                    if (voiceTimes.get(j) <= n.tickStart) {
                        // this voice is active but has no note playing. catch up the voice and add the note
                        Voice v = voices.get(j);
                        // check if we need to insert a rest
                        long restDur = eventTime - voiceTimes.get(j);

                        if (restDur > 0) {
                            v.addNote(new Rest(ticksToApproxDuration(restDur)));
                        }

                        v.addNote(n.note);
                        voiceTimes.set(j, n.tickEnd);
                        placed = true;
                    }
                }

                // all voices had active notes on them, so we need to add and catch up another
                if (!placed) {
                    int vIndex = voices.size();
                    addVoice(voiceTimes, vIndex, n);

                    voices.get(vIndex).addNote(n.note);
                    voiceTimes.set(vIndex, n.tickEnd);
                }
            }
        }

        return new Staff(fileTempo, timeSignature, voices);
    }

    private void addVoice(List<Long> voiceTimes, int index, MidiNote note) {
        Voice v = new Voice(new ArrayList<>(), index);
        voices.add(v);

        if (note.tickStart > 0) {
            long ts = note.tickStart;
            while (ts > 0) {
                Duration d = ticksToApproxDuration(ts);
                v.addNote(new Rest(d));
                ts -= durationToTicks(d);
            }
        }

        voiceTimes.add(note.tickStart);
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
        noteMap = new HashMap<>();
        allNoteTimes = new ArrayList<>();

        // add primary voice
        voices.add(new Voice(new ArrayList<>()));

        // loops chunks
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();

            skipList = new ArrayList<>(track.size());

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                long tickTime = event.getTick();
                System.out.print("@" + tickTime + " ");
                MidiMessage message = event.getMessage();

                // Channel voice, channel mode, system common, system realtime
                if (message instanceof ShortMessage sm) {
                    System.out.print("Channel: " + sm.getChannel() + " ");

                    if (sm.getCommand() == ShortMessage.NOTE_ON) {
                        processNoteOn2(tickTime, sm);
                    } else if (sm.getCommand() == ShortMessage.NOTE_OFF && !skipList.contains(i)) {
                        processNoteOff2(tickTime, sm);
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

    private void processNoteOn2(long eventTickTime, ShortMessage sm) {
        int key = sm.getData1();
        int octave = (key / 12) - 1;
        int note = key % 12;
        String noteName = NOTE_NAMES[note];
        int velocity = sm.getData2();

        // some midi files encode a NOTE_OFF event as NOTE_ON with velocity 0
        if (velocity == 0) {
            processNoteOff2(eventTickTime, sm);
            return;
        }

        String noteString = noteName + octave + ":N";
        Note tmpNote = new Note(noteString);

        if (noteMap.containsKey(eventTickTime)) {
            // TODO
//            // add highest notes to the top of the list if possible
//            var nm = noteMap.get(eventTickTime);
//            for (int i = 0; i < nm.size(); i++) {
//                if (nm.get(i).note.compareTo(tmpNote) <= 0) {
//                    nm.add(i, new MidiNote(eventTickTime, 0, tmpNote));
//                }
//            }
            noteMap.get(eventTickTime).add(new MidiNote(eventTickTime, 0, tmpNote));
        } else {
            ArrayList<MidiNote> noteArrayList = new ArrayList<>();
            noteArrayList.add(new MidiNote(eventTickTime, 0, tmpNote));
            noteMap.put(eventTickTime, noteArrayList);
        }

        if (!allNoteTimes.contains(eventTickTime)) {
            allNoteTimes.add(eventTickTime);
        }

        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
    }

    private void processNoteOff2(long eventTickTime, ShortMessage sm) {
        int key = sm.getData1();
        int octave = (key / 12) - 1;
        int note = key % 12;
        String noteName = NOTE_NAMES[note];

        // Create a note equal to stored one, to get its value
        String noteString = noteName + octave + ":N";
        Note temp = new Note(noteString);

        // find the starting event for this note and calculate its duration
        // move backwards from the current time and "pop" times off to look for the starting event

        ListIterator<Long> iter = allNoteTimes.listIterator(allNoteTimes.size());
        while (iter.hasPrevious()) {
            long ett = iter.previous();

            var notes = noteMap.get(ett);
            for (MidiNote n : notes) {
                if (n.note.equals(temp)) {
                    long noteTicks = eventTickTime - ett;
                    Duration approxDuration = ticksToApproxDuration(noteTicks);

                    n.note.setDuration(approxDuration);
                    n.tickEnd = eventTickTime;
                }
            }
        }

        System.out.println("Note off, " + noteName + octave + " key=" + key);

    }

    @Deprecated
    private void processNoteOn(long eventTickTime, ShortMessage sm, Track currentTrack, int eventIndex) {
        int key = sm.getData1();
        int octave = (key / 12) - 1;
        int note = key % 12;
        String noteName = NOTE_NAMES[note];
        int velocity = sm.getData2();

        // some midi files encode a NOTE_OFF event as NOTE_ON with velocity 0
        if (velocity == 0) {
            processNoteOff(eventTickTime, sm, currentTrack, eventIndex);
            return;
        }

        // before processing this note, seek forward and check for any NOTE_OFF at the same tickTime
        // do this so that out of order events on the same ms tick do not mess up voicing
        int lookAheadIndex = eventIndex + 1;
        long lookAheadTickTime = -1;

        // get the tick time of the next event in sequence first
        MidiEvent lookAheadEvent = null;
        if (lookAheadIndex < currentTrack.size()) {
            lookAheadEvent = currentTrack.get(lookAheadIndex);
            lookAheadTickTime = lookAheadEvent.getTick();
        } // else, skip this process since there are no more events to check

        while (lookAheadTickTime == eventTickTime && lookAheadEvent != null) {
            MidiMessage message = lookAheadEvent.getMessage();

            if (message instanceof ShortMessage tmp) {
                if (tmp.getCommand() == ShortMessage.NOTE_OFF) {
                    processNoteOff(lookAheadTickTime, tmp, currentTrack, lookAheadIndex);
                    // skip list
                    skipList.add(lookAheadIndex);
                }
            }

            lookAheadIndex++;

            if (lookAheadIndex >= currentTrack.size()) {
                // avoid out of bounds
                break;
            }

            lookAheadEvent = currentTrack.get(lookAheadIndex);
            lookAheadTickTime = lookAheadEvent.getTick();
        }

        // This note is currently playing, push to stack
        // Give note a temporary quarter note time, this will change
        String noteString = noteName + octave + ":N";
        Note temp = new Note(noteString);

        if (noteNum > 0 && soundingNotes.size() == 0) {
            // if there are no notes playing, calculate a rest to fill the time between now and the last note off
            long restTime = eventTickTime - lastNoteAction;

            // restTime can be 0 (or fuzzed threshold) if on/off are on same tick
            if (restTime > 0) {
                for (Voice v : voices) {
                    v.addNote(new Rest(ticksToApproxDuration(restTime)));
                }
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

    @Deprecated
    private void processNoteOff(long eventTickTime, ShortMessage sm, Track currentTrack, int currentIndex) {
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

    private long durationToTicks(Duration d) {
        return (long)(d.getDoubleValue() * resolution * 4);
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
        StringBuilder toReturn = new StringBuilder();

        for (int rawChar : bytes) {
            toReturn.append((char) rawChar);
        }

        return toReturn.toString();
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