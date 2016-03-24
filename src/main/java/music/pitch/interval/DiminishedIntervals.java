package music.pitch.interval;

/**
 * @author reedt
 */
public enum DiminishedIntervals implements Interval {
    _2nd(0),
    _3rd(2),
    _4th(4),
    _5th(6),
    _6th(7),
    _7th(9),
    _OCTAVE(11);

    private int numHalfSteps;
    DiminishedIntervals(int numHalfSteps) {
        this.numHalfSteps = numHalfSteps;
    }

    public int getNumHalfSteps() {
        return numHalfSteps;
    }
}
