package paint.controller;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.iShape;



public interface DrawingEngine {
    
    /* redraw all shapes on the canvas */
    public void refresh(Object canvas);
    
    /* return the created shapes objects */
    public iShape[] getShapes();
    
    /* limited to 20 steps. You consider these actions in
    * undo & redo: addShape, removeShape, updateShape */
    public void undo();
    public void redo();
    
    /* use the file extension to determine the type,
    * or throw runtime exception when unexpected extension */
    public void save(String path);
    public void load(String path);
    
// *************** bonus functions ****************
    /* return the classes (types) of supported shapes already exist and the
    * ones that can be dynamically loaded at runtime (see Part 4) */
    public java.util.List<Class<? extends iShape>> getSupportedShapes();
    
    /* add to the supported shapes the new shape class (see Part 4) */
    public void installPluginShape(String jarPath);
}