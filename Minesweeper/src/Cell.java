import java.util.Arrays;

/**
 * Created by kevin on 5/12/16.
 */
public class Cell implements Tile{
    private boolean isRevealed;
    private int value;
    private boolean flagged;

    public Cell(int inValue){
        isRevealed = false;
        flagged = false;
        value = inValue;
    }

    public boolean isRevealed(){
        return isRevealed;
    }

    public boolean click(){
        isRevealed = true;
        return true;
    }

    public int getValue(){
        return value;
    }

    public String toString(){
        if (!isRevealed)
            return "*";
        return "C" + value;
    }

    public static int neighboringMines(Tile[][] board, int cellRow, int cellCol){
        int[][] neighbors = getNeighbors(board, cellRow, cellCol);
        int mines = 0;
        for (int[] neighbor : neighbors)
            if (board[neighbor[0]][neighbor[1]] instanceof Mine)
                mines++;
        return mines;
    }

    public static int[][] getNeighbors(Tile[][] board, int row, int col){
        int[][] neighbors = new int[8][];
        int index = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (!(r == row && c == col)) {
                    try {
                        board[r][c] = board[r][c];
                        neighbors[index] = new int[]{r, c};
                        index++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }

                }
            }
        }

        return Arrays.copyOf(neighbors, index);
    }

    public boolean isFlagged(){
        return flagged;
    }

    public void flag(){
        flagged = !flagged;
    }
}
