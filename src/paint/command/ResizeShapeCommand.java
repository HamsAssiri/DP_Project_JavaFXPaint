package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.iShape;

public class ResizeShapeCommand implements Command {
    private final FXMLDocumentController controller;
    private final int index;
    private final iShape oldShape;
    private final iShape newShape;

    public ResizeShapeCommand(FXMLDocumentController controller, int index, iShape oldShape, iShape newShape) {
        this.controller = controller;
        this.index = index;
        this.oldShape = oldShape;
        this.newShape = newShape;
    }

    @Override
    public void execute() {
        controller.performReplaceAt(index, newShape);
    }

    @Override
    public void undo() {
        controller.performReplaceAt(index, oldShape);
    }

    @Override
    public String getName() {
        return "ResizeShape";
    }
}
