package paint.controller;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ShapeReceiver {
    private final List<iShape> shapeList;
    private final CanvasManager canvasManager;
    private final Stack<ArrayList<iShape>> historyStack;

    public ShapeReceiver(List<iShape> shapeList, CanvasManager canvasManager, Stack<ArrayList<iShape>> historyStack) {
        this.shapeList = shapeList;
        this.canvasManager = canvasManager;
        this.historyStack = historyStack;
    }

    // Basic operations
    public void performRemoveAt(int index) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.remove(index);
            canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
        }
    }

    public void performAddAt(int index, iShape shape) {
        shapeList.add(index, shape);
        canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
    }

    public void performSetTopLeftAt(int index, Point2D p) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.get(index).setTopLeft(p);
            canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
        }
    }

    public void performSetFillColorAt(int index, Color c) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.get(index).setFillColor(c);
            canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
        }
    }

    public void performReplaceAt(int index, iShape shape) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.set(index, shape);
            canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
        }
    }

    public iShape getShapeAt(int index) {
        if (index >= 0 && index < shapeList.size()) {
            return shapeList.get(index);
        }
        return null;
    }

    // Group operations
    public ShapeGroup groupShapesAtIndices(List<Integer> indices) {
        if (indices.size() < 2) return null;

        indices.sort((a, b) -> b - a); // sort descending
        List<iShape> selectedShapes = new ArrayList<>();
        for (int idx : indices) {
            selectedShapes.add(shapeList.get(idx));
            shapeList.remove(idx);
        }

        int insertIndex = indices.get(indices.size() - 1);
        ShapeGroup group = new ShapeGroup(selectedShapes);
        shapeList.add(insertIndex, group);

        canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);

        return group;
    }

    public void ungroupShape(int groupIndex) {
        if (groupIndex < 0 || groupIndex >= shapeList.size()) return;
        
        iShape shape = shapeList.get(groupIndex);
        if (!(shape instanceof ShapeGroup)) return;
        
        ShapeGroup group = (ShapeGroup) shape;
        List<iShape> children = group.getShapes();

        shapeList.remove(groupIndex);
        shapeList.addAll(groupIndex, children);

        canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
    }

    public void restoreGroupAt(int index, ShapeGroup group) {
        if (index >= 0 && index <= shapeList.size()) {
            shapeList.add(index, group);
            canvasManager.refreshWithHistory((ArrayList<iShape>) shapeList, historyStack);
        }
    }

    // Decorator operations
    public iShape toggleBorder(iShape s) {
        iShape core = unwrap(s);
        if (core instanceof ShapeGroup g) {
            List<iShape> kids = new ArrayList<>();
            for (iShape child : g.getShapes()) {
                if (hasDecorator(child, BorderDecorator.class)) {
                    kids.add(removeDecorator(child, BorderDecorator.class));
                } else {
                    kids.add(new BorderDecorator(child));
                }
            }
            return new ShapeGroup(kids);
        }

        return hasDecorator(s, BorderDecorator.class)
                ? removeDecorator(s, BorderDecorator.class)
                : new BorderDecorator(s);
    }

    public iShape toggleShadow(iShape s) {
        iShape core = unwrap(s);
        if (core instanceof ShapeGroup g) {
            List<iShape> kids = new ArrayList<>();
            for (iShape child : g.getShapes()) {
                if (hasDecorator(child, ShadowDecorator.class)) {
                    kids.add(removeDecorator(child, ShadowDecorator.class));
                } else {
                    kids.add(new ShadowDecorator(child));
                }
            }
            return new ShapeGroup(kids);
        }

        return hasDecorator(s, ShadowDecorator.class)
                ? removeDecorator(s, ShadowDecorator.class)
                : new ShadowDecorator(s);
    }

    // Helper methods for decorators
    private boolean hasDecorator(iShape s, Class<? extends ShapeDecorator> clazz) {
        iShape cur = s;
        while (cur instanceof ShapeDecorator) {
            if (clazz.isInstance(cur)) return true;
            cur = ((ShapeDecorator) cur).getDecoratedShape();
        }
        return false;
    }

    private iShape removeDecorator(iShape s, Class<? extends ShapeDecorator> clazz) {
        return ShapeDecorator.removeDecoratorOfClass(s, clazz);
    }

    private iShape unwrap(iShape s) {
        iShape cur = s;
        while (cur instanceof ShapeDecorator) {
            cur = ((ShapeDecorator) cur).getDecoratedShape();
        }
        return cur;
    }
}
