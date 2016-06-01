import java.awt.*;

/**
 * Created by kevin on 5/20/16.
 */
public class BoardValues {
    private Dimension dim;
    private int mines;

    public BoardValues(Dimension inDim, int inMines){
        dim = inDim;
        mines = inMines;
    }

    public Dimension getDim() {
        return dim;
    }

    public int getMines() {
        return mines;
    }

    public String toString(){
        return dim + " Mines: " + mines;
    }

    public boolean equals(Object o){
        if (!(o instanceof BoardValues))
            return false;
        BoardValues b = (BoardValues) o;
        return (b.getDim().equals(dim) && b.getMines() == mines);
    }
}
