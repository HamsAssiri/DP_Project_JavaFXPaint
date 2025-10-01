package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import java.util.Map;

public interface iShape extends Cloneable{
    
    //Added
    String getType();

    public void setPosition(javafx.geometry.Point2D position);
    
    public javafx.geometry.Point2D getPosition();
    
    /* update shape specific properties (e.g., radius) */
    public void setProperties(java.util.Map<String, Double> properties);
    public java.util.Map<String, Double> getProperties();
    
    public void setColor(javafx.scene.paint.Color color);
    
    public javafx.scene.paint.Color getColor();
    
    public void setFillColor(javafx.scene.paint.Color color);
    
    public javafx.scene.paint.Color getFillColor();
    
    /* redraw the shape on the canvas,
    for swing, you will cast canvas to java.awt.Graphics */
    public void draw(javafx.scene.canvas.Canvas canvas);
    
    /* create a deep clone of the shape */
    //public Object clone() throws CloneNotSupportedException;
    
    //modified
    iShape clone() throws CloneNotSupportedException;
    
    //used in code but never declared 
    Point2D getTopLeft();
    void setTopLeft(Point2D position);
    Point2D getEndPosition();
}