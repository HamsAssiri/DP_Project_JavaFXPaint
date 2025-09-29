package paint.controller;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import paint.model.Shape;

public class CanvasManager {
    
    private static volatile CanvasManager instance;
    
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    
    private static final double DEFAULT_CANVAS_WIDTH = 833.33;
    private static final double DEFAULT_CANVAS_HEIGHT = 370.0;
    
    private CanvasManager() {
        
    }
    
    public static CanvasManager getInstance() {
        if (instance == null) {
            synchronized (CanvasManager.class) {
                if (instance == null) {
                    instance = new CanvasManager();
                }
            }
        }
        return instance;
    }
    
    public void initializeCanvas(Canvas canvas) {
        if (this.canvas == null) {
            this.canvas = canvas;
            this.graphicsContext = canvas.getGraphicsContext2D();
            
            // Set canvas dimensions if not already set
            if (canvas.getWidth() == 0 || canvas.getHeight() == 0) {
                canvas.setWidth(DEFAULT_CANVAS_WIDTH);
                canvas.setHeight(DEFAULT_CANVAS_HEIGHT);
            }
            
            // Initialize with default background color
            clearCanvas();
        }
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }
    
    public void clearCanvas() {
        if (graphicsContext != null) {
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            // Set default background color to white
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }
    
    public void drawShape(Shape shape) {
        if (canvas != null && shape != null) {
            shape.draw(canvas);
        }
    }
    
    public void redrawAll(ArrayList<Shape> shapes) {
        if (canvas == null || graphicsContext == null) {
            return;
        }
        
        // Clear the canvas
        clearCanvas();
        
        // Draw all shapes
        if (shapes != null) {
            for (Shape shape : shapes) {
                if (shape != null) {
                    shape.draw(canvas);
                }
            }
        }
    }
    
    public double[] getCanvasDimensions() {
        if (canvas != null) {
            return new double[]{canvas.getWidth(), canvas.getHeight()};
        }
        return new double[]{DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT};
    }
    
    public boolean isCanvasReady() {
        return canvas != null && graphicsContext != null;
    }
    

    public void setCanvasBackground(Color color) {
        if (graphicsContext != null) {
            graphicsContext.setFill(color);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }
    

    public boolean isWithinCanvasBounds(double x, double y) {
        if (canvas == null) {
            return false;
        }
        return x >= 0 && x <= canvas.getWidth() && y >= 0 && y <= canvas.getHeight();
    }
    
    public double getCanvasWidth() {
        return canvas != null ? canvas.getWidth() : DEFAULT_CANVAS_WIDTH;
    }
    

    public double getCanvasHeight() {
        return canvas != null ? canvas.getHeight() : DEFAULT_CANVAS_HEIGHT;
    }
    

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of CanvasManager singleton is not allowed");
    }
    
    public static synchronized void resetInstance() {
        instance = null;
    }
}