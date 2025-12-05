package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.iShape;

public class CopyShapeCommand implements Command {
    private final ShapeReceiver receiver;
    private final iShape copiedShape;
    private final int insertIndex;

    public CopyShapeCommand(ShapeReceiver receiver, iShape copiedShape, int insertIndex) {
        this.receiver = receiver;
        this.copiedShape = copiedShape;
        this.insertIndex = insertIndex;
    }

    @Override
    public void execute() {
        receiver.performAddAt(insertIndex, copiedShape);
    }

    @Override
    public void undo() {
        receiver.performRemoveAt(insertIndex);
    }

    @Override
    public String getName() {
        return "CopyShape";
    }
}