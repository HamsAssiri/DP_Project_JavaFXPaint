package paint.controller;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import paint.model.iShape;

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
    
    public void clearCanvas() {
        if (graphicsContext != null) {
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }
    
    public void drawShape(iShape shape) {
        if (canvas != null && shape != null) {
            shape.draw(canvas);
        }
    }
    
    public void redrawAll(ArrayList<iShape> shapes) {
        if (canvas == null || graphicsContext == null) {
            return;
        }
        clearCanvas();
        if (shapes != null) {
            for (iShape shape : shapes) {
                if (shape != null) {
                    shape.draw(canvas);
                }
            }
        }
    }
    
    public void refreshWithHistory(ArrayList<iShape> shapes, Stack<ArrayList<iShape>> historyStack) {
        try {
            historyStack.push(new ArrayList<>(cloneList(shapes)));
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(CanvasManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        redrawAll(shapes);
    }
    
    private ArrayList<iShape> cloneList(ArrayList<iShape> shapes) throws CloneNotSupportedException {
        ArrayList<iShape> clonedList = new ArrayList<>();
        for (iShape shape : shapes) {
            clonedList.add(shape.clone());
        }
        return clonedList;
    }
}