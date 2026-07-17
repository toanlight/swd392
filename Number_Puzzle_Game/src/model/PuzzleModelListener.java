package model;

public interface PuzzleModelListener {

    /** Fired whenever any tile position on the board changes. */
    void onBoardChanged(int[][] board, int size);

    /** Fired whenever the move counter changes. */
    void onMoveCountChanged(int moveCount);

    /** Fired every time the elapsed-time clock ticks (once per second). */
    void onTimeChanged(long elapsedSeconds);

    /** Fired exactly once, the moment the puzzle becomes solved. */
    void onGameWon(int moveCount, long elapsedSeconds);

    /** Fired when a brand-new game/board has been generated. */
    void onNewGame(int size);
}