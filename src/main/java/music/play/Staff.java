package music.play;

import music.play.key.Key;
import music.play.key.KeySignature;

import java.util.List;

/**
 * @author reedt
 */
public class Staff {
    private Tempo tempo;
    private KeySignature keySignature;
    private TimeSignature timeSignature;
    private List<Voice> voices;

    public Staff(Tempo tempo, Key key, TimeSignature timeSignature, List<Voice> voices) {
        this.tempo = tempo;
        this.voices = voices;
        this.keySignature = key.getKeySignature();
        this.timeSignature = timeSignature;
    }
}
