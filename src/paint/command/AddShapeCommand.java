package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.iShape;

public class AddShapeCommand implements Command {
    private final FXMLDocumentController controller;
    private final iShape shape;
    private final int insertIndex;

    public AddShapeCommand(FXMLDocumentController controller, iShape shape, int insertIndex) {
        this.controller = controller;
        this.shape = shape;
        this.insertIndex = insertIndex;
    }

    @Override
    public void execute() {
        controller.performAddAt(insertIndex, shape);
    }

    @Override
    public void undo() {
        controller.performRemoveAt(insertIndex);
    }

    @Override
    public String getName() {
        return "AddShape";
    }
}
