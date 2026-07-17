
import controller.PuzzleController;
import model.IPuzzleModel;
import model.PuzzleModel;
import view.PuzzleView;
import model.GameTimerThread;

import javax.swing.*;

/**
 * Entry point for the Number Puzzle Game.
 * Instantiates the MVC components, establishes Observer connections, and starts the game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IPuzzleModel model = new PuzzleModel();      // Model
            PuzzleView view = new PuzzleView(model);      // View  (registers as Observer)
            model.initialize();                           // fires initial onNewGame → builds grid
            new PuzzleController(model, view);            // Controller (wires buttons)

            GameTimerThread timer = new GameTimerThread(model);
            timer.start();                                // dedicated thread for elapsed time

            view.setVisible(true);
        });
    }
}