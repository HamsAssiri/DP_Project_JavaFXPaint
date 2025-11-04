// composit pattern new class
package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapeGroup implements iShape {
    private List<iShape> shapes = new ArrayList<>();
    private Point2D topLeft;
    private Point2D bottomRight;
    private Color color = Color.BLACK;  // Default color
    private Color fillColor = Color.TRANSPARENT;
    
    public ShapeGroup() {}
    
    public ShapeGroup(List<iShape> shapes) {
        this.shapes.addAll(shapes);
        calculateBoundingBox();
    }
    
    public void addShape(iShape shape) {
        shapes.add(shape);
        calculateBoundingBox();
    }
    
    public void removeShape(iShape shape) {
        shapes.remove(shape);
        calculateBoundingBox();
    }
    
    public List<iShape> getShapes() {
        return new ArrayList<>(shapes);
    }
    
    public boolean isEmpty() {
        return shapes.isEmpty();
    }
    
    private void calculateBoundingBox() {
        if (shapes.isEmpty()) {
            topLeft = new Point2D(0, 0);
            bottomRight = new Point2D(0, 0);
            return;
        }
        
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        
        for (iShape shape : shapes) {
            Point2D tl = shape.getTopLeft();
            Point2D end = shape.getEndPosition();
            
            minX = Math.min(minX, Math.min(tl.getX(), end.getX()));
            minY = Math.min(minY, Math.min(tl.getY(), end.getY()));
            maxX = Math.max(maxX, Math.max(tl.getX(), end.getX()));
            maxY = Math.max(maxY, Math.max(tl.getY(), end.getY()));
        }
        
        this.topLeft = new Point2D(minX, minY);
        this.bottomRight = new Point2D(maxX, maxY);
    }
    
    // iShape Interface Implementation
    @Override
    public String getType() {
        return "Group";
    }
    
    @Override
    public void setPosition(Point2D position) {
        if (shapes.isEmpty()) return;
        
        Point2D offset = position.subtract(topLeft);
        
        for (iShape shape : shapes) {
            Point2D currentPos = shape.getTopLeft();
            Point2D newPos = currentPos.add(offset);
            shape.setTopLeft(newPos);
        }
        calculateBoundingBox();
    }
    
    @Override
    public Point2D getPosition() {
        return topLeft;
    }
    
    @Override
    public Point2D getEndPosition() {
        return bottomRight;
    }
    
    @Override
    public void setProperties(Map<String, Double> properties) {
        // Apply properties to all shapes in group
        for (iShape shape : shapes) {
            shape.setProperties(properties);
        }
        calculateBoundingBox();
    }
    
    @Override
    public Map<String, Double> getProperties() {
        Map<String, Double> props = new HashMap<>();
        props.put("topLeftX", topLeft.getX());
        props.put("topLeftY", topLeft.getY());
        props.put("bottomRightX", bottomRight.getX());
        props.put("bottomRightY", bottomRight.getY());
        props.put("shapeCount", (double) shapes.size());
        return props;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
        for (iShape shape : shapes) {
            shape.setColor(color);
        }
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setFillColor(Color color) {
        this.fillColor = color;
        for (iShape shape : shapes) {
            shape.setFillColor(color);
        }
    }
    
    @Override
    public Color getFillColor() {
        return fillColor;
    }
    
    @Override
    public void draw(Canvas canvas) {
        for (iShape shape : shapes) {
            shape.draw(canvas);
        }
    }
    
    @Override
    public Point2D getTopLeft() {
        return topLeft;
    }
    
    @Override
    public void setTopLeft(Point2D position) {
        setPosition(position);
    }
    
    @Override
    public iShape clone() throws CloneNotSupportedException {
        ShapeGroup cloned = new ShapeGroup();
        for (iShape shape : shapes) {
            cloned.addShape(shape.clone());
        }
        cloned.color = this.color;
        cloned.fillColor = this.fillColor;
        return cloned;
    }
}