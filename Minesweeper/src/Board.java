import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by kevin on 5/12/16.
 */
public class Board {
    private Tile[][] board;

    public Board(int inWidth, int inHeight){
        board = new Tile[inWidth][inHeight];
    }

    public Board(int inWidth, int inHeight, int mines, int clickRow, int clickCol) throws IllegalArgumentException{
        board = new Tile[inWidth][inHeight];
        Random r = new Random();

        ArrayList<int[]> available = new ArrayList<>();
        for (int row = 0; row < inWidth; row++) {
            for (int col = 0; col < inHeight; col++) {
                boolean contains = false;
                for (int[] neighbor : Cell.getNeighbors(board, clickRow, clickCol))
                    if ((row == neighbor[0] && col == neighbor[1]) || (row == clickRow && col == clickCol))
                        contains = true;
                if (!contains)
                    available.add(new int[]{row, col});
            }
        }
        for (int i = 1; i <= mines; i++){
            int[] mineLoc = available.remove(r.nextInt(available.size()));
            board[mineLoc[0]][mineLoc[1]] = new Mine();
        }

        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board[0].length; col++)
                if (board[row][col] == null)
                    board[row][col] = new Cell(Cell.neighboringMines(board, row, col));

        click(clickRow, clickCol);
    }

    public String toString(){
        String ans = "";
        for (Tile[] row: board){
            ans += Arrays.toString(row) + "\n";
        }

        return ans;
    }

    public String showFullBoard(){
        String ans = "";
        for (Tile[] row : board){
            ans += "[";
            for (Tile tile : row){
                if (tile instanceof Cell)
                    ans += "C" + tile.getValue() + ", ";
                else
                    ans += "M" + ", ";
            }
            ans = ans.substring(0, ans.length()-2) + "]" + "\n";
        }
        return ans;
    }

    public boolean click(int row, int col){
        if (!board[row][col].isFlagged())
            return click(row, col, new ArrayList<>());
        return true;
    }

    private boolean click(int row, int col, ArrayList<int[]> processed){
        if (board[row][col] instanceof Mine) {
            for (Tile[] boardRow : board){
                for (Tile tile : boardRow)
                    if (tile instanceof Mine)
                        tile.click();
            }
            return board[row][col].click();
        }
        if (board[row][col].getValue() > 0){
            processed.add(new int[] {row, col});
            return board[row][col].click();
        }
        else {
            for (int[] neighbor : Cell.getNeighbors(board, row, col)) {
                boolean done = false;
                for (int[] index : processed)
                    if (Arrays.equals(index, neighbor))
                        done = true;
                if (board[neighbor[0]][neighbor[1]] instanceof Cell && !done) {
                    processed.add(new int[]{row, col});
                    click(neighbor[0], neighbor[1], processed);
                }
            }
            return board[row][col].click();
        }
    }

    public Tile getTile (int row, int col){
        return board[row][col];
    }

    public void flag(int row, int col){
        board[row][col].flag();
    }

    public boolean isFlagged(int row, int col){
        return board[row][col].isFlagged();
    }
    public boolean isWin(){
        for (Tile[] row : board){
            for (Tile tile : row){
                if (tile instanceof Cell && !tile.isRevealed())
                    return false;
            }
        }
        return true;
    }
}
