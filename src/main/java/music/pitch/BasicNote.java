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
    public String toNoteString();

    /**
     * @return The duration of this note.
     */
    public Duration getDuration();
}
