import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.Timer;

/**
 * Created by kevin on 5/13/16.
 */
public class MinesweeperPanel extends JPanel {
    private int rows, cols, minesFlagged, totalMines;
    private final int DEFAULT_ROWS = 9;
    private final int DEFAULT_COLS = 9;
    private final int DEFAULT_MINES = 10;
    private JPanel masterPanel, Minefield, settingsPanel, outputPanel;
    private JButton[][] MinefieldArray;
    private JFormattedTextField[] textField;
    private JLabel[] labels;
    private JLabel minesLeft, timerLabel, message;
    private JFrame frame;
    private JButton newGameButton;
    private JButton showHighscoresButton;
    private Timer timer;
    private Board board;
    private boolean started;

    public MinesweeperPanel(JFrame inFrame) {
        started = false;
        board = new Board(DEFAULT_ROWS, DEFAULT_COLS);
        minesFlagged = 0;
        totalMines = DEFAULT_MINES;
        frame = inFrame;
        rows = DEFAULT_ROWS;
        cols = DEFAULT_COLS;
        Minefield = new JPanel(new GridLayout(rows, cols));
        MinefieldArray = new JButton[rows][cols];

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));

        masterPanel = new JPanel(new BorderLayout());
        textField = new JFormattedTextField[3];
        labels = new JLabel[3];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JButton mine = new JButton();
                mine.setOpaque(true);
                Minefield.add(mine);
                MinefieldArray[row][col] = mine;
                MinefieldArray[row][col].addMouseListener(new MouseHandler(row, col));
                MinefieldArray[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }

        JLabel rowLabel = new JLabel("Rows:");
        JLabel colLabel = new JLabel("Cols:");
        JLabel mineLabel = new JLabel("Mines:");
        NumberFormat numberFormatter = NumberFormat.getInstance();
        numberFormatter.setGroupingUsed(false);
        JFormattedTextField rowInput = new JFormattedTextField(numberFormatter);
        rowInput.setValue(DEFAULT_ROWS);
        JFormattedTextField colInput = new JFormattedTextField(numberFormatter);
        colInput.setValue(DEFAULT_COLS);
        JFormattedTextField mineInput = new JFormattedTextField(numberFormatter);
        mineInput.setValue(DEFAULT_MINES);
        labels[0] = (JLabel) settingsPanel.add(rowLabel);
        textField[0] = (JFormattedTextField) settingsPanel.add(rowInput);
        labels[1] = (JLabel) settingsPanel.add(colLabel);
        textField[1] = (JFormattedTextField) settingsPanel.add(colInput);
        labels[2] = (JLabel) settingsPanel.add(mineLabel);
        textField[2] = (JFormattedTextField) settingsPanel.add(mineInput);

        newGameButton = new JButton(" Reset ");
        newGameButton.addMouseListener(new MouseHandler(newGameButton, "reset"));
        newGameButton.setBorder(BorderFactory.createEtchedBorder());
        settingsPanel.add(newGameButton);

        showHighscoresButton = new JButton(" Highscores ");
        showHighscoresButton.addMouseListener(new MouseHandler(showHighscoresButton, "highscores"));
        showHighscoresButton.setBorder(BorderFactory.createEtchedBorder());
        settingsPanel.add(showHighscoresButton);

        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
        minesLeft = new JLabel(" ");
        message = new JLabel(" ");
        timerLabel = new JLabel();
        timer = new Timer();

        outputPanel.add(timerLabel);
        outputPanel.add(Box.createHorizontalGlue());
        outputPanel.add(message);
        outputPanel.add(Box.createHorizontalGlue());
        outputPanel.add(minesLeft);

        masterPanel.add(settingsPanel, BorderLayout.NORTH);
        masterPanel.add(Minefield, BorderLayout.CENTER);
        masterPanel.add(outputPanel, BorderLayout.SOUTH);

        outputPanel.setBorder(BorderFactory.createEtchedBorder());

        updateMinefield();
    }
    private class MouseHandler extends MouseAdapter {
        private int row, col;
        private JButton button;
        private String command;

        public MouseHandler(int inRow, int inCol) {
            row = inRow;
            col = inCol;
        }

        public MouseHandler(JButton inButton, String inCommand){
            button = inButton;
            command = inCommand;
        }

        public void mouseClicked(MouseEvent e) {
            if (button == null) {
                if (!started) {
                    resetMinefield();
                    started = true;
                    message.setText("");
                    rows = Integer.parseInt(textField[0].getText());
                    cols = Integer.parseInt(textField[1].getText());
                    totalMines = Integer.parseInt(textField[2].getText());
                    try {
                        board = new Board(rows, cols, totalMines, row, col);
                        timer.cancel();
                        timer = new Timer();
                        long startTime = System.nanoTime();
                        timerLabel.setText("0.00");
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                long endTime = System.nanoTime();
                                long elapsedNano = endTime - startTime;
                                timerLabel.setText(new DecimalFormat("#.##").format(elapsedNano/1000000000.0));
                            }
                        }, 1, 10);

                        minesLeft.setText("" + totalMines);
                        updateMinefield();

                        if (board.isWin())
                            win();
                    }catch (IllegalArgumentException exception){
                        message.setText("Not enough space to create the mines.");
                    }
                }
                else if (SwingUtilities.isRightMouseButton(e) || (e.getModifiers() & ActionEvent.CTRL_MASK) ==ActionEvent.CTRL_MASK) {
                    if (board.isFlagged(row, col))
                        minesFlagged--;
                    else
                        minesFlagged++;
                    board.flag(row, col);
                    minesLeft.setText("" + (totalMines - minesFlagged));
                    updateMinefield();
                }
                else{
                    boolean living = board.click(row, col);
                    updateMinefield();
                    if (board.isWin())
                        win();
                    else if (!living){
                        started = false;
                        timer.cancel();
                        message.setText("You lost. Click for a new game.");
                    }
                }
            }
            else {
                if (command.equals("reset")) {
                    resetMinefield();
                }
                if (command.equals("highscores")){
                    displayHighscores(-1, -1, -1);
                }
            }
            frame.revalidate();
            frame.repaint();
        }
    }

    public JPanel getPanel(){
        return masterPanel;
    }

    public void updatePanel(){
        Minefield.revalidate();
        settingsPanel.revalidate();
        outputPanel.revalidate();
        masterPanel.revalidate();
    }

    public void updateMinefield(){
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < MinefieldArray[0].length; col++){
                if (board.getTile(row, col) == null){
                    MinefieldArray[row][col].setText("");
                    MinefieldArray[row][col].setBackground(new Color(238, 238, 238));
                }
                else if (board.isFlagged(row, col) && !board.getTile(row, col).isRevealed()){
                    MinefieldArray[row][col].setText("F");
                    MinefieldArray[row][col].setBackground(Color.RED);
                }
                else if (board.getTile(row, col) instanceof Mine && board.getTile(row, col).isRevealed()){
                    MinefieldArray[row][col].setText("M");
                    MinefieldArray[row][col].setBackground(Color.BLACK);
                }
                else if (board.getTile(row, col).isRevealed() && board.getTile(row, col).getValue() == 0){
                    MinefieldArray[row][col].setBackground(Color.GREEN);
                }
                else if (board.getTile(row, col).isRevealed() && board.getTile(row, col).getValue() == 1){
                    MinefieldArray[row][col].setText("1");
                    MinefieldArray[row][col].setBackground(new Color(233, 255, 0));
                }
                else if (board.getTile(row, col).isRevealed() && board.getTile(row, col).getValue() == 2){
                    MinefieldArray[row][col].setText("2");
                    MinefieldArray[row][col].setBackground(new Color(255, 225, 33));
                }
                else if (board.getTile(row, col).isRevealed() && board.getTile(row, col).getValue() == 3){
                    MinefieldArray[row][col].setText("3");
                    MinefieldArray[row][col].setBackground(new Color(226, 187, 12));
                }
                else if (board.getTile(row, col).isRevealed() && board.getTile(row, col).getValue() > 3){
                    MinefieldArray[row][col].setText("" + board.getTile(row, col).getValue());
                    MinefieldArray[row][col].setBackground(new Color(154, 103, 0));
                }
                else{
                    MinefieldArray[row][col].setText("");
                    MinefieldArray[row][col].setBackground(new Color(238, 238, 238));
                }
            }
        }
        updatePanel();
    }

    public void resetMinefield(){
        started = false;
        minesFlagged = 0;
        timer.cancel();
        rows = Integer.parseInt(textField[0].getText());
        cols = Integer.parseInt(textField[1].getText());
        board = new Board(rows, cols);
        Minefield = new JPanel(new GridLayout(rows, cols));
        MinefieldArray = new JButton[rows][cols];

        for (int mRow = 0; mRow < rows; mRow++) {
            for (int mCol = 0; mCol < cols; mCol++) {
                JButton mine = new JButton();
                mine.setOpaque(true);
                Minefield.add(mine);
                MinefieldArray[mRow][mCol] = mine;
                MinefieldArray[mRow][mCol].addMouseListener(new MouseHandler(mRow, mCol));
                MinefieldArray[mRow][mCol].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
        masterPanel.removeAll();
        masterPanel.add(settingsPanel, BorderLayout.NORTH);
        masterPanel.add(Minefield, BorderLayout.CENTER);
        masterPanel.add(outputPanel, BorderLayout.SOUTH);
        updateMinefield();
    }

    private void win(){
        started = false;
        Highscore.writeHighscore(new Dimension(rows, cols), totalMines, timerLabel.getText());
        timer.cancel();
        message.setText("You won! Click for a new game.");
        displayHighscores(rows, cols, totalMines);
    }

    private void displayHighscores(int row, int col, int mines){
        String scores = "Highscores:\n";
        if (row == -1 && col == -1)
            for (Map.Entry<BoardValues, ArrayList<String>> entry : Highscore.readFullHighscore().entrySet()){
                Dimension entryDim = entry.getKey().getDim();
                scores += (int)entryDim.getWidth() + "x" + (int)entryDim.getHeight() + " Mines: " + entry.getKey().getMines() + "\n";
                for (String time : entry.getValue())
                    scores += time + "\n";
            }
        else {
            scores += row + "x" + col + " Mines: " + mines + "\n";
            for (String time : Highscore.readHighscore(new BoardValues(new Dimension(row, col), mines)))
                scores += time + "\n";
            scores = "Congratulations! You won.\n" + scores;
        }
        scores = scores.trim();
        if (scores.replace("Highscores:", "").isEmpty())
            scores = "No highscores yet. Go play a game!";
        JOptionPane.showMessageDialog(frame, scores);
    }
}

