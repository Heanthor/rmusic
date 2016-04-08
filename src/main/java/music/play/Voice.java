package music.play;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.rhythm.Duration;
import music.rhythm.Rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
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

        for (BasicNote b: melody) {
            sb.append(String.format("%-7s", b.toNoteString()).replace(' ', '.'));
        }

        return "Voice " + index + ": " + sb.toString();
    }

    /**
     * Add a note to this Voice.
     * @param n The note or rest to add
     * @return The current object, for chained calls
     */
    public Voice addNote(Note n) {
        melody.add(n);

        return this;
    }

    /**
     * Get the number of notes in this Voice
     * @return The number of notes.
     */
    public int size() {
        return melody.size();
    }

    /**
     * Get the index of this Voice.
     * Defaults to 0.
     * @return The index of this Voice
     */
    public int getIndex() {
        return index;
    }
}
