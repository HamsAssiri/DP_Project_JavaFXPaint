package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.iShape;
import paint.model.ShapeDecorator;
import paint.model.ShadowDecorator;

public class ShadowDecoratorCommand implements Command {
    private final FXMLDocumentController controller;
    private final int index;
    private iShape oldShape;
    private iShape newShape;

    public ShadowDecoratorCommand(FXMLDocumentController controller, int index) {
        this.controller = controller;
        this.index = index;
    }

    @Override
    public void execute() {
        oldShape = controller.getShapeAt(index);
        newShape = controller.toggleShadow(oldShape); // create a new shape with shadow toggled
        controller.performReplaceAt(index, newShape);
    }

    @Override
    public void undo() {
        controller.performReplaceAt(index, oldShape);
    }

    @Override
    public String getName() {
        return "ToggleShadow";
    }
}
