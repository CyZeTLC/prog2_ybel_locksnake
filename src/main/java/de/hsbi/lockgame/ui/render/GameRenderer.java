package de.hsbi.lockgame.ui.render;

import de.hsbi.lockgame.logic.GameState;
import java.awt.*;

public interface GameRenderer {
    void render(Graphics2D g2d, GameState state, int tileSize);
}
