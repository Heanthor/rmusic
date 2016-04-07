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
     * Create a Tempo object.
     * The bpm represents beats per minute, with a quarter note representing the beat.
     *
     * @param bpm
     */
    public Tempo(int bpm) {
        this.note = new Duration(Duration.DurationValue.QUARTER, false);
        this.bpm = bpm;
    }

    /**
     * Create a Tempo object.
     * The bpm represents beats per minute, with a quarter note representing the beat.
     *
     * @param tempo
     */
    public Tempo(CommonTempos tempo) {
        this.note = new Duration(Duration.DurationValue.QUARTER, false);
        this.bpm = tempo.bpm;
    }

    /**
     * Creates a Tempo object.
     * This represents the modern music notation "[note] = [beats per minute]."
     *
     * @param note
     * @param bpm
     */
    public Tempo(Duration note, int bpm) {
        this.note = note;
        this.bpm = bpm;
    }

    /**
     * Creates a tempo object.
     * This represents the modern music notation "[note] = [beats per minute]."
     *
     * @param note
     * @param tempo
     */
    public Tempo(Duration note, CommonTempos tempo) {
        this.note = note;
        this.bpm = tempo.bpm;
    }

    /**
     * Represents common tempo markings, and their corresponding BPM tempo.
     */
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

        @Override
        public String toString() {
            return name() + ": " + bpm + " bpm";
        }
    }
}
