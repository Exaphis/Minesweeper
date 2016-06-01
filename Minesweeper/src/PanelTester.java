import javax.swing.*;
import java.awt.*;

/**
 * Created by kevin on 5/13/16.
 */
public class PanelTester{
    public static void main(String[] args){
        JFrame frame = new JFrame("Minesweeper");
        MinesweeperPanel m = new MinesweeperPanel(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(m.getPanel());
        frame.setSize(700, 700);

        frame.revalidate();
    }
}
