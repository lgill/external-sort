package tapeSort;

/**
 * Represents a machine with limited memory that can sort tape drives.
 */
public class TapeSorter {

	private int memorySize;
	private int tapeSize;
	private int[] memory;

	public TapeSorter(int memorySize, int tapeSize) {
		this.memorySize = memorySize;
		this.tapeSize = tapeSize;
		this.memory = new int[memorySize];
	}

	/**
	 * Sorts the first `size` items in memory via quicksort
	 */
	public void quicksort(int size) {
		this.quicksort(0, size - 1);
	}
	
	public void quicksort(int l, int u) {
		int lower = l;
		int upper = u;
		int pivot = memory[l + (u - l)/2];
		while (lower <= upper) {
			while (memory[lower] < pivot) {
				lower ++;
			}
			while (memory[upper] > pivot) {
				upper--;
			}
			if (lower <= upper) {
				swap(lower, upper);
				lower++;
				upper--;
			}
		}
		if (l < upper)
			quicksort(l, upper);
		if (lower < u)
			quicksort(lower, u);
	}
	
	private void swap(int l, int u) {
		int temp = memory[l];
		memory[l] = memory[u];
		memory[u] = temp;
	}

	/**
	 * Reads in numbers from drive `in` into memory (a chunk), sorts it, then
	 * writes it out to a different drive. It writes chunks alternatively to
	 * drives `out1` and `out2`.
	 *
	 * If there are not enough numbers left on drive `in` to fill memory, then
	 * it should read numbers until the end of the drive is reached.
	 *
	 * Example 1: Tape size = 8, memory size = 2
	 * ------------------------------------------ BEFORE: in: 4 7 8 6 1 3 5 7
	 *
	 * AFTER: out1: 4 7 1 3 _ _ _ _ out2: 6 8 5 7 _ _ _ _
	 *
	 *
	 * Example 2: Tape size = 10, memory size = 3
	 * ------------------------------------------ BEFORE: in: 6 3 8 9 3 1 0 7 3
	 * 5
	 *
	 * AFTER: out1: 3 6 8 0 3 7 _ _ _ _ out2: 1 3 9 5 _ _ _ _ _ _
	 *
	 *
	 * Example 3: Tape size = 13, memory size = 4
	 * ------------------------------------------ BEFORE: in: 6 3 8 9 3 1 0 7 3
	 * 5 9 2 4
	 *
	 * AFTER: out1: 3 6 8 9 2 3 5 9 _ _ _ _ _ out2: 0 1 3 7 4 _ _ _ _ _ _ _ _
	 */
	public void initialPass (TapeDrive in, TapeDrive out1, TapeDrive out2) {
		int count = 0;
		for (int i = 0; i < tapeSize - (tapeSize % memorySize);) {
			for (int j = 0; j < memorySize; j++, i++) {
			memory[j] = in.read();
			}
			quicksort(memorySize);
		for (int k = 0; k < memorySize; k++) {
			if ((count % 2) == 0) {
					out1.write(memory[k]);
			}
			else {
				out2.write(memory[k]);
			}
			}
			count++;
		}
		memory = new int[memorySize];
		for (int i = tapeSize - (tapeSize % memorySize); i < tapeSize;) {
			for (int j = 0; j < (tapeSize % memorySize); j++, i++) {
				memory[j] = in.read();
			}
		}
			quicksort(tapeSize % memorySize);
			for (int k = 0; k < (tapeSize % memorySize); k++) {
				if ((count % 2) == 0) {
					out1.write(memory[k]);
				}
				else {
					out2.write(memory[k]);
				}
			}
		in.reset();
		out1.reset();
		out2.reset();
	}

	/**
	 * Merges the first chunk on drives `in1` and `in2` and writes the sorted,
	 * merged data to drive `out`. The size of the chunk on drive `in1` is
	 * `size1`. The size of the chunk on drive `in2` is `size2`.
	 *
	 * Example =============
	 *
	 * (BEFORE) in1: [ ... 1 3 6 8 9 ... ] ^ in2: [ ... 2 4 5 7 8 ... ] ^ out: [
	 * ... _ _ _ _ _ ... ] ^ size1: 4, size2: 4
	 *
	 * (AFTER) in1: [ ... 1 3 6 8 9 ... ] ^ in2: [ ... 2 4 5 7 8 ... ] ^ out: [
	 * ... 1 2 3 4 5 6 7 8 _ _ _ ... ] ^
	 */
	public void mergeChunks(TapeDrive in1, TapeDrive in2, TapeDrive out,
			int size1, int size2) {
		int t1 = 0;
		int t2 = 0;
		
		if (size1 != 0) {
			t1 = in1.read();
		}
	    if (size2 != 0) {
	    	t2 = in2.read();
	    }
	        
	    int i = 0;
	    int j = 0;
	        
	    while(i < size1 && j < size2 ){
	    	if (t1 <= t2) {
	    		out.write(t1);
	    		i++;
	    		if (i < size1) {
	    			t1 = in1.read();
	    		}
	    	}	
	    	if (t2 < t1) {
	    		out.write(t2);
    			j++;
	    		if (j < size2) {
	    			t2 = in2.read();
	    		}
	    	}
	    }
	    if(i < size1) {
	    	for(; i < size1;) {
	    		out.write(t1);
    			i++;
	    		if (i < size1) {
	    			t1 = in1.read();
	    		}
	    	}
	    }
	    if(j < size2) {
	    	for(; j < size2;) {
	    		out.write(t2);
	    		j++;
	    		if (j < size2) {
	    			t2 = in2.read();
	    		}
	    	}
	    }
	}

	/**
	 * Merges chunks from drives `in1` and `in2` and writes the resulting merged
	 * chunks alternatively to drives `out1` and `out2`.
	 *
	 * The `runNumber` argument denotes which run this is, where 0 is the first
	 * run.
	 *
	 * -- Math Help -- The chunk size on each drive prior to merging will be:
	 * memorySize * (2 ^ runNumber) The number of full chunks on each drive is:
	 * floor(tapeSize / (chunk size * 2)) Note: If the number of full chunks is
	 * 0, that means that there is a full chunk on drive `in1` and a partial
	 * chunk on drive `in2`. The number of leftovers is: tapeSize - 2 * chunk
	 * size * number of full chunks
	 *
	 * To help you better understand what should be happening, here are some
	 * examples of corner cases (chunks are denoted within curly braces {}):
	 *
	 * -- Even number of chunks -- in1 -> { 1 3 5 6 } { 5 7 8 9 } in2 -> { 2 3 4
	 * 7 } { 3 5 6 9 } out1 -> { 1 2 3 3 4 5 6 7 } out2 -> { 3 5 5 6 7 8 9 9 }
	 *
	 * -- Odd number of chunks -- in1 -> { 1 3 5 } { 6 7 9 } { 3 4 8 } in2 -> {
	 * 2 4 6 } { 2 7 8 } { 0 3 9 } out1 -> { 1 2 3 4 5 6 } { 0 3 3 4 8 9 } out2
	 * -> { 2 6 7 7 8 9 }
	 *
	 * -- Number of leftovers <= the chunk size -- in1 -> { 1 3 5 6 } { 5 7 8 9
	 * } in2 -> { 2 3 4 7 } out1 -> { 1 2 3 3 4 5 6 7 } out2 -> { 5 7 8 9 }
	 *
	 * -- Number of leftovers > the chunk size -- in1 -> { 1 3 5 6 } { 5 7 8 9 }
	 * in2 -> { 2 3 4 7 } { 3 5 } out1 -> { 1 2 3 3 4 5 6 7 } out2 -> { 3 5 5 7
	 * 8 9 }
	 *
	 * -- Number of chunks is 0 -- in1 -> { 2 4 5 8 9 } in2 -> { 1 5 7 } out1 ->
	 * { 1 2 4 5 5 7 8 9 } out2 ->
	 */
	public void doRun(TapeDrive in1, TapeDrive in2, TapeDrive out1,
			TapeDrive out2, int runNumber) {
		int chunkSize = (int) (memorySize * Math.pow(2, runNumber));
		int fullChunks = (int) Math.floor(tapeSize / (chunkSize * 2));
		int leftovers = tapeSize - 2 * chunkSize * fullChunks;
		
		int count = 0;
		
		if (fullChunks > 0) {
			for (int i = 0; i < fullChunks; i++) {
				if ((count % 2) == 0) {
				mergeChunks(in1, in2, out1, chunkSize,chunkSize);
				}
				else {
					mergeChunks(in1, in2, out2, chunkSize,chunkSize);
				}
				count++;
			}
		}
		if (leftovers > chunkSize) {
			if ((count % 2) == 0) {
				mergeChunks(in1, in2, out1, chunkSize,leftovers - chunkSize);
			}
			else {
				mergeChunks(in1, in2, out2, chunkSize,leftovers - chunkSize);
			}
		}
		if (leftovers < chunkSize) {
			if ((count % 2) == 0) {
				mergeChunks(in1, in2, out1, leftovers,0);
			}
			else {
				mergeChunks(in1, in2, out2, leftovers, 0);
			}
		}
		in1.reset();
		in2.reset();
		out1.reset();
		out2.reset();
	}

	/**
	 * Sorts the data on drive `t1` using the external sort algorithm. The
	 * sorted data should end up on drive `t1`.
	 *
	 * Initially, drive `t1` is filled to capacity with unsorted numbers. Drives
	 * `t2`, `t3`, and `t4` are empty and are to be used in the sorting process.
	 */
	public void sort(TapeDrive t1, TapeDrive t2, TapeDrive t3, TapeDrive t4) {
		initialPass(t1, t3, t4);
		int max = (int) Math.ceil(Math.log((double) tapeSize/(double) memorySize)/Math.log(2));
		
		for (int i = 0; i < max; i++) {
			if (i % 2 == 0) {
			doRun(t3, t4, t1, t2, i);
			} else {
				doRun(t1, t2, t3, t4, i);
			}
		}
		if (max % 2 == 0) {
			for (int i = 0; i < tapeSize; i++) {
				t1.write(t3.read());
			}
		}
	}

	public static void main(String[] args) {
		// Example of how to test
		int ts = 80;
		TapeSorter tapeSorter = new TapeSorter(10, ts);
		TapeDrive t1 = TapeDrive.generateRandomTape(ts);
		TapeDrive t2 = new TapeDrive(ts);
		TapeDrive t3 = new TapeDrive(ts);
		TapeDrive t4 = new TapeDrive(ts);

		tapeSorter.sort(t1, t2, t3, t4);
		int last = Integer.MIN_VALUE;
		boolean sorted = true;
		for (int i = 0; i < ts; i++) {
			int val = t1.read();
			sorted &= last <= val; // <=> sorted = sorted && (last <= val);
			last = val;
		}
		if (sorted)
			System.out.println("Sorted!");
		else
			System.out.println("Not sorted!");
	}

}
