package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.iShape;
import paint.model.ShapeDecorator;
import paint.model.ShadowDecorator;

public class ShadowDecoratorCommand implements Command {
    private final ShapeReceiver receiver;
    private final int index;
    private iShape oldShape;
    private iShape newShape;

    public ShadowDecoratorCommand(ShapeReceiver receiver, int index) {
        this.receiver = receiver;
        this.index = index;
    }

    @Override
    public void execute() {
        oldShape = receiver.getShapeAt(index);
        newShape = receiver.toggleShadow(oldShape); // create a new shape with shadow toggled
        receiver.performReplaceAt(index, newShape);
    }

    @Override
    public void undo() {
        receiver.performReplaceAt(index, oldShape);
    }

    @Override
    public String getName() {
        return "ToggleShadow";
    }
}
