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


}
