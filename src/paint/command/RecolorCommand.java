package paint.command;

import javafx.scene.paint.Color;
import paint.controller.FXMLDocumentController;
import paint.model.iShape;

public class RecolorCommand implements Command {
    private final FXMLDocumentController controller;
    private final int index;
    private final Color oldColor;
    private final Color newColor;

    public RecolorCommand(FXMLDocumentController controller, int index, Color oldColor, Color newColor) {
        this.controller = controller;
        this.index = index;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    @Override
    public void execute() {
        controller.performSetFillColorAt(index, newColor);
    }

    @Override
    public void undo() {
        controller.performSetFillColorAt(index, oldColor);
    }

    @Override
    public String getName() {
        return "RecolorShape";
    }
}