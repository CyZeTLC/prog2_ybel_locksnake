package de.hsbi.lockgame.ui;

import de.hsbi.lockgame.logic.GameEngine;
import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.logic.Observer;
import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.settings.GameConstants;
import de.hsbi.lockgame.settings.InputConstants;
import de.hsbi.lockgame.ui.render.GameRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

public class GamePanel extends JPanel implements Observer<GameState> {
    private final List<Observer<Direction>> observers;

    private GameState state;
    private final GameRenderer renderer;
    private GameEngine gameEngine;

    public GamePanel(GameState initialState, GameRenderer renderer) {
        this.observers = new LinkedList<>();
        this.state = initialState;
        this.renderer = renderer;

        var width = initialState.level().width() * GameConstants.TILE_SIZE;
        var height = initialState.level().height() * GameConstants.TILE_SIZE;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        setFocusable(true);
        InputConstants.BINDINGS.forEach(this::setupKeyBindings);
    }

    public void update(GameState newState) {
        this.state = newState;
        repaint();
    }

    /*
    Hier bin ich ehrlich, ich würde das hier normalerweise nicht so wissen, aber ich fand, dass die Aufgabenstellung es so will.
    Normalerweise würde ich einfach nur `gameEngine.update(direction);` machen weil es ja einfach nur ein "Observer" ist.
    */
    public void notifyObserver(Direction direction) {
        this.observers.forEach(observer -> observer.update(direction));
    }

    public void setGameEngine(GameEngine engine) {
        this.gameEngine = engine;
    }

    private void setupKeyBindings(Direction direction, Iterable<Integer> keyCodes) {
        // Swing separates two layers: multiple keystrokes can be mapped to a single Action
        // 1. KeyStroke → Name
        var inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        // 2. Name → Action
        var actionMap = getActionMap();

        // shared name per direction
        var actionKey = "move_" + direction.name();

        // shared Swing Action per direction
        var swingAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyObserver(direction);
            }
        };

        // 1. register KeyStroke → Name
        keyCodes.forEach(keyCode -> inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), actionKey));
        // 2. register Name → Action
        actionMap.put(actionKey, swingAction);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, state, GameConstants.TILE_SIZE);
    }

    public List<Observer<Direction>> getObservers() {
        return observers;
    }
}
