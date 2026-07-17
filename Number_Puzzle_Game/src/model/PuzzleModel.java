package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleModel implements IPuzzleModel {

    private int[][] board;
    private int size;
    private int emptyRow, emptyCol;
    private int moveCount;
    private long elapsedSeconds;
    private boolean solved;

    private final List<PuzzleModelListener> listeners = new ArrayList<>();

    public PuzzleModel() {
        // Set up initial board state WITHOUT notifying (no listeners yet).
        this.size = 3;
        this.board = new int[size][size];
        initBoard();
        shuffle();
    }

    /**
     * Must be called once after all listeners (e.g. the View) have been registered.
     * Fires the initial onNewGame / onBoardChanged / onMoveCountChanged notifications
     * so the UI builds its grid and shows the shuffled board.
     */
    public void initialize() {
        notifyNewGame(size);
        notifyBoardChanged();
        notifyMoveCountChanged();
        notifyTimeChanged();
    }

    /** Fills the board with 1..n²−1, 0 in solved order and resets counters. */
    private void initBoard() {
        this.moveCount = 0;
        this.elapsedSeconds = 0;
        this.solved = false;
        int value = 1;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = (value < size * size) ? value++ : 0;
            }
        }
        emptyRow = size - 1;
        emptyCol = size - 1;
    }

    @Override
    public void addListener(PuzzleModelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(PuzzleModelListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void newGame(int size) {
        this.size = size;
        this.board = new int[size][size];
        initBoard();
        shuffle();

        notifyNewGame(size);
        notifyBoardChanged();
        notifyMoveCountChanged();
        notifyTimeChanged();
    }

    /** Performs random legal slides from the solved state so the puzzle is always solvable. */
    private void shuffle() {
        Random rnd = new Random();
        int shuffleMoves = size * size * 40;
        int lastMovedRow = -1, lastMovedCol = -1;

        for (int i = 0; i < shuffleMoves; i++) {
            List<int[]> candidates = new ArrayList<>();
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                int r = emptyRow + d[0];
                int c = emptyCol + d[1];
                if (isInBounds(r, c) && !(r == lastMovedRow && c == lastMovedCol)) {
                    candidates.add(new int[]{r, c});
                }
            }
            int[] pick = candidates.get(rnd.nextInt(candidates.size()));
            lastMovedRow = emptyRow;
            lastMovedCol = emptyCol;
            swapWithEmpty(pick[0], pick[1]);
        }
    }

    @Override
    public boolean moveTile(int row, int col) {
        if (solved || !isAdjacentToEmpty(row, col)) {
            return false;
        }
        swapWithEmpty(row, col);
        moveCount++;

        notifyBoardChanged();
        notifyMoveCountChanged();

        if (checkSolved()) {
            solved = true;
            notifyGameWon();
        }
        return true;
    }

    @Override
    public void tick() {
        if (solved) return;
        elapsedSeconds++;
        notifyTimeChanged();
    }

    @Override
    public int getSize() { return size; }

    @Override
    public int getTile(int row, int col) { return board[row][col]; }

    @Override
    public int getMoveCount() { return moveCount; }

    @Override
    public long getElapsedSeconds() { return elapsedSeconds; }

    @Override
    public boolean isSolved() { return solved; }

    // ---- internal helpers ----

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    private boolean isAdjacentToEmpty(int r, int c) {
        if (!isInBounds(r, c)) return false;
        int dr = Math.abs(r - emptyRow);
        int dc = Math.abs(c - emptyCol);
        return (dr + dc) == 1;
    }

    private void swapWithEmpty(int r, int c) {
        board[emptyRow][emptyCol] = board[r][c];
        board[r][c] = 0;
        emptyRow = r;
        emptyCol = c;
    }

    private boolean checkSolved() {
        int expected = 1;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (r == size - 1 && c == size - 1) {
                    if (board[r][c] != 0) return false;
                } else {
                    if (board[r][c] != expected++) return false;
                }
            }
        }
        return true;
    }

    // ---- notification (Observer "notify" step) ----

    private void notifyBoardChanged() {
        int[][] snapshot = deepCopy(board);
        for (PuzzleModelListener l : new ArrayList<>(listeners)) {
            l.onBoardChanged(snapshot, size);
        }
    }

    private void notifyMoveCountChanged() {
        for (PuzzleModelListener l : new ArrayList<>(listeners)) {
            l.onMoveCountChanged(moveCount);
        }
    }

    private void notifyGameWon() {
        for (PuzzleModelListener l : new ArrayList<>(listeners)) {
            l.onGameWon(moveCount, elapsedSeconds);
        }
    }

    private void notifyNewGame(int size) {
        for (PuzzleModelListener l : new ArrayList<>(listeners)) {
            l.onNewGame(size);
        }
    }

    private void notifyTimeChanged() {
        for (PuzzleModelListener l : new ArrayList<>(listeners)) {
            l.onTimeChanged(elapsedSeconds);
        }
    }

    private int[][] deepCopy(int[][] src) {
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }
        return copy;
    }
}