package model;

public interface IPuzzleModel {

    /** Starts a brand new shuffled game of size x size (3 or 4). */
    void newGame(int size);

    /**
     * Fires initial notifications to all registered listeners.
     * Must be called once after all listeners have been added.
     */
    void initialize();

    /**
     * Attempts to slide the tile at (row, col) into the empty slot.
     * No-op (and returns false) if the move is illegal.
     */
    boolean moveTile(int row, int col);

    int getSize();

    /** 0 represents the empty slot. */
    int getTile(int row, int col);

    int getMoveCount();

    long getElapsedSeconds();

    boolean isSolved();

    /** Called once per second by the timer thread. */
    void tick();

    void addListener(PuzzleModelListener listener);

    void removeListener(PuzzleModelListener listener);
}