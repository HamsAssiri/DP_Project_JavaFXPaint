package paint.command;

import javafx.geometry.Point2D;
import paint.controller.ShapeReceiver;

public class MoveShapeCommand implements Command {
    private final ShapeReceiver receiver;
    private final int index;
    private final Point2D oldPosition;
    private final Point2D newPosition;

    public MoveShapeCommand(ShapeReceiver receiver, int index, Point2D oldPosition, Point2D newPosition) {
        this.receiver = receiver;
        this.index = index;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    @Override
    public void execute() {
        receiver.performSetTopLeftAt(index, newPosition);
    }

    @Override
    public void undo() {
        receiver.performSetTopLeftAt(index, oldPosition);
    }

    @Override
    public String getName() {
        return "MoveShape";
    }
}