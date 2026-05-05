package de.hsbi.lockgame.settings;

import java.util.Map;

public class LevelConstants {
  public enum Id {
    LEVEL1,
    LEVEL2
  }

  private static final Map<Id, String> LEVELS =
      Map.of(
          Id.LEVEL1, "/levels/level1.txt",
          Id.LEVEL2, "/levels/level2.txt");

  public static String defaultLevel() {
    return LEVELS.get(Id.LEVEL1);
  }

  public static String levelById(Id id) {
    return LEVELS.get(id);
  }
}
