package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import java.util.Map;

public abstract class ShapeDecorator implements iShape {
    protected final iShape decoratedShape;

    public ShapeDecorator(iShape decoratedShape) {
        this.decoratedShape = decoratedShape;
    }

    public iShape getDecoratedShape() { return decoratedShape; }

    @Override public String getType() { return decoratedShape.getType(); }
    @Override public void setPosition(Point2D position) { decoratedShape.setPosition(position); }
    @Override public Point2D getPosition() { return decoratedShape.getPosition(); }
    @Override public void setProperties(Map<String, Double> properties) { decoratedShape.setProperties(properties); }
    @Override public Map<String, Double> getProperties() { return decoratedShape.getProperties(); }
    @Override public void setColor(Color color) { decoratedShape.setColor(color); }
    @Override public Color getColor() { return decoratedShape.getColor(); }
    @Override public void setFillColor(Color color) { decoratedShape.setFillColor(color); }
    @Override public Color getFillColor() { return decoratedShape.getFillColor(); }
    @Override public Point2D getTopLeft() { return decoratedShape.getTopLeft(); }
    @Override public void setTopLeft(Point2D position) { decoratedShape.setTopLeft(position); }
    @Override public Point2D getEndPosition() { return decoratedShape.getEndPosition(); }

    @Override public abstract void draw(Canvas canvas);
    @Override public abstract iShape clone() throws CloneNotSupportedException;

    public static iShape unwrapAll(iShape s) {
        while (s instanceof ShapeDecorator) s = ((ShapeDecorator) s).getDecoratedShape();
        return s;
    }

    public static iShape removeOuterDecorator(iShape s) {
        if (s instanceof ShapeDecorator) return ((ShapeDecorator) s).getDecoratedShape();
        return s;
    }

    public static iShape removeDecoratorOfClass(iShape s, Class<? extends ShapeDecorator> cls) {
        if (!(s instanceof ShapeDecorator)) return s;
        ShapeDecorator outer = (ShapeDecorator) s;
    if (cls.isInstance(outer)) return outer.getDecoratedShape();

    iShape inner = removeDecoratorOfClass(outer.getDecoratedShape(), cls);
    if (inner == outer.getDecoratedShape()) return outer; 
        try {
            return outer.getClass().getDeclaredConstructor(iShape.class).newInstance(inner);
        } catch (Exception e) {
            return inner;
        }
    }
}
