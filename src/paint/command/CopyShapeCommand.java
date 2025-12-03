package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.iShape;

public class CopyShapeCommand implements Command {
    private final FXMLDocumentController controller;
    private final iShape copiedShape;
    private final int insertIndex;

    public CopyShapeCommand(FXMLDocumentController controller, iShape copiedShape, int insertIndex) {
        this.controller = controller;
        this.copiedShape = copiedShape;
        this.insertIndex = insertIndex;
    }

    @Override
    public void execute() {
        controller.performAddAt(insertIndex, copiedShape);
    }

    @Override
    public void undo() {
        controller.performRemoveAt(insertIndex);
    }

    @Override
    public String getName() {
        return "CopyShape";
    }
}