package de.hsbi.lockgame.ui;

import de.hsbi.lockgame.logic.GameEngine;
import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.settings.GameConstants;
import de.hsbi.lockgame.settings.InputConstants;
import de.hsbi.lockgame.ui.render.GameRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class GamePanel extends JPanel {
  private GameState state;
  private final GameRenderer renderer;
  private GameEngine gameEngine;

  public GamePanel(GameState initialState, GameRenderer renderer) {
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
    var swingAction =
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            gameEngine.update(direction);
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
}
