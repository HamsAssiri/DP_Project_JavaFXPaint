package paint.command;

import javafx.geometry.Point2D;
import paint.controller.FXMLDocumentController;

public class MoveShapeCommand implements Command {
    private final FXMLDocumentController controller;
    private final int index;
    private final Point2D oldPosition;
    private final Point2D newPosition;

    public MoveShapeCommand(FXMLDocumentController controller, int index, Point2D oldPosition, Point2D newPosition) {
        this.controller = controller;
        this.index = index;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    @Override
    public void execute() {
        controller.performSetTopLeftAt(index, newPosition);
    }

    @Override
    public void undo() {
        controller.performSetTopLeftAt(index, oldPosition);
    }

    @Override
    public String getName() {
        return "MoveShape";
    }
}
