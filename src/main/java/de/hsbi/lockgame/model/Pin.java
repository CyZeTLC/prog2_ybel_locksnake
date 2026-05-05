package de.hsbi.lockgame.model;

public final class Pin {
  private final Position position;
  private final State state;
  private final Direction activationDirection;

  public Pin(Position position, State state, Direction activationDirection) {
    this.position = position;
    this.state = state;
    this.activationDirection = activationDirection;
  }

  public Pin withState(State newState) {
    return new Pin(position, newState, activationDirection);
  }

  public Position position() {
    return position;
  }

  public State state() {
    return state;
  }

  public Direction activationDirection() {
    return activationDirection;
  }

  public enum State {
    LOW,
    HIGH;

    public boolean isSet() {
      return this == HIGH;
    }
  }
}
