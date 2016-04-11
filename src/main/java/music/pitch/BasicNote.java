package music.pitch;

import music.rhythm.Duration;

/**
 * Interface representing something that can exist on a staff, either a note or rest.
 */
public interface BasicNote {
    /**
     * Get the note or rest string representation of this BasicNote
     * @return The note or rest string representation.
     */
    String toNoteString();

    /**
     * @return The duration of this note.
     */
    Duration getDuration();

    /**
     * Set the duration of this BasicNote.
     * @param d Duration to set this note to.
     */
    void setDuration(Duration d);

    /**
     * Get the optional midi index given to this BasicNote.
     * @return The index, or -1 if not specified.
     */
    int getIndex();
    /**
     * Computes equals() between two BasicNotes, including the optional midi index given to the constructor.
     * @param o Object to compare
     * @return True if equal, false if not
     */
    boolean equalsIndex(BasicNote b);
}
