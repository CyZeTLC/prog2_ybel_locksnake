package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;

import java.util.List;
import java.util.Optional;

public final class GameState {
    private Level level;
    private Snake snake;
    private List<Pin> pins;
    private Status status;
    private Direction pendingDirection;

    public GameState(
                     Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
        this.level = level;
        this.snake = snake;
        this.pins = pins;
        this.status = status;
        this.pendingDirection = pendingDirection;
    }

    public Level level() {
        return this.level;
    }

    public Snake snake() {
        return this.snake;
    }

    public List<Pin> pins() {
        return this.pins;
    }

    public Status status() {
        return this.status;
    }

    public Direction pendingDirection() {
        return this.pendingDirection;
    }

    public void setPendingDirection(Direction pendingDirection) {
        this.pendingDirection = pendingDirection;
    }

    public GameState tick() {
        if (!this.status.isRunning() || this.pendingDirection == Direction.NONE) return this;

        if (this.checkAllPinsSet()) {
            this.status = Status.WON;
            return this;
        }

        Position newHead = this.snake.nextHead(this.pendingDirection);

        if (!level.isInside(newHead)) {
            this.status = Status.LOST_OUT_OF_BOUNDS;
            return this;
        }

        if (level.cellAt(newHead) == CellType.WALL) {
            this.pendingDirection = Direction.NONE;
            return this;
        }

        if (this.snake().body().contains(newHead)) {
            this.status = Status.LOST_SELF_COLLISION;
            return this;
        }

        if (level.cellAt(newHead) == CellType.PIN_SLOT) {
            Optional<Pin> pin = this.getPinAt(newHead);
            pendingDirection = Direction.NONE;

            if (pin.isPresent()) {
                if ((pin.get().state().isSet() || pin.get().activationDirection() != pendingDirection)) {
                    return this;
                }
            }

            List<Pin> updatedPins = pins.stream().map(
                    currentPin -> currentPin.position().equals(newHead) ? currentPin.withState(Pin.State.HIGH) : currentPin
            ).toList();

            return new GameState(this.level, this.snake, updatedPins, this.status, this.pendingDirection);
        }

        return new GameState(this.level, this.snake.grow(this.pendingDirection), this.pins, this.status, this.pendingDirection);
    }

    public Optional<Pin> getPinAt(Position position) {
        return this.pins().stream().filter(pin -> pin.position().equals(position)).findFirst();
    }

    public boolean checkAllPinsSet() {
        return this.pins().stream().map(Pin::state).allMatch(Pin.State::isSet);
    }

    public enum Status {
        RUNNING, WON, LOST_SELF_COLLISION, LOST_OUT_OF_BOUNDS;

        public boolean isRunning() {
            return this == RUNNING;
        }
    }
}
