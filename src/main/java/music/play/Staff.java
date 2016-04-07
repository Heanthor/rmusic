package music.play;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.play.key.Key;
import music.play.key.KeySignature;

import java.util.Arrays;
import java.util.List;

/**
 * @author reedt
 */
public class Staff {
    public final Tempo tempo;
    public final KeySignature keySignature;
    public final TimeSignature timeSignature;
    public final List<Voice> voices;

    public Staff(Tempo tempo, Key key, TimeSignature timeSignature, List<Voice> voices) {
        this.tempo = tempo;
        this.voices = voices;
        this.keySignature = key.getKeySignature();
        this.timeSignature = timeSignature;
    }

    public Staff(Tempo tempo, Key key, TimeSignature timeSignature, Voice[] voices) {
        this.tempo = tempo;
        this.voices = Arrays.asList(voices);
        this.keySignature = key.getKeySignature();
        this.timeSignature = timeSignature;
    }
}
