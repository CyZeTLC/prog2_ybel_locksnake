package de.hsbi.lockgame.settings;

import de.hsbi.lockgame.model.Direction;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

public final class InputConstants {
  public static final Map<Direction, List<Integer>> BINDINGS =
      Map.of(
          Direction.UP, List.of(KeyEvent.VK_UP, KeyEvent.VK_W, KeyEvent.VK_K),
          Direction.DOWN, List.of(KeyEvent.VK_DOWN, KeyEvent.VK_S, KeyEvent.VK_J),
          Direction.LEFT, List.of(KeyEvent.VK_LEFT, KeyEvent.VK_A, KeyEvent.VK_H),
          Direction.RIGHT, List.of(KeyEvent.VK_RIGHT, KeyEvent.VK_D, KeyEvent.VK_L));
}
