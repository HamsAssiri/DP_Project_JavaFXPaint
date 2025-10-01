
package paint.controller;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.iShape;
import java.util.Map;

public interface IShapeFactory {
    iShape createShape(String type, Point2D start, Point2D end, Color color);
    iShape createShape(String type, Map<String, Double> properties);
    void registerShape(String type, Class<? extends iShape> shapeClass);
    java.util.List<String> getAvailableShapes();
}
