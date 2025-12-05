package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.iShape;
import paint.model.ShapeGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupShapesCommand implements Command {
    private final ShapeReceiver receiver;
    private final List<Integer> indices;
    private int insertIndex;
    private ShapeGroup group;

    public GroupShapesCommand(ShapeReceiver receiver, List<Integer> indices) {
        this.receiver = receiver;
        this.indices = new ArrayList<>(indices);
    }

    @Override
    public void execute() {
         group = receiver.groupShapesAtIndices(indices);
         insertIndex = indices.get(indices.size() - 1);
    }

    @Override
    public void undo() {
        if (group != null) {
            receiver.ungroupShape(insertIndex); // restore original shapes
        }
    }

    @Override
    public String getName() {
        return "GroupShapes";
    }
}
