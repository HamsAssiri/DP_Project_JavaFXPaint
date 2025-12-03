package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.iShape;

public class DeleteShapeCommand implements Command {
    private final FXMLDocumentController controller;
    private final iShape shape;
    private final int index;

    public DeleteShapeCommand(FXMLDocumentController controller, iShape shape, int index) {
        this.controller = controller;
        this.shape = shape;
        this.index = index;
    }

    @Override
    public void execute() {
        controller.performRemoveAt(index);
    }

    @Override
    public void undo() {
        controller.performAddAt(index, shape);
    }

    @Override
    public String getName() {
        return "DeleteShape";
    }
}