package music.play;

import music.pitch.BasicNote;

import java.util.List;

/**
 *
 * @author reedt
 */
public class Voice {
    private final BasicNote[] melody;

    public Voice(BasicNote[] melody) {
        this.melody = melody;
    }

    public Voice(List<BasicNote> melody) {
       this.melody = melody.toArray(new BasicNote[melody.size()]);
    }
}
