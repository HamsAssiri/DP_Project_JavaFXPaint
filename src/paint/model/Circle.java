
package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;


public class Circle extends Ellipse implements iShape{
    
    public Circle(Point2D startPos, Point2D endPos, Color strockColor) {
        super(startPos, endPos, strockColor);
        if(super.gethRadius()<super.getvRadius()){
            super.setvRadius(super.gethRadius());
        }else{
            super.sethRadius(super.getvRadius());
        }
        
    }

    public Circle() {
        
    }
    
   @Override
    public String getType() {
        return "Circle";
    }
    
    // Ensure clone returns IShape
    @Override
    public iShape clone() throws CloneNotSupportedException {
        return (iShape) super.clone();
    } 
    
    
    
}
