import music.pitch.Note;
import music.pitch.interval.Interval;
import music.pitch.interval.IntervalUtils;

import java.util.Scanner;

/**
 * Test driver
 *
 * @author reedt
 */
public class Main {
    public static void main(String[] args) {
        new Main().inputLoop();
    }

    private void inputLoop() {
        System.out.print("Give me a base note. ");
        System.out.println("Form: [Base note][Optional: # or b][Octave number]:[Optional: Duration string].");
        System.out.println("Duration string choices: \"W\", \"H\", \"Q\", \"E\", \"S\", \"T\", or \"X\"");

        while (true) {
            Scanner sc = new Scanner(System.in);

            System.out.print("Note: ");

            try {
                Note noteIn = new Note(sc.nextLine());
                System.out.println(noteIn.toString());

                System.out.println("Now give me an interval: ");
                Interval intervalIn = IntervalUtils.stringToInterval(sc.nextLine());

                System.out.print("Find note above or below? (a/b): ");
                String choice = sc.nextLine();

                if (!choice.equals("a") && !choice.equals("b")) {
                    throw new IllegalArgumentException("Choice must be a or b.");
                }

                boolean above = choice.equals("a");

                Note noteOut;
                if (above) {
                    noteOut = IntervalUtils.getNoteAbove(noteIn, intervalIn);
                } else {
                    noteOut = IntervalUtils.getNoteBelow(noteIn, intervalIn);
                }

                System.out.println("Note " + (above ? "above: " : "below: ") + noteOut.pitchOnlyToString());
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Delay printing slightly
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
