package paint.controller;

import java.util.ArrayList;
import java.util.List;
import paint.model.iShape;

public class CanvasVersionMemento {

    // Snapshot of shapes at save time
    private final List<iShape> shapesSnapshot;
    private final String name;

    public CanvasVersionMemento(List<iShape> shapes, String name) {
        // Copy current list reference content
        this.shapesSnapshot = new ArrayList<>(shapes);
        this.name = name;
    }

    public List<iShape> getShapesSnapshot() {
        // Return a copy to protect internal list
        return new ArrayList<>(shapesSnapshot);
    }

    public String getName() {
        return name;
    }
}

