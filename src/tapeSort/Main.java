package tapeSort;

public class Main {
	
	public static void main(String[] args) {
		
		TapeSorter s1 = new TapeSorter(3, 13);
		TapeDrive t1 = new TapeDrive(13);
		TapeDrive t2 = new TapeDrive(13);
		TapeDrive t3 = new TapeDrive(13);
		TapeDrive t4 = new TapeDrive(13);
		
		int[] arr = {81, 94, 11, 96, 12, 35, 17, 99, 28, 58, 41, 75, 15};
		
		for (int i = 0; i < 13; i++) {
			t1.write(arr[i]);
		}
		
		System.out.println("original tape: ");
		
		for (int i = 0; i < 13; i++) {
			System.out.println(t1.read());
		}

		s1.sort(t1, t2, t3, t4);
		
		System.out.println("tape out:");
		
		for (int i = 0; i < 13; i++) {
			System.out.println(t1.read());
		}
	}
}
