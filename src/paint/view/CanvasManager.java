package paint.view;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import paint.model.Shape;

public class CanvasManager {
    private static CanvasManager instance = null;
    private ArrayList<Shape> shapeList;
    private Canvas canvas;

    // private constructor
    private CanvasManager() {
        shapeList = new ArrayList<>();
        canvas = new Canvas(850, 370);
    }

    // global access point
    public static CanvasManager getInstance() {
        if (instance == null) {
            instance = new CanvasManager();
        }
        return instance;

    }

    public Canvas getCanvas() {
        return canvas;
    }

    public ArrayList<Shape> getShapeList() {
        return shapeList;
    }

    public void addShape(Shape shape) {
        shapeList.add(shape);
    }

    public void removeShape(Shape shape) {
        shapeList.remove(shape);
    }
}
