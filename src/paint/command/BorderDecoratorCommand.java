package paint.command;

import javafx.scene.paint.Color;
import paint.controller.FXMLDocumentController;
import paint.model.iShape;
import paint.model.ShapeDecorator;
import paint.model.BorderDecorator;

public class BorderDecoratorCommand implements Command {
    private final FXMLDocumentController controller;
    private final int index;
    private iShape oldShape;
    private iShape newShape;

    public BorderDecoratorCommand(FXMLDocumentController controller, int index) {
        this.controller = controller;
        this.index = index;
    }

    @Override
    public void execute() {
        oldShape = controller.getShapeAt(index);
        newShape = controller.toggleBorder(oldShape); // create a new shape with border toggled
        controller.performReplaceAt(index, newShape);
    }

    @Override
    public void undo() {
        controller.performReplaceAt(index, oldShape);
    }

    @Override
    public String getName() {
        return "ToggleBorder";
    }
}
