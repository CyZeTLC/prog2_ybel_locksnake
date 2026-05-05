package de.hsbi.lockgame.io;

import de.hsbi.lockgame.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LevelLoader {

  public static Level loadLevelFromPath(Path path) throws IOException {
    var lines = Files.readAllLines(path).stream().filter(l -> !l.isBlank()).toList();
    return parseLines(lines);
  }

  public static Level loadLevelFromResource(String resourcePath) throws IOException {
    var in = LevelLoader.class.getResourceAsStream(resourcePath);
    if (in == null) throw new IOException("level file not found: " + resourcePath);

    try (in;
        var br = new BufferedReader(new InputStreamReader(in))) {
      var lines = br.lines().filter(l -> !l.isBlank()).toList();
      return parseLines(lines);
    }
  }

  private static Level parseLines(List<String> lines) {
    if (lines.isEmpty()) throw new IllegalArgumentException("level file empty");

    var height = lines.size();
    var width = lines.getFirst().length();

    var cells = new CellType[width][height];
    var pins = new ArrayList<Pin>();
    Position snakeStart = null;

    for (int y = 0; y < height; y++) {
      var line = lines.get(y);
      if (line.length() != width)
        throw new IllegalArgumentException("inconsistent line length in level file");

      for (int x = 0; x < width; x++) {
        var c = line.charAt(x);
        var pos = new Position(x, y);

        switch (c) {
          case '#' -> cells[x][y] = CellType.WALL;
          case '^' -> {
            cells[x][y] = CellType.PIN_SLOT;
            pins.add(new Pin(pos, Pin.State.LOW, Direction.DOWN));
          }
          case 'v' -> {
            cells[x][y] = CellType.PIN_SLOT;
            pins.add(new Pin(pos, Pin.State.LOW, Direction.UP));
          }
          case '<' -> {
            cells[x][y] = CellType.PIN_SLOT;
            pins.add(new Pin(pos, Pin.State.LOW, Direction.RIGHT));
          }
          case '>' -> {
            cells[x][y] = CellType.PIN_SLOT;
            pins.add(new Pin(pos, Pin.State.LOW, Direction.LEFT));
          }
          case 'S' -> {
            cells[x][y] = CellType.EMPTY;
            snakeStart = pos;
          }
          default -> cells[x][y] = CellType.EMPTY;
        }
      }
    }

    if (snakeStart == null) throw new IllegalStateException("no start position 'S' in level file");

    return new Level(width, height, cells, List.copyOf(pins), snakeStart);
  }
}
