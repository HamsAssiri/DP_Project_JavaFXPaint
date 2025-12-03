package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.ShapeGroup;
import paint.model.iShape;

import java.util.List;

public class UngroupShapesCommand implements Command {
    private final FXMLDocumentController controller;
    private final int groupIndex;
    private ShapeGroup groupBackup;

    public UngroupShapesCommand(FXMLDocumentController controller, int groupIndex) {
        this.controller = controller;
        this.groupIndex = groupIndex;
    }

    @Override
    public void execute() {
        iShape shape = controller.getShapeAt(groupIndex);
        if (shape instanceof ShapeGroup sg) {
            groupBackup = sg;
            controller.ungroupShape(groupIndex);
        }
    }

    @Override
    public void undo() {
        if (groupBackup != null) {
            controller.restoreGroupAt(groupIndex, groupBackup); // you may need to implement this helper
        }
    }

    @Override
    public String getName() {
        return "UngroupShapes";
    }
}
