package paint.command;

import paint.controller.FXMLDocumentController;
import paint.model.iShape;
import paint.model.ShapeGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupShapesCommand implements Command {
    private final FXMLDocumentController controller;
    private final List<Integer> indices;
    private int insertIndex;
    private ShapeGroup group;

    public GroupShapesCommand(FXMLDocumentController controller, List<Integer> indices) {
        this.controller = controller;
        this.indices = new ArrayList<>(indices);
    }

    @Override
    public void execute() {
         group = controller.groupShapesAtIndices(indices);
         insertIndex = indices.get(indices.size() - 1);
    }

    @Override
    public void undo() {
        if (group != null) {
            controller.ungroupShape(insertIndex); // restore original shapes
        }
    }

    @Override
    public String getName() {
        return "GroupShapes";
    }
}
