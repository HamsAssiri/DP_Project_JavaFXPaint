package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.StrokeLineCap;

public class ShadowDecorator extends ShapeDecorator {
    private Color color = Color.color(0, 0, 0, 0.32);
    private int layers = 5;
    private double offsetX = 6.0;
    private double offsetY = 6.0;
    private double padding = 2.0;

    public ShadowDecorator(iShape decoratedShape) { super(decoratedShape); }

    public ShadowDecorator(iShape decoratedShape, Color color, int layers, double offsetX, double offsetY) {
        super(decoratedShape);
        this.color = color; this.layers = layers; this.offsetX = offsetX; this.offsetY = offsetY;
    }

    public void setColor(Color c) { this.color = c; }
    public void setLayers(int l) { this.layers = l; }
    public void setOffset(double x, double y){ this.offsetX = x; this.offsetY = y; }
    public void setPadding(double p) { this.padding = p; }

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);

        // Use the innermost wrapped shape so instanceof checks match the concrete shape
        iShape core = ShapeDecorator.unwrapAll(decoratedShape);
        Point2D tl = core.getTopLeft();
        Point2D end = core.getEndPosition();
        double bx = Math.min(tl.getX(), end.getX());
        double by = Math.min(tl.getY(), end.getY());
        double bw = Math.abs(end.getX() - tl.getX());
        double bh = Math.abs(end.getY() - tl.getY());

        for (int i = layers; i >= 1; --i) {
            double t = (double) i / Math.max(1, layers);
            double ox = offsetX * t;
            double oy = offsetY * t;
            double alpha = color.getOpacity() * (0.24 * t);
            Color c = Color.color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            gc.setFill(c);
            gc.setStroke(c);

            if (core instanceof Circle || core instanceof Ellipse) {
                Ellipse e = (Ellipse) core;
                double oxp = e.getTopLeft().getX();
                double oyp = e.getTopLeft().getY();
                double ow = e.gethRadius() * 2;
                double oh = e.getvRadius() * 2;
                gc.fillOval(oxp + ox - padding, oyp + oy - padding, ow + padding * 2, oh + padding * 2);

            } else if (core instanceof Rectangle || core instanceof Square) {
                Rectangle r = (Rectangle) core;
                double oxp = r.getTopLeft().getX();
                double oyp = r.getTopLeft().getY();
                double ow = r.getWidth();
                double oh = r.getHeight();
                gc.fillRect(oxp + ox - padding, oyp + oy - padding, ow + padding * 2, oh + padding * 2);

            } else if (core instanceof Line) {
                Line l = (Line) core;
                double x1 = l.getPosition().getX() + ox;
                double y1 = l.getPosition().getY() + oy;
                double x2 = l.getEndPosition().getX() + ox;
                double y2 = l.getEndPosition().getY() + oy;
                gc.setLineWidth(4.0 * t + 1.0);
                gc.strokeLine(x1, y1, x2, y2);

            } else if (core instanceof Triangle) {
                Triangle tri = (Triangle) core;
                double x1 = tri.getPosition().getX() + ox;
                double y1 = tri.getPosition().getY() + oy;
                double x2 = tri.getEndPosition().getX() + ox;
                double y2 = tri.getEndPosition().getY() + oy;
                try {
                    Point2D third = tri.getThirdPoint();
                    double x3 = third.getX() + ox;
                    double y3 = third.getY() + oy;
                    gc.fillPolygon(new double[]{x1, x2, x3}, new double[]{y1, y2, y3}, 3);
                } catch (Exception ex) {
                    gc.fillRoundRect(bx + ox - padding, by + oy - padding, bw + padding * 2, bh + padding * 2, Math.max(6, padding * 2), Math.max(6, padding * 2));
                }

            } else {
                gc.fillRoundRect(bx + ox - padding, by + oy - padding, bw + padding * 2, bh + padding * 2, Math.max(6, padding * 2), Math.max(6, padding * 2));
            }
        }

        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        gc.restore();

        decoratedShape.draw(canvas);
    }

    @Override
    public iShape clone() throws CloneNotSupportedException {
        iShape cw = decoratedShape.clone();
        ShadowDecorator s = new ShadowDecorator(cw);
        s.color = this.color; s.layers = this.layers; s.offsetX = this.offsetX; s.offsetY = this.offsetY; s.padding = this.padding;
        return s;
    }
}
