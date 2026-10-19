//Student Name- Taksh Sharma
//Student Number- 3161113
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
//QUESTION-1
public class Main {// Defines the number of rows and columns in the matrix.
    private static final int SIZE = 1000;
    // Defines the word that the program must search for.
    private static final String TARGET = "fun";
    // The main method is the starting point of the Java program.
    // throws Exception allows exceptions from Future.get() and other
    // operations to be passed to the Java runtime.
    public static void main(String[] args) throws Exception {
        // Creates a two-dimensional character array of size 1000 x 1000.
        char[][] matrix = new char[SIZE][SIZE];
        // Creates a random number generator.
        // Seed 42 ensures that the same sequence of random values is generated
        // each time the program runs.
        Random random = new Random(42);
        // Outer loop visits every row of the matrix.
        for (int r = 0; r < SIZE; r++) {
            // Inner loop visits every column in the current row.
            for (int c = 0; c < SIZE; c++) {
                // Generates a random integer from 0 to 25.
                // Adding it to 'a' produces a lowercase character from 'a' to 'z'.
                matrix[r][c] = (char) ('a' + random.nextInt(26));
            }
        }
        // Creates a thread pool containing three worker threads.
        // Each thread will search in a different direction.
        ExecutorService pool = Executors.newFixedThreadPool(3);
        // Records the starting time in nanoseconds.
        // Timing begins immediately before the search tasks are submitted.
        long start = System.nanoTime();
        // Submits the horizontal search task to the thread pool.
        // Future<Integer> will hold the result returned by the task.
        Future<Integer> horizontal = pool.submit(
                new SearchTask(matrix, TARGET, SearchTask.HORIZONTAL)
        );
        // Submits the vertical search task to the thread pool.
        Future<Integer> vertical = pool.submit(
                new SearchTask(matrix, TARGET, SearchTask.VERTICAL)
        );
        // Submits the diagonal search task to the thread pool.
        Future<Integer> diagonal = pool.submit(
                new SearchTask(matrix, TARGET, SearchTask.DIAGONAL)
        );
        // Retrieves the result from each task and adds the three counts.
        // get() waits for a task to finish if its result is not ready.
        int count = horizontal.get() + vertical.get() + diagonal.get();
        // Tells the thread pool that no more tasks will be submitted.
        pool.shutdown();
        // Waits up to one minute for the thread pool to terminate.
        // The submitted search tasks have already completed because get()
        // was called for all three Future objects.
        pool.awaitTermination(1, TimeUnit.MINUTES);
        // Calculates the total elapsed time in nanoseconds.
        long elapsedNanos = System.nanoTime() - start;
        // Prints the dimensions of the matrix.
        System.out.println("Matrix size: " + SIZE + "x" + SIZE);
        // Prints the word being searched for.
        System.out.println("Target: " + TARGET);
        // Prints the number of search directions and threads.
        System.out.println(
                "Search threads: 3 (horizontal, vertical, diagonal down-right)"
        );
        // Prints the total number of times the word was found.
        System.out.println("Count = " + count);
        // Converts the elapsed time from nanoseconds to milliseconds
        // and prints the result to three decimal places.
        System.out.printf(
                "Execution time: %.3f ms%n",
                elapsedNanos / 1_000_000.0
        );
    }
}