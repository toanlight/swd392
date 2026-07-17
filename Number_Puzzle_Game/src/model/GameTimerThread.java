package model;

public class GameTimerThread extends Thread {

    private final IPuzzleModel model;
    private volatile boolean running = true;

    public GameTimerThread(IPuzzleModel model) {
        super("PuzzleGameTimer");
        this.model = model;
        setDaemon(true);
    }

    public void stopTimer() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            // Swing is not thread-safe: hop back onto the EDT before
            // mutating/notifying, since PuzzleView will update its
            // labels/buttons directly from the callback.
            javax.swing.SwingUtilities.invokeLater(model::tick);
        }
    }
}