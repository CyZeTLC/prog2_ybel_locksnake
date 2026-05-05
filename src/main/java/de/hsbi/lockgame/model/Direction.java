package de.hsbi.lockgame.model;

public enum Direction {
  UP,
  DOWN,
  LEFT,
  RIGHT,
  NONE;

  public Position applyTo(Position pos) {
    return switch (this) {
      case UP -> new Position(pos.x(), pos.y() - 1);
      case DOWN -> new Position(pos.x(), pos.y() + 1);
      case LEFT -> new Position(pos.x() - 1, pos.y());
      case RIGHT -> new Position(pos.x() + 1, pos.y());
      case NONE -> new Position(pos.x(), pos.y());
    };
  }

  public Direction oppositeDirection() {
    return switch (this) {
      case UP -> DOWN;
      case DOWN -> UP;
      case LEFT -> RIGHT;
      case RIGHT -> LEFT;
      case NONE -> NONE;
    };
  }
}
