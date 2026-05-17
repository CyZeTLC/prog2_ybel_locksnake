import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.model.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void stateShouldRetainAllPassedConstructorArguments() {
        // Given: A custom map layout setup with distinct elements
        var level = level("#######",
            "#..S..#",
            "#..^..#",
            "#######");

        // When: The game state is instantiated
        var state = runningState(level, Direction.NONE);

        // Then: Read accessors must return the exact provided objects and dimensions
        assertAll(
            () -> assertSame(level, state.level()),
            () -> assertEquals(new Position(3, 1), state.snake().head()),
            () -> assertEquals(1, state.pins().size()),
            () -> assertEquals(GameState.Status.RUNNING, state.status())
        );
    }

    @Test
    void tickShouldReturnSameInstanceWhenSnakeHasNoPendingDirection() {
        // Given: A running state with an idle snake
        var level = level("####",
            "#S.#",
            "####");
        var state = runningState(level, Direction.NONE);

        // When: The system process execution triggers a tick
        var next = state.tick();

        // Then: No execution logic should mutate the state or return a new object
        assertSame(state, next);
    }

    @Test
    void snakeShouldAdvanceAndExtendBodyWhenMovingIntoEmptySpace() {
        // Given: A snake positioned with clear space below it, and an extra low pin so game doesn't auto-win
        var level = level("#####",
            "#S..#",
            "#..v#",
            "#####");
        var state = runningState(level, Direction.DOWN);

        // When: Processing the downward movement tick
        var next = state.tick();

        // Then: The head updates its coordinates and the body track extends
        assertAll(
            () -> assertEquals(new Position(1, 2), next.snake().head()),
            () -> assertEquals(List.of(new Position(1, 2), new Position(1, 1)), next.snake().body()),
            () -> assertEquals(Direction.DOWN, next.pendingDirection())
        );
    }

    @Test
    void movementShouldBePreventedAndDirectionClearedWhenHittingObstacle() {
        // Given: A horizontal wall immediately blocking a downward path, with an extra low pin
        var level = level("###",
            "#S#",
            "#v#",
            "###");
        var state = runningState(level, Direction.DOWN);

        // When: The snake attempts to breach the wall boundary
        var next = state.tick();

        // Then: The pending direction drops to NONE and position is locked
        assertAll(
            () -> assertEquals(new Position(1, 1), next.snake().head()),
            () -> assertEquals(Direction.NONE, next.pendingDirection()),
            () -> assertEquals(GameState.Status.RUNNING, next.status())
        );
    }

    @Test
    void selfCollisionShouldTransitionGameStatusToLost() {
        // Given: A multi-segmented snake coiled to crash into its own tail segment, with an extra low pin
        var level = level("####",
            "#S.#",
            "#^v#",
            "####");
        var snake = new Snake(List.of(new Position(1, 1), new Position(2, 1), new Position(2, 2), new Position(1, 2)));
        var state = new GameState(level, snake, level.pins(), GameState.Status.RUNNING, Direction.RIGHT);

        // When: Moving into the occupied tail coordinate
        var next = state.tick();

        // Then: The state reflects a terminal self-collision failure
        assertAll(
            () -> assertEquals(GameState.Status.LOST_SELF_COLLISION, next.status()),
            () -> assertEquals(new Position(1, 1), next.snake().head())
        );
    }

    @Test
    void enteringPinSlotFromIncorrectAngleShouldFreezeMovement() {
        // Given: An upward-activating pin reached by a snake moving downwards, plus an extra low pin
        var level = level("####",
            "#S.#",
            "#vv#",
            "####"); // Left 'v' expects UP entry, DOWN movement fails. Right 'v' keeps game from winning.
        var state = runningState(level, Direction.DOWN);

        // When: Moving down into the unaligned pin mechanism
        var next = state.tick();

        // Then: The step is cancelled, setting direction to NONE without activating it
        assertAll(
            () -> assertEquals(new Position(1, 1), next.snake().head()),
            () -> assertEquals(Direction.NONE, next.pendingDirection()),
            () -> assertEquals(Pin.State.LOW, pinAt(next, new Position(1, 2)).state())
        );
    }

    @Test
    void activatedPinShouldActAsSolidImpassableBlockade() {
        // Given: A path blocked by a pin that has already been turned HIGH, plus an extra low pin
        var level = level("#####",
            "#S^v#",
            "#####");
        var activePins = level.pins().stream()
            .map(pin -> pin.position().equals(new Position(2, 1)) ? pin.withState(Pin.State.HIGH) : pin)
            .toList();
        var state = new GameState(level, new Snake(List.of(level.snakeStart())), activePins, GameState.Status.RUNNING, Direction.RIGHT);

        // When: Driving the snake head into the locked slot
        var next = state.tick();

        // Then: The tile is treated like a wall, killing the movement input
        assertAll(
            () -> assertEquals(new Position(1, 1), next.snake().head()),
            () -> assertEquals(Direction.NONE, next.pendingDirection())
        );
    }

    @Test
    void boundaryViolationShouldInstantlyTriggerOutOfBoundsLoss() {
        // Given: A snake sitting at the upper map border moving up, with an extra low pin
        var level = level(" S ",
            " v ");
        var state = runningState(level, Direction.UP);

        // When: Forcing an illegal out-of-bounds cross
        var next = state.tick();

        // Then: The engine terminates the game loop via boundary check failure
        assertAll(
            () -> assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, next.status()),
            () -> assertEquals(new Position(1, 0), next.snake().head())
        );
    }

    @Test
    void preCompletedPinStatesShouldAutoWinOnFirstTick() {
        // Given: A game initialized where every game-winning condition is already met
        var level = level("###",
            "#S#",
            "###");
        var fullyChargedPins = List.of(new Pin(new Position(2, 2), Pin.State.HIGH, Direction.UP));
        var state = new GameState(level, new Snake(List.of(level.snakeStart())), fullyChargedPins, GameState.Status.RUNNING, Direction.RIGHT);

        // When: The update cycle queries the completion check
        var next = state.tick();

        // Then: The status shifts immediately to WON before assessing any movement
        assertEquals(GameState.Status.WON, next.status());
    }

    @Test
    void terminalStatesShouldShortCircuitAndIgnoreTickCalls() {
        // Given: A game context that already concluded with a loss
        var level = level("###", "#S#", "###");
        var state = new GameState(level, new Snake(List.of(level.snakeStart())), level.pins(), GameState.Status.LOST_SELF_COLLISION, Direction.DOWN);

        // When: Trying to push a tick into an ended state
        var next = state.tick();

        // Then: Execution bails out early, shifting nothing
        assertSame(state, next);
    }

    private static GameState runningState(Level level, Direction direction) {
        return new GameState(
            level,
            new Snake(List.of(level.snakeStart())),
            level.pins(),
            GameState.Status.RUNNING,
            direction);
    }

    private static Pin pinAt(GameState state, Position position) {
        return state.pins().stream()
            .filter(pin -> pin.position().equals(position))
            .findFirst()
            .orElseThrow();
    }

    private static Level level(String... rows) {
        int height = rows.length;
        int width = rows[0].length();

        if (java.util.Arrays.stream(rows).anyMatch(row -> row.length() != width)) {
            throw new IllegalArgumentException("rows must have same width");
        }

        CellType[][] cells = new CellType[width][height];
        List<Pin> pins = new ArrayList<>();
        Position start = null;

        for (int y = 0; y < height; y++) {
            String currentRow = rows[y];
            for (int x = 0; x < width; x++) {
                char symbol = currentRow.charAt(x);
                Position currentPos = new Position(x, y);

                cells[x][y] = (symbol == '#') ? CellType.WALL : CellType.EMPTY;

                if (symbol == 'S') {
                    start = currentPos;
                } else if (symbol == '^' || symbol == 'v' || symbol == '<' || symbol == '>') {
                    cells[x][y] = CellType.PIN_SLOT;
                    Direction requiredDir = determineActivationDirection(symbol);
                    pins.add(new Pin(currentPos, Pin.State.LOW, requiredDir));
                }
            }
        }

        Position finalStart = start;
        java.util.Optional.ofNullable(finalStart)
            .orElseThrow(() -> new IllegalArgumentException("level needs a start position"));

        return new Level(width, height, cells, List.copyOf(pins), finalStart);
    }

    private static Direction determineActivationDirection(char symbol) {
        return switch (symbol) {
            case '^' -> Direction.DOWN;
            case 'v' -> Direction.UP;
            case '<' -> Direction.RIGHT;
            case '>' -> Direction.LEFT;
            default -> Direction.NONE;
        };
    }
}
