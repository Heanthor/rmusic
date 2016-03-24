package music.pitch.interval;

/**
 * @author reedt
 */
public enum MinorIntervals implements Interval {
    _2nd(1),
    _3rd(3),
    _6th(8),
    _7th(10);

    private int numHalfSteps;
    MinorIntervals(int numHalfSteps) {
        this.numHalfSteps = numHalfSteps;
    }

    public int getNumHalfSteps() {
        return numHalfSteps;
    }
}
