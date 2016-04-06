package music.play;

import music.rhythm.Duration;

/**
 * Mutable class representing tempo. As the tempo can change during a piece of music,
 * this class can change to represent this.
 *
 * @author reedt
 */
public class Tempo {
    public Duration note;
    public int bpm;

    /**
     * Creates a Tempo object.
     * This represents the modern music notation "[note] = [beats per minute]"
     * @param note
     * @param bpm
     */
    public Tempo(Duration note, int bpm) {
        this.note = note;
        this.bpm = bpm;
    }

    public enum CommonTempos {
        LARGO(40),
        LARGHETTO(60),
        ADAGIO(66),
        ANDANTE(76),
        MODERATO(108),
        ALLEGRO(120),
        PRESTO(168),
        PRESTISSIMO(200);

        public final int bpm;

        CommonTempos(int bpm) {
            this.bpm = bpm;
        }
    }
}
