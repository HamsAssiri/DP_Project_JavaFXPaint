package paint.command;

import javafx.scene.paint.Color;
import paint.controller.ShapeReceiver;
import paint.model.iShape;

public class RecolorCommand implements Command {
    private final ShapeReceiver receiver;
    private final int index;
    private final Color oldColor;
    private final Color newColor;

    public RecolorCommand(ShapeReceiver receiver, int index, Color oldColor, Color newColor) {
        this.receiver = receiver;
        this.index = index;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    @Override
    public void execute() {
        receiver.performSetFillColorAt(index, newColor);
    }

    @Override
    public void undo() {
        receiver.performSetFillColorAt(index, oldColor);
    }

    @Override
    public String getName() {
        return "RecolorShape";
    }
}