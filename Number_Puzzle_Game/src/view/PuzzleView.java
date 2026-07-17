package view;

import model.IPuzzleModel;
import model.PuzzleModelListener;

import javax.swing.*;
import java.awt.*;

/**
 * View ("V" in MVC) and, in Observer-pattern terms, a concrete "Observer".
 *
 * Responsibility (SRP): render the current state and forward raw UI
 * events to whoever wired up its listener (the Controller). It never
 * mutates game state itself and never talks to IPuzzleModel's mutating
 * methods (moveTile/newGame) directly - only the Controller does that.
 *
 * It depends only on the IPuzzleModel abstraction to read state (DIP).
 */
public class PuzzleView extends JFrame implements PuzzleModelListener {

    private final IPuzzleModel model;

    private JPanel boardPanel;
    private JButton[][] tileButtons;
    private JLabel moveLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;
    private JComboBox<String> sizeSelector;
    private JButton newGameButton;

    public PuzzleView(IPuzzleModel model) {
        super("Number Puzzle Game (MVC + Observer)");
        this.model = model;
        this.model.addListener(this); // <-- Observer self-registration

        buildUi();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));

        JPanel topPanel = new JPanel(new FlowLayout());
        sizeSelector = new JComboBox<>(new String[]{"3x3", "4x4"});
        newGameButton = new JButton("New Game");
        topPanel.add(new JLabel("Size:"));
        topPanel.add(sizeSelector);
        topPanel.add(newGameButton);
        add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        moveLabel = new JLabel("Moves: 0");
        timeLabel = new JLabel("Time: 0s");
        statusLabel = new JLabel(" ");
        bottomPanel.add(moveLabel);
        bottomPanel.add(timeLabel);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void rebuildGrid(int size) {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(size, size, 4, 4)); // <-- required by spec
        tileButtons = new JButton[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton b = new JButton();
                b.setFont(new Font("Arial", Font.BOLD, 22));
                b.setFocusPainted(false);
                tileButtons[r][c] = b;
                boardPanel.add(b);
            }
        }
        pack();
    }

    // ---- exposed for the Controller to attach behavior ----

    public JButton getTileButton(int row, int col) { return tileButtons[row][col]; }
    public JButton getNewGameButton() { return newGameButton; }
    public JComboBox<String> getSizeSelector() { return sizeSelector; }
    public int getBoardSize() { return model.getSize(); }

    // ==== PuzzleModelListener (Observer) callbacks - automatic UI refresh ====

    @Override
    public void onNewGame(int size) {
        statusLabel.setText(" ");
        rebuildGrid(size);
    }

    @Override
    public void onBoardChanged(int[][] board, int size) {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int v = board[r][c];
                JButton btn = tileButtons[r][c];
                btn.setText(v == 0 ? "" : String.valueOf(v));
                btn.setEnabled(v != 0);
            }
        }
    }

    @Override
    public void onMoveCountChanged(int moveCount) {
        moveLabel.setText("Moves: " + moveCount);
    }

    @Override
    public void onTimeChanged(long elapsedSeconds) {
        timeLabel.setText("Time: " + elapsedSeconds + "s");
    }

    @Override
    public void onGameWon(int moveCount, long elapsedSeconds) {
        statusLabel.setText("Congratulations! Solved in " + moveCount
                + " moves, " + elapsedSeconds + "s");
        JOptionPane.showMessageDialog(this,
                "Congratulations! You solved the puzzle!\nMoves: " + moveCount
                        + "\nTime: " + elapsedSeconds + "s",
                "You win!", JOptionPane.INFORMATION_MESSAGE);
    }
}
