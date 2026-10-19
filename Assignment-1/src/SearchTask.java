//Student Name- Taksh Sharma
//Student Number- 3161113
import java.util.concurrent.Callable;//Callable allows this task to run in a thread and return a result.
//Here, the result is an Integer representing the number of times "fun" is found.
public class SearchTask implements Callable<Integer>{//SearchTask implements Callable<Integer> so its call() method returns an Integer.
    //Following are the constants representing the 3 directions of searching.
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int DIAGONAL = 2;

    private final char[][] matrix;//This is the 2-D character array to search.
    private final String target;//The target is the word we want to find.
    private final int direction;// Indicates the direction of the search.
    public SearchTask(char[][] matrix, String target, int direction){
        this.matrix = matrix;//Stores the matrix.
        this.target = target;//Stores the target.
        this.direction = direction;//Stores the search direction.
    }
    // call() is executed when the task is submitted to an ExecutorService. It returns the total number of matches found in its assigned direction.
    @Override
    public Integer call(){
        //Following attributes are responsible for getting the number of rows and columns in the matrix.
        int rows = matrix.length;
        int cols = matrix[0].length;

        int count = 0;// This is a local counter for matches found by this task.
        int dr = 0, dc = 0;// dr = row movement and dc = column movement. These values determine how we move throught the matrix.
        if (direction == HORIZONTAL) {//HORIZONTAL: move right, keeping the row unchanged.
            dc = 1;
        }
        else if (direction == VERTICAL) {//VERTICAL: move down, keeping the column unchanged.
            dr = 1;
        }
        else {//DIAGONAL: move down and right at the same time.
            dr = 1;
            dc = 1;
        }
        // Visit every cell as a possible starting position for the word.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Calculate the row and column of the word's final character.
                // target.length() - 1 is the number of steps from the first
                // character to the last character.
                int endR = r + (target.length() - 1) * dr;
                int endC = c + (target.length() - 1) * dc;
                // If the final character would be outside the matrix,
                // this starting position cannot contain the complete word.
                if (endR >= rows || endC >= cols) {
                    continue; // Skip to the next starting position.
                }
                // Assume the word matches until a different character is found.
                boolean match = true;
                // Compare each character of the target word with the matrix.
                for (int k = 0; k < target.length(); k++) {
                    // Calculate the position of the current character.
                    // Compare it with the corresponding target character.
                    if (matrix[r + k * dr][c + k * dc] != target.charAt(k)) {
                        match = false; // A character does not match.
                        break;         // Stop checking this starting position.
                    }
                }
                // If every character matched, increase the match counter.
                if (match) {
                    count++;
                }
            }
        }
        // Return this task's total to the main program.
        return count;
    }


}
