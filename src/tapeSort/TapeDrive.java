package tapeSort;

import java.util.Random;

public class TapeDrive {
	
	private int[] tape;
    private int currentPos = 0;

    public TapeDrive(int capacity) {
        tape = new int[capacity];
    }


    public void write(int i) {
        tape[currentPos] = i;
        currentPos = (currentPos + 1) % tape.length;
    }

    public int read() {
        int i = tape[currentPos];
        currentPos = (currentPos + 1) % tape.length;
        return i;
    }

    public void reset() {
        currentPos = 0;
    }

    /**
     * A helper function for debugging purpose. 
     * Inspect and print the tape data in the range of [0, tape.length)
     */
    public void printTape() {
        System.out.print("Tape data: ");
        for (int i=0; i<tape.length; ++i) {
            System.out.print(tape[i] + " ");
        }
        System.out.println();
    }

    /**
     * Create a new TapeDrive that can hold `capacity` numbers, fill it with random numbers, and return it.
     * The numbers must be random in the full integer range.
     */
    public static TapeDrive generateRandomTape(int capacity) {
        TapeDrive drive = new TapeDrive(capacity);
        Random rdm = new Random();
        for (int i = 0; i < capacity; i++) {
            drive.tape[i] = rdm.nextInt(100);
        }
        return drive;
    }
}
