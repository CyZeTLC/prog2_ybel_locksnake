package de.hsbi.lockgame.ui.render;

import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.settings.TextureConstants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// Draft.
public class TextureRenderer implements GameRenderer {
  private final BufferedImage WALL_TEX;
  private final BufferedImage FLOOR_TEX;
  private final BufferedImage PIN_LOW_TEX;
  private final BufferedImage PIN_HIGH_TEX;
  private final BufferedImage SNAKE_HEAD_TEX;
  private final BufferedImage SNAKE_BODY_TEX;

  public TextureRenderer() throws IOException {
    WALL_TEX = loadImage(TextureConstants.WALL_TEXTURE);
    FLOOR_TEX = loadImage(TextureConstants.FLOOR_TEXTURE);
    PIN_LOW_TEX = loadImage(TextureConstants.PIN_LOW_TEXTURE);
    PIN_HIGH_TEX = loadImage(TextureConstants.PIN_HIGH_TEXTURE);
    SNAKE_HEAD_TEX = loadImage(TextureConstants.SNAKE_HEAD_TEXTURE);
    SNAKE_BODY_TEX = loadImage(TextureConstants.SNAKE_BODY_TEXTURE);
  }

  private BufferedImage loadImage(String resourcePath) throws IOException {
    try (var in = getClass().getResourceAsStream(resourcePath)) {
      if (in == null) throw new IOException("texture file not found: " + resourcePath);
      return ImageIO.read(in);
    }
  }

  @Override
  public void render(Graphics2D g2d, GameState state, int tileSize) {
    var level = state.level();

    // Background / Tiles
    for (int y = 0; y < level.height(); y++) {
      for (int x = 0; x < level.width(); x++) {
        var px = x * tileSize;
        var py = y * tileSize;

        var cell = level.cells()[x][y];
        var tex =
            switch (cell) {
              case WALL -> WALL_TEX;
              default -> FLOOR_TEX;
            };
        g2d.drawImage(tex, px, py, tileSize, tileSize, null);
      }
    }

    // Pins
    state
        .pins()
        .forEach(
            pin -> {
              var pos = pin.position();
              var px = pos.x() * tileSize;
              var py = pos.y() * tileSize;
              var img = pin.state().isSet() ? PIN_HIGH_TEX : PIN_LOW_TEX;
              g2d.drawImage(img, px, py, tileSize, tileSize, null);
            });

    // Snake
    var body = state.snake().body();
    var isHead = true;
    for (var pos : body) {
      var px = pos.x() * tileSize;
      var py = pos.y() * tileSize;

      var img = isHead ? SNAKE_HEAD_TEX : SNAKE_BODY_TEX;
      g2d.drawImage(img, px, py, tileSize, tileSize, null);
      isHead = false;
    }
  }
}
