
package paint.view;

import java.util.ArrayList;
import paint.model.Shape;


public class CanvasManager {
 private static CanvasManager instance=null;
    private ArrayList<Shape> shapeList;

    // private constructor
    private CanvasManager() {
        shapeList = new ArrayList<>();
    }

    // global access point
    public static CanvasManager getInstance() {
        if (instance == null) {
            instance = new CanvasManager();
        }
        return instance;
    }

}
