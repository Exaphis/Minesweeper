/**
 * Created by kevin on 5/12/16.
 */
public class BoardTester {
    public static void main(String[] args) {
        Board b = new Board(10, 10, 10, 5, 5);
        System.out.println(b);
        System.out.println(b.showFullBoard());
        System.out.println(b.click(0, 0));
        System.out.println(b);
    }
}
