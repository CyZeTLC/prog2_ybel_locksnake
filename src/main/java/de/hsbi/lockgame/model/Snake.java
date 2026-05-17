package de.hsbi.lockgame.model;

import java.util.ArrayList;
import java.util.List;

public final class Snake {
    private final List<Position> body;

    public Snake(List<Position> body) {
        if (body == null || body.isEmpty())
            throw new IllegalArgumentException("Snake: body must not be null/empty");
        body = List.copyOf(body);
        this.body = body;
    }

    public Position head() {
        return body.getFirst();
    }

    public List<Position> body() {
        return body;
    }

    public Position nextHead(Direction d) {
        return d.applyTo(head());
    }

    public boolean occupies(Position position) {
        return body.contains(position);
    }

    public Snake grow(Direction d) {
        var newHead = nextHead(d);
        var newBody = new ArrayList<Position>(body.size() + 1);
        newBody.add(newHead);
        newBody.addAll(body);
        return new Snake(newBody);
    }
}
