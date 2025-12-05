package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.iShape;

public class DeleteShapeCommand implements Command {
    private final ShapeReceiver receiver;
    private final iShape shape;
    private final int index;

    public DeleteShapeCommand(ShapeReceiver receiver, iShape shape, int index) {
        this.receiver = receiver;
        this.shape = shape;
        this.index = index;
    }

    @Override
    public void execute() {
        receiver.performRemoveAt(index);
    }

    @Override
    public void undo() {
        receiver.performAddAt(index, shape);
    }

    @Override
    public String getName() {
        return "DeleteShape";
    }
}