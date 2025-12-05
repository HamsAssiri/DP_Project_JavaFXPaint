package paint.command;

import paint.controller.ShapeReceiver;
import paint.model.ShapeGroup;
import paint.model.iShape;

import java.util.List;

public class UngroupShapesCommand implements Command {
    private final ShapeReceiver receiver;
    private final int groupIndex;
    private ShapeGroup groupBackup;

    public UngroupShapesCommand(ShapeReceiver receiver, int groupIndex) {
        this.receiver = receiver;
        this.groupIndex = groupIndex;
    }

    @Override
    public void execute() {
        iShape shape = receiver.getShapeAt(groupIndex);
        if (shape instanceof ShapeGroup sg) {
            groupBackup = sg;
            receiver.ungroupShape(groupIndex);
        }
    }

    @Override
    public void undo() {
        if (groupBackup != null) {
            receiver.restoreGroupAt(groupIndex, groupBackup); // you may need to implement this helper
        }
    }

    @Override
    public String getName() {
        return "UngroupShapes";
    }
}