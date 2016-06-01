/**
 * Created by kevin on 5/12/16.
 */
public class Mine implements Tile{
    private boolean isRevealed;
    private boolean flagged;
    public Mine(){
        isRevealed = false;
        flagged = false;
    }

    public boolean isRevealed(){
        return isRevealed;
    }

    public boolean click(){
        isRevealed = true;
        return false;
    }

    public int getValue(){
        return -1;
    }

    public String toString(){
        if (!isRevealed)
            return "*";
        return "M";
    }

    public boolean isFlagged(){
        return flagged;
    }

    public void flag(){
        flagged = !flagged;
    }
}
