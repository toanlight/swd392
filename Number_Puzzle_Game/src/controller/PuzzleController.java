package controller;

import java.awt.event.ActionListener;
import view.PuzzleView;
import model.IPuzzleModel;

/**
 * GameController coordinates actions from the GameView and updates the GameModel.
 * It also manages the background thread responsible for counting elapsed time.
 */
public class PuzzleController {

    private final IPuzzleModel model;
    private final PuzzleView view;

    public PuzzleController(IPuzzleModel model, PuzzleView view) {
        this.model = model;
        this.view = view;
        wireEvents();
    }

    private void wireEvents() {
        wireBoardButtons();

        view.getNewGameButton().addActionListener(e -> {
            int size = view.getSizeSelector().getSelectedIndex() == 0 ? 3 : 4;
            model.newGame(size);
            wireBoardButtons(); // grid was rebuilt -> re-attach handlers
        });
    }

    /** (Re)attaches click handlers to every tile button currently in the view. */
    private void wireBoardButtons() {
        int size = model.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                final int row = r, col = c;
                ActionListener listener = e -> model.moveTile(row, col);
                var btn = view.getTileButton(row, col);
                for (var al : btn.getActionListeners()) {
                    btn.removeActionListener(al);
                }
                btn.addActionListener(listener);
            }
        }
    }
}

