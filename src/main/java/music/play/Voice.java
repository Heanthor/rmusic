package music.play;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.rhythm.Duration;
import music.rhythm.Rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author reedt
 */
public class Voice {
    public List<BasicNote> melody;
    private int index;

    public Voice(BasicNote[] melody) {
        this.melody = Arrays.asList(melody);
        this.index = 0;
    }

    public Voice(List<BasicNote> melody) {
        this.melody = melody;
        this.index = 0;
    }

    public Voice(BasicNote[] melody, int index) {
        this.melody = Arrays.asList(melody);
        this.index = index;
    }

    public Voice(List<BasicNote> melody, int index) {
        this.melody = melody;
        this.index = index;
    }

    public Voice(String voiceString) {
        this.melody = Voice.parseVoiceString(voiceString).melody;
    }

    /**
     * Create a Voice out of multiple Note strings of the form [notestring],[notestring],[notestring]...
     *
     * @param voiceString Voice string to parse
     * @return The created object
     */
    public static Voice parseVoiceString(String voiceString) {
        String[] notes = voiceString.split(",");
        BasicNote[] parsed = new Note[notes.length];

        for (int i = 0; i < notes.length; i++) {
            if (notes[i].charAt(0) == 'R') {
                parsed[i] = Rest.parseRestString(notes[i]);
            } else {
                parsed[i] = new Note(notes[i]);
            }
        }

        return new Voice(parsed);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (BasicNote b : melody) {
            sb.append(Note.compactFormatBasicNote(b));
        }

        return trimEnd("Voice " + index + ": " + sb.toString(), '.');
    }

    /**
     * Utility used to remove characters from the end of a string.
     *
     * @param toTrim String to trim
     * @param ch     Character to remove
     * @return The string with all instances of ch removed from the tail.
     */
    private static String trimEnd(String toTrim, char ch) {
        int goodLength = toTrim.length();

        for (int i = toTrim.length() - 1; i > 0; i--) {
            char c = toTrim.charAt(i);

            if (c == ch) {
                // knock off a character
                goodLength--;
            } else {
                // trimmed as much as possible
                break;
            }
        }

        return toTrim.substring(0, goodLength);
    }

    /**
     * Sum all note values in this Voice, producing an array of largest-value Durations.
     *
     * @return An array of Durations, in the largest possible units.
     */
    public Duration[] getTotalDuration() {
        return Duration.generateMultipleDurations(getTotalDurationValue());
    }

    /**
     * Sum all note values in this Voice.
     *
     * @return Their total duration, represented in beats.
     */
    public double getTotalDurationValue() {
        double sum = 0;

        for (BasicNote b : melody) {
            sum += b.getDuration().getDoubleValue();
        }

        return sum;
    }

    /**
     * Add a note to this Voice.
     *
     * @param n The note or rest to add
     * @return The current object, for chained calls
     */
    public Voice addNote(Note n) {
        melody.add(n);

        return this;
    }

    /**
     * Replaces the element at the given index with the given BasicNote.
     *
     * @param n     The note to replace with.
     * @param index The index to replace.
     * @return The current object
     */
    public Voice addNote(Note n, int index) {
        melody.set(index, n);

        return this;
    }

    /**
     * Add a collection of notes to this Voice.
     *
     * @param notes The notes or rests to add
     * @return The current object, for chained calls
     */
    public Voice addNotes(Note... notes) {
        Collections.addAll(melody, notes);

        return this;
    }

    /**
     * Get the number of notes in this Voice
     *
     * @return The number of notes.
     */
    public int size() {
        return melody.size();
    }

    /**
     * Get the index of this Voice.
     * Defaults to 0.
     *
     * @return The index of this Voice
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the Note that is at a given index in this voice.
     *
     * @param index The index to retrieve from
     * @return The present note.
     */
    public BasicNote getNote(int index) {
        return melody.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Voice voice = (Voice) o;

        if (index != voice.index) return false;
        return melody.equals(voice.melody);

    }

    @Override
    public int hashCode() {
        int result = melody.hashCode();
        result = 31 * result + index;
        return result;
    }
}
