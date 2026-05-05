package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import java.util.List;

public final class GameState {

  public GameState(
      Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
    // TODO: lege einen neuen GameState mit den übergebenen Informationen an
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public Level level() {
    // TODO: Getter
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public Snake snake() {
    // TODO: Getter
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public List<Pin> pins() {
    // TODO: Getter
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public Status status() {
    // TODO: Getter
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public Direction pendingDirection() {
    // TODO: Getter
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public GameState tick() {
    // TODO: diese Methode lässt das Spiel einen Schritt laufen (berechnet den Spielzustand im
    // nächsten Schritt)

    // TODO: early exit: wenn das Spiel nicht läuft oder keine Blickrichtung gesetzt ist: keine
    // Änderung

    // TODO: prüfe die folgenden Bedingungen:
    // (a) Schlange würde das Spielfeld verlassen: Spiel verloren
    // (b) Schlange würde in ein Wandelement gehen: Blockiert (keine Bewegung, Blickrichtung "none")
    // (c) Schlange beisst sich: Spiel verloren
    // (d) Schlange würde auf einen Pin gehen (Pin bereits gesetzt oder Schlange kommt nicht in der
    // Aktivierungsrichtung): Blockiert (keine Bewegung, Blickrichtung "none")

    // TODO: aktiviere einen noch nicht gesetzten Pin, wenn die Schlange in der richtigen Richtung
    // auf den Pin gehen würde (die Schlange darf dabei aber nicht auf den Pin gehen)

    // TODO: anderenfalls: bewege die Schlange um einen Schritt in Blickrichtung (falls gesetzt)
    throw new UnsupportedOperationException("method not implemented yet");
  }

  public enum Status {
    RUNNING,
    WON,
    LOST_SELF_COLLISION,
    LOST_OUT_OF_BOUNDS;

    public boolean isRunning() {
      return this == RUNNING;
    }
  }
}
