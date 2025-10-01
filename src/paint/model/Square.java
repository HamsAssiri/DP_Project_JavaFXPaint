
package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;


public class Square extends Rectangle implements iShape{
    
    public Square(Point2D startPos, Point2D endPos, Color strockColor) {
        super(startPos, endPos, strockColor);
        if(super.getHeight()<super.getWidth()){
            super.setWidth(super.getHeight());
        }else{
            super.setHeight(super.getWidth());
        }
    }

    public Square() {
        
    }
    @Override
    public String getType() {
        return "Square";
    }
    
    // Ensure clone returns IShape
    @Override
    public iShape clone() throws CloneNotSupportedException {
        return (iShape) super.clone();
    } 
    
}
