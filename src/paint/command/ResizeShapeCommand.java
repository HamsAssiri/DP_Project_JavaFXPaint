package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.iShape;

public class ResizeShapeCommand implements Command {
    private final ShapeReceiver receiver;
    private final int index;
    private final iShape oldShape;
    private final iShape newShape;

    public ResizeShapeCommand(ShapeReceiver receiver, int index, iShape oldShape, iShape newShape) {
        this.receiver = receiver;
        this.index = index;
        this.oldShape = oldShape;
        this.newShape = newShape;
    }

    @Override
    public void execute() {
        receiver.performReplaceAt(index, newShape);
    }

    @Override
    public void undo() {
        receiver.performReplaceAt(index, oldShape);
    }

    @Override
    public String getName() {
        return "ResizeShape";
    }
}