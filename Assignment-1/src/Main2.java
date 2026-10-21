import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Main2 {
    //QUESTION-2
    // Number of integers we want to sort: 10 million.
    private static final int ARRAY_SIZE = 10_000_000;
    // If a segment has 100 or fewer elements, we use Insertion Sort.
    private static final int INSERTION_SORT_THRESHOLD = 100;

    public static void main(String[] args) {
        // Create an array to store 10 million integers.
        int[] values = new int[ARRAY_SIZE];
        // Generate random numbers. Using seed 42 gives us the same data
        // whenever we run the program again.
        Random random = new Random(42);
        // Fill the array with random integers.
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
        // Create a ForkJoinPool to handle the sorting tasks in parallel.
        ForkJoinPool pool = new ForkJoinPool();
        // Start the timer just before sorting begins.
        long start = System.nanoTime();
        // Start the hybrid merge sort.
        // The temporary array helps us merge the sorted segments.
        pool.invoke(new HybridMergeSortTask(
                values,
                new int[values.length],
                0,
                values.length,
                INSERTION_SORT_THRESHOLD
        ));
        // Stop the timer after sorting is complete.
        long elapsedNanos = System.nanoTime() - start;
        // Shut down the pool because we no longer need it.
        pool.shutdown();
        // Check whether all the numbers are in ascending order.
        boolean sorted = isSorted(values);
        // Display the results.
        System.out.println("Array size: " + ARRAY_SIZE);
        System.out.println("Insertion sort threshold: "
                + INSERTION_SORT_THRESHOLD);
        // Show how many processors are available to Java.
        System.out.println("Available processors: "
                + Runtime.getRuntime().availableProcessors());
        System.out.println("Sorted correctly: " + sorted);
        // Convert the time from nanoseconds to milliseconds.
        System.out.printf("Execution time: %.3f ms%n",
                elapsedNanos / 1_000_000.0);
        // Print just the first 10 numbers to keep the output readable.
        System.out.println("First 10 sorted values: "
                + Arrays.toString(Arrays.copyOfRange(values, 0, 10)));
    }

    // Checks the array to make sure every number is in the correct order.
    private static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            // If a number is greater than the one after it,
            // the array is not sorted correctly.
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        // If we reach here, the array is sorted.
        return true;
    }

    // This class handles the sorting work using the ForkJoin framework.
    private static final class HybridMergeSortTask extends RecursiveAction {
        private final int[] values;    // The array we are sorting.
        private final int[] temp;      // Temporary space used during merging.
        private final int low;         // First index of the current segment.
        private final int high;        // Index just after the current segment.
        private final int threshold;   // Segment size limit for Insertion Sort.

        // Pass the array and segment details to each sorting task.
        private HybridMergeSortTask(
                int[] values, int[] temp,
                int low, int high, int threshold) {
            this.values = values;
            this.temp = temp;
            this.low = low;
            this.high = high;
            this.threshold = threshold;
        }

        // Each task decides whether to sort a small segment directly
        // or divide a larger segment into two smaller tasks.
        @Override
        protected void compute() {
            int length = high - low;
            // Small segments are sorted using Insertion Sort.
            // This avoids creating unnecessary tasks for tiny segments.
            if (length <= threshold) {
                insertionSort(values, low, high);
                return;
            }
            // Split the current segment into two halves.
            int mid = low + length / 2;
            // Create one task for each half.
            HybridMergeSortTask left =
                    new HybridMergeSortTask(
                            values, temp, low, mid, threshold);
            HybridMergeSortTask right =
                    new HybridMergeSortTask(
                            values, temp, mid, high, threshold);
            // Let the ForkJoin framework process both halves.
            // It can run them on different worker threads.
            invokeAll(left, right);
            // Once both halves are sorted, combine them.
            merge(values, temp, low, mid, high);
        }

        // Sorts a small section of the array using Insertion Sort.
        private static void insertionSort(int[] array, int low, int high) {
            // Start from the second element and insert each value
            // into its correct position in the already-sorted section.
            for (int i = low + 1; i < high; i++) {
                int key = array[i];
                int j = i - 1;
                // Move larger values one position to the right
                // until we find the correct place for key.
                while (j >= low && array[j] > key) {
                    array[j + 1] = array[j];
                    j--;
                }
                // Place the value where it belongs.
                array[j + 1] = key;
            }
        }

        // Combines two already-sorted halves into one sorted segment.
        private static void merge(
                int[] array, int[] temp,
                int low, int mid, int high) {
            int i = low;  // Current position in the left half.
            int j = mid;  // Current position in the right half.
            int k = low;  // Position to fill in the temporary array.
            // Compare the values from both halves and copy
            // the smaller one into the temporary array.
            while (i < mid && j < high) {
                if (array[i] <= array[j]) {
                    temp[k++] = array[i++];
                } else {
                    temp[k++] = array[j++];
                }
            }
            // Copy any values left over in the left half.
            while (i < mid) {
                temp[k++] = array[i++];
            }
            // Copy any values left over in the right half.
            while (j < high) {
                temp[k++] = array[j++];
            }
            // Copy the merged result back into the original array.
            System.arraycopy(temp, low, array, low, high - low);
        }
    }}
