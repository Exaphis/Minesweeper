/**
 * Created by kevin on 5/12/16.
 */
public interface Tile {
    boolean isRevealed();

    boolean click();

    int getValue();

    String toString();

    boolean isFlagged();

    void flag();
}
