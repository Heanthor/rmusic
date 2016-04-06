package music.play;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.rhythm.Rest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author reedt
 */
public class Voice {
    public final BasicNote[] melody;

    public Voice(BasicNote[] melody) {
        this.melody = melody;
    }

    public Voice(List<BasicNote> melody) {
       this.melody = melody.toArray(new BasicNote[melody.size()]);
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
        ArrayList<String> notes = new ArrayList<>();

        for (BasicNote b: melody) {
            notes.add(b.toNoteString());
        }

        return "Voice: " + String.join(", ", notes);
    }
}
