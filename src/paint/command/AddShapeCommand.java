package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.iShape;

public class AddShapeCommand implements Command {
    private final ShapeReceiver receiver;
    private final iShape shape;
    private final int insertIndex;

    public AddShapeCommand(ShapeReceiver receiver, iShape shape, int insertIndex) {
        this.receiver = receiver;
        this.shape = shape;
        this.insertIndex = insertIndex;
    }

    @Override
    public void execute() {
        receiver.performAddAt(insertIndex, shape);
    }

    @Override
    public void undo() {
        receiver.performRemoveAt(insertIndex);
    }

    @Override
    public String getName() {
        return "AddShape";
    }
}