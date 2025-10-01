
package paint.controller;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.iShape;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ShapeFactory implements IShapeFactory {
    private static ShapeFactory instance;
    private final Map<String, Class<? extends iShape>> shapeRegistry = new HashMap<>();
    
    ShapeFactory() {
        // Private constructor for singleton
        initializeDefaultShapes();
    }
    
    public static synchronized ShapeFactory getInstance() {
        if (instance == null) {
            instance = new ShapeFactory();
        }
        return instance;
    }
    
   /* private void initializeDefaultShapes() {
        // Register all available shapes
        registerShape("Circle", paint.model.Circle.class);
        registerShape("Ellipse", paint.model.Ellipse.class);
        registerShape("Rectangle", paint.model.Rectangle.class);
        registerShape("Square", paint.model.Square.class);
        registerShape("Line", paint.model.Line.class);
        registerShape("Triangle", paint.model.Triangle.class);
    }*/
    
    private void initializeDefaultShapes() {
        try {
            //class names match exactly
            registerShape("Circle", paint.model.Circle.class);
            registerShape("Ellipse", paint.model.Ellipse.class);
            registerShape("Rectangle", paint.model.Rectangle.class);
            registerShape("Square", paint.model.Square.class);
            registerShape("Line", paint.model.Line.class);
            registerShape("Triangle", paint.model.Triangle.class);
        } catch (Exception e) {
            System.err.println("Error initializing shapes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void registerShape(String type, Class<? extends iShape> shapeClass) {
        shapeRegistry.put(type, shapeClass);
    }
    
    /*@Override
    public iShape createShape(String type, Point2D start, Point2D end, Color color) {
        if (!shapeRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Unknown shape type: " + type);
        }
        
        try {
            Class<? extends iShape> shapeClass = shapeRegistry.get(type);
            Constructor<? extends iShape> constructor = shapeClass.getDeclaredConstructor(
                Point2D.class, Point2D.class, Color.class);
            return constructor.newInstance(start, end, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create shape: " + type, e);
        }
    }*/
    public iShape createShape(String type, Point2D start, Point2D end, Color color) {
        if (!shapeRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Unknown shape type: " + type);
        }
        
        try {
            Class<? extends iShape> shapeClass = shapeRegistry.get(type);
            // Try to find the constructor
            java.lang.reflect.Constructor<? extends iShape> constructor = 
                shapeClass.getDeclaredConstructor(Point2D.class, Point2D.class, Color.class);
            return constructor.newInstance(start, end, color);
        } catch (Exception e) {
            System.err.println("Error creating shape " + type + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create shape: " + type, e);
        }
    }
    
   /* @Override
    public iShape createShape(String type, Map<String, Double> properties) {
        if (!shapeRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Unknown shape type: " + type);
        }
        
        try {
            Class<? extends iShape> shapeClass = shapeRegistry.get(type);
            Constructor<? extends iShape> constructor = shapeClass.getDeclaredConstructor();
            iShape shape = constructor.newInstance();
            shape.setProperties(properties);
            return shape;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create shape from properties: " + type, e);
        }
    }*/
    public iShape createShape(String type, Map<String, Double> properties) {
        if (!shapeRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Unknown shape type: " + type);
        }
        
        try {
            Class<? extends iShape> shapeClass = shapeRegistry.get(type);
            iShape shape = shapeClass.getDeclaredConstructor().newInstance();
            shape.setProperties(properties);
            return shape;
        } catch (Exception e) {
            System.err.println("Error creating shape from properties " + type + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create shape from properties: " + type, e);
        }
        }
    
    @Override
    public List<String> getAvailableShapes() {
        return new ArrayList<>(shapeRegistry.keySet());
    }
}


/* ORIGINAL CODE

package paint.controller;

import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.*;

//Factory DP
public class ShapeFactory {
    
    public ShapeFactory(){
        
    }
    
    public Shape createShape(String type, Point2D start, Point2D end, Color color){
        Shape temp=null;
        switch(type){
            case"Circle": temp = new Circle(start,end,color);break;
            case"Ellipse": temp = new Ellipse(start,end,color);break;
            case"Rectangle": temp = new Rectangle(start,end,color);break;
            case"Square": temp = new Square(start,end,color);break;
            case"Line": temp = new Line(start,end,color);break;
            case"Triangle": temp = new Triangle(start,end,color);break;
        }
        return temp;
    }
    
    public Shape createShape(String type, HashMap<String,Double> m){
        Shape temp=null;
        switch(type){
            case"Circle": temp = new Circle();break;
            case"Ellipse": temp = new Ellipse();break;
            case"Rectangle": temp = new Rectangle();break;
            case"Square": temp = new Square();break;
            case"Line": temp = new Line();break;
            case"Triangle": temp = new Triangle();break;
        }
        temp.setProperties(m);
        return temp;
    }
}
*/