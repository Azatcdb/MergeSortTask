import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        MergeSort mergerSort = new MergeSort();
        try {
            mergerSort.setArgs(args);
            mergerSort.sort();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
