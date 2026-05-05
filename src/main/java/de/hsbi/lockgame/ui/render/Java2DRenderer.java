package de.hsbi.lockgame.ui.render;

import de.hsbi.lockgame.logic.GameState;
import java.awt.*;

public class Java2DRenderer implements GameRenderer {
  private static final Color EMPTY_COLOR = Color.DARK_GRAY;
  private static final Color WALL_COLOR = new Color(50, 50, 60);
  private static final Color PIN_BACKGROUND_COLOR = new Color(70, 70, 80);
  private static final Color PIN_FRAME_COLOR = new Color(120, 120, 150);
  private static final Color PIN_UP_COLOR = Color.YELLOW;
  private static final Color PIN_DOWN_COLOR = new Color(180, 180, 0);
  private static final Color SNAKE_HEAD_COLOR = Color.GREEN;
  private static final Color SNAKE_TONGUE_COLOR = Color.RED;
  private static final Color SNAKE_BODY_COLOR = new Color(0, 120, 0);
  private static final Color OVERLAY_FRAME_COLOR = new Color(0, 0, 0, 150);
  private static final Color OVERLAY_TEXT_COLOR = Color.WHITE;

  @Override
  public void render(Graphics2D g2d, GameState state, int tileSize) {
    var level = state.level();

    // Anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Game board
    for (int y = 0; y < level.height(); y++) {
      for (int x = 0; x < level.width(); x++) {
        var cell = level.cells()[x][y];

        var px = x * tileSize;
        var py = y * tileSize;

        switch (cell) {
          case EMPTY -> {
            g2d.setColor(EMPTY_COLOR);
            g2d.fillRect(px, py, tileSize, tileSize);
          }
          case WALL -> {
            g2d.setColor(WALL_COLOR);
            g2d.fillRect(px, py, tileSize, tileSize);
          }
          case PIN_SLOT -> {
            g2d.setColor(PIN_BACKGROUND_COLOR);
            g2d.fillRect(px, py, tileSize, tileSize);
            g2d.setColor(PIN_FRAME_COLOR);
            g2d.drawRect(px + 4, py + 4, tileSize - 8, tileSize - 8);
          }
        }
      }
    }

    // Pins
    state
        .pins()
        .forEach(
            pin -> {
              var pos = pin.position();
              var sign = pin.state().isSet() ? 1 : -1;

              g2d.setColor(pin.state().isSet() ? PIN_UP_COLOR : PIN_DOWN_COLOR);

              var dx = 0;
              var dy = 0;
              switch (pin.activationDirection()) {
                case UP -> dy = -1;
                case DOWN -> dy = 1;
                case LEFT -> dx = -1;
                case RIGHT -> dx = 1;
                case NONE -> {}
              }

              var w = tileSize / 4;
              var h = tileSize / 4;

              var baseX = pos.x() * tileSize + (tileSize - w) / 2;
              var baseY = pos.y() * tileSize + (tileSize - h) / 2;

              var distX = (tileSize - w) / 2 - 2;
              var distY = (tileSize - h) / 2 - 2;

              var rx = baseX + sign * dx * distX;
              var ry = baseY + sign * dy * distY;

              g2d.fillRect(rx, ry, w, h);
            });

    // Snake
    var body = state.snake().body();
    var isHead = true;
    for (var pos : body) {
      var px = pos.x() * tileSize;
      var py = pos.y() * tileSize;

      if (isHead) {
        g2d.setColor(SNAKE_HEAD_COLOR);
        g2d.fillOval(px + 2, py + 2, tileSize - 4, tileSize - 4);

        g2d.setColor(SNAKE_TONGUE_COLOR);
        var cx = px + tileSize / 2; // Mittelpunkt des Kopfes
        var cy = py + tileSize / 2;
        var tongueLen = tileSize / 3; // Länge der Nase
        switch (state.pendingDirection()) {
          case UP -> g2d.drawLine(cx, cy, cx, cy - tongueLen);
          case DOWN -> g2d.drawLine(cx, cy, cx, cy + tongueLen);
          case LEFT -> g2d.drawLine(cx, cy, cx - tongueLen, cy);
          case RIGHT -> g2d.drawLine(cx, cy, cx + tongueLen, cy);
          case NONE -> {}
        }

        isHead = false;
      } else {
        g2d.setColor(SNAKE_BODY_COLOR);
        g2d.fillOval(px + 6, py + 6, tileSize - 10, tileSize - 10);
      }
    }

    // Status Overlay
    if (!state.status().isRunning()) {
      g2d.setColor(OVERLAY_FRAME_COLOR);
      g2d.fillRect(0, 0, level.width() * tileSize, level.height() * tileSize);

      g2d.setColor(OVERLAY_TEXT_COLOR);
      g2d.setFont(g2d.getFont().deriveFont(24f));

      var text =
          switch (state.status()) {
            case WON -> "Alle Pins gesetzt!";
            case LOST_SELF_COLLISION -> "Schlange hat sich gebissen.";
            case LOST_OUT_OF_BOUNDS -> "Außerhalb des Schlosses.";
            default -> "";
          };

      var fm = g2d.getFontMetrics();
      var textWidth = fm.stringWidth(text);
      var x = (level.width() * tileSize - textWidth) / 2;
      var y = (level.height() * tileSize) / 2;

      g2d.drawString(text, x, y);
    }
  }
}
