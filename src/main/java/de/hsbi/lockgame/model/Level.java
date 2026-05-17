package de.hsbi.lockgame.model;

import java.util.List;

public final class Level {
    private final int width;
    private final int height;
    private final CellType[][] cells;
    private final List<Pin> pins;
    private final Position snakeStart;

    public Level(int width, int height, CellType[][] cells, List<Pin> pins, Position snakeStart) {
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.pins = pins;
        this.snakeStart = snakeStart;
    }

    public boolean isInside(Position pos) {
        return pos.x() >= 0 && pos.x() < width && pos.y() >= 0 && pos.y() < height;
    }

    public CellType cellAt(Position pos) {
        return cells[pos.x()][pos.y()];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public CellType[][] cells() {
        return cells;
    }

    public List<Pin> pins() {
        return pins;
    }

    public Position snakeStart() {
        return snakeStart;
    }
}
