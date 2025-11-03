package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.StrokeLineCap;

public class BorderDecorator extends ShapeDecorator {

    private Color borderColor = Color.BLACK;
    private double borderWidth = 3.0;

    public BorderDecorator(iShape decoratedShape) {
        super(decoratedShape);
    }

    public BorderDecorator(iShape decoratedShape, Color borderColor, double borderWidth) {
        super(decoratedShape);
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
    }

    public void setBorderColor(Color c) {
        this.borderColor = c;
    }

    public void setBorderWidth(double w) {
        this.borderWidth = w;
    }

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);

        Color shapeColor = decoratedShape.getColor();
        gc.setStroke(shapeColor != null ? shapeColor : borderColor);

        gc.setLineWidth(borderWidth);

        Point2D tl = decoratedShape.getTopLeft();
        Point2D end = decoratedShape.getEndPosition();
        double bx = Math.min(tl.getX(), end.getX());
        double by = Math.min(tl.getY(), end.getY());
        double bw = Math.abs(end.getX() - tl.getX());
        double bh = Math.abs(end.getY() - tl.getY());

        if (decoratedShape instanceof Circle || decoratedShape instanceof Ellipse) {
            Ellipse e = (Ellipse) decoratedShape;
            double oxp = e.getTopLeft().getX();
            double oyp = e.getTopLeft().getY();
            double ow = e.gethRadius() * 2;
            double oh = e.getvRadius() * 2;
            gc.strokeOval(oxp, oyp, ow, oh);

        } else if (decoratedShape instanceof Rectangle || decoratedShape instanceof Square) {
            Rectangle r = (Rectangle) decoratedShape;
            double oxp = r.getTopLeft().getX();
            double oyp = r.getTopLeft().getY();
            double ow = r.getWidth();
            double oh = r.getHeight();
            gc.strokeRect(oxp, oyp, ow, oh);

        } else if (decoratedShape instanceof Line) {
            Line l = (Line) decoratedShape;
            gc.strokeLine(
                    l.getPosition().getX(),
                    l.getPosition().getY(),
                    l.getEndPosition().getX(),
                    l.getEndPosition().getY()
            );

        } else if (decoratedShape instanceof Triangle) {
            Triangle tri = (Triangle) decoratedShape;
            try {
                Point2D third = tri.getThirdPoint();
                gc.strokePolygon(
                        new double[]{tri.getPosition().getX(), tri.getEndPosition().getX(), third.getX()},
                        new double[]{tri.getPosition().getY(), tri.getEndPosition().getY(), third.getY()},
                        3
                );
            } catch (Exception ex) {
                gc.strokeRect(bx, by, bw, bh);
            }

        } else {
            gc.strokeRect(bx, by, bw, bh);
        }

        gc.restore();
        decoratedShape.draw(canvas);
    }

    @Override
    public iShape clone() throws CloneNotSupportedException {
        iShape cw = decoratedShape.clone();
        BorderDecorator b = new BorderDecorator(cw);
        b.borderColor = this.borderColor;
        b.borderWidth = this.borderWidth;
        return b;
    }
}
