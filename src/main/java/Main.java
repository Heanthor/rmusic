import music.pitch.Note;
import music.pitch.Octave;
import music.pitch.NoteValue;
import music.pitch.interval.Interval;
import music.pitch.interval.IntervalUtils;
import music.rhythm.Duration;

import java.util.Scanner;

import static music.pitch.NoteValue.*;

/**
 * Test driver
 * @author reedt
 */
public class Main {
    public static void main(String[] args) {
        new Main().inputLoop();
    }

    private void inputLoop() {
        System.out.print("Give me a base note.");
        System.out.println("Form: [Base note (uppercase)][Optional: # or b][Octave number]:[Duration string].");
        System.out.println("Duration string choices: \"W\", \"H\", \"Q\", \"E\", \"S\", \"T\", or \"X\"");

        while (true) {
            Scanner sc = new Scanner(System.in);

            System.out.print("Note: ");

            try {
                Note noteIn = new Note(sc.nextLine());
                System.out.println(noteIn.toString());

                System.out.println("Now give me an interval: ");
                Interval intervalIn = IntervalUtils.stringToInterval(sc.nextLine());
                System.out.print(intervalIn);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
