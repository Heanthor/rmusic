package music.pitch;

import music.pitch.interval.Interval;
import music.pitch.interval.IntervalUtils;
import music.rhythm.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Multiple Notes sounded at once.
 * A Chord is just a collection of Note objects.
 * @author Reed
 */
public class Chord implements BasicNote {
    public final ArrayList<Note> chord = new ArrayList<Note>();
    private int index = -1;

    /**
     * Create a Chord with the given Notes
     * @param notes Notes to add to the chord
     */
    public Chord(Note... notes) {
        Collections.addAll(chord, notes);
    }

    /**
     * Create a Chord by giving a base note, and Intervals above it
     * @param base The lowest note in the chord
     * @param intervalsAbove Multiple intervals above the base note, forming the Chord
     */
    public Chord(Note base, Interval... intervalsAbove) {
        chord.add(base);

        for (Interval i: intervalsAbove) {
            chord.add(IntervalUtils.getNoteAbove(base, i));
        }
    }

    /**
     * Create a Chord by supplying a chord string of format [Notestring]-[Notestring]...
     * @param chordString The chord string to provide
     */
    public Chord(String chordString) {
        String[] noteStrings = chordString.split("-");

        for (String s: noteStrings) {
            chord.add(new Note(s));
        }
    }

    public String toString() {
        ArrayList<String> sortedList = new ArrayList<String>(chord.size());

        sortedList.addAll(chord.stream().map(Note::pitchOnlyToString).collect(Collectors.toList()));

        sortedList.sort(null); // Notes are Comparable, so no Comparator is necessary
        return String.join("-", sortedList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chord chord1 = (Chord) o;

        return chord.equals(chord1.chord);

    }

    @Override
    public int hashCode() {
        return chord.hashCode();
    }

    @Override
    public String toNoteString() {
        return toString();
    }

    @Override
    public Duration getDuration() {
        return chord.get(0).getDuration();
    }

    @Override
    public void setDuration(Duration d) {
        for (BasicNote n: chord) {
            n.setDuration(d);
        }
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equalsIndex(BasicNote b) {
        return this.index == b.getIndex() && this.equals(b);
    }
}
