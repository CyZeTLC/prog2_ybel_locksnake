package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.model.Snake;
import de.hsbi.lockgame.ui.GamePanel;

import java.util.*;

public final class GameEngine implements Observer<Direction> {
    private final List<Observer<GameState>> observers;
    private GameState gameState;
    private GamePanel gamePanel;

    public GameEngine(Level level) {
        this.observers = new LinkedList<>();
        this.gameState = new GameState(level, new Snake(Collections.singletonList(level.snakeStart())), level.pins(), GameState.Status.RUNNING, Direction.UP);
    }

    public GameState state() {
        return this.gameState;
    }

    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
    }

    public void update(Direction d) {
        this.gameState.setPendingDirection(d);
        this.updateGameState(this.gameState);
    }

    /*
    Hier bin ich ehrlich, ich würde das hier normalerweise nicht so wissen, aber ich fand, dass die Aufgabenstellung es so will.
    Normalerweise würde ich einfach nur `this.gamePanel.update(this.state());` machen weil es ja einfach nur ein "Observer" ist.
     */
    public void notifyObserver(GameState state) {
        this.observers.forEach(observer -> observer.update(state));
    }

    public void tick() {
        this.updateGameState(this.gameState.tick());
    }

    public void updateGameState(GameState state) {
        this.gameState = state;
        this.notifyObserver(state);
    }

    public List<Observer<GameState>> getObservers() {
        return observers;
    }
}
