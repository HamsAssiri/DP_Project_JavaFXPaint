package paint.command;

import javafx.scene.paint.Color;
import paint.controller.ShapeReceiver;
import paint.model.iShape;
import paint.model.ShapeDecorator;
import paint.model.BorderDecorator;

public class BorderDecoratorCommand implements Command {
    private final ShapeReceiver receiver;
    private final int index;
    private iShape oldShape;
    private iShape newShape;

    public BorderDecoratorCommand(ShapeReceiver receiver, int index) {
        this.receiver = receiver;
        this.index = index;
    }

    @Override
    public void execute() {
        oldShape = receiver.getShapeAt(index);
        newShape = receiver.toggleBorder(oldShape); // create a new shape with border toggled
        receiver.performReplaceAt(index, newShape);
    }

    @Override
    public void undo() {
        receiver.performReplaceAt(index, oldShape);
    }

    @Override
    public String getName() {
        return "ToggleBorder";
    }
}
