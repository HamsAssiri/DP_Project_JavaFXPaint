package paint.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import paint.model.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import javafx.scene.control.SelectionMode; // For composite
import java.util.List;       // مهم
import paint.controller.CanvasVersionMemento;
import paint.controller.VersionManager;




public class FXMLDocumentController implements Initializable, DrawingEngine {

    /**
     * *FXML VARIABLES**
     */
    @FXML
    private Button DeleteBtn;
    @FXML
    private ComboBox<String> ShapeBox;
    @FXML
    private Button UndoBtn;
    @FXML
    private Button RedoBtn;
    @FXML
    private ColorPicker ColorBox;
    @FXML
    private Button SaveBtn;
    @FXML
    private Button MoveBtn;
    @FXML
    private Button RecolorBtn;
    @FXML
    private Button LoadBtn;
    @FXML
    private GridPane After;
    @FXML
    private Pane Before;
    @FXML
    private Pane PathPane;
    @FXML
    private TextField PathText;
    @FXML
    private Button StartBtn;
    @FXML
    private Button ResizeBtn;
    @FXML
    private Button ImportBtn;
    @FXML
    private Button PathBtn;
    @FXML
    private Canvas CanvasBox;
    @FXML
    private Button CopyBtn;
  
    // Composite Buttons
    
    //private Button GroupBtn;
    
    //private Button UngroupBtn;

    @FXML
     private ToggleButton GroupToggle;

    @FXML
    private Label Message;
    @FXML
    private ListView<String> ShapeList; // Added generic type

    /**
     * *CLASS VARIABLES**
     */
    private Point2D start;
    private Point2D end;

    //SINGLETON DP - Canvas Manager
    private CanvasManager canvasManager;

    //Shape list management - Use iShape consistently
    private ArrayList<iShape> shapeList = new ArrayList<>();
    private IShapeFactory shapeFactory;

    private boolean move = false;
    private boolean copy = false;
    private boolean resize = false;
    private boolean save = false;
    private boolean load = false;
    private boolean importt = false;

    //MEMENTO DP - Fix the stack types to use iShape
    private Stack<ArrayList<iShape>> primary = new Stack<>();
    private Stack<ArrayList<iShape>> secondary = new Stack<>();

    private VersionManager versionManager = new VersionManager();



    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == StartBtn) {
            Before.setVisible(false);
            After.setVisible(true);
        }

        Message.setText("");
        if (event.getSource() == DeleteBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                removeShape(shapeList.get(index));
            } else {
                Message.setText("You need to pick a shape first to delete it.");
            }
        }

        if (event.getSource() == RecolorBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                shapeList.get(index).setFillColor(ColorBox.getValue());
                // Use CanvasManager singleton for refresh operations
                canvasManager.refreshWithHistory(shapeList, primary);
                ShapeList.setItems(getStringList());
            } else {
                Message.setText("You need to pick a shape first to recolor it.");
            }
        }

        if (event.getSource() == MoveBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                move = true;
                Message.setText("Click on the new top-left position below to move the selected shape.");
            } else {
                Message.setText("You need to pick a shape first to move it.");
            }
        }

        if (event.getSource() == CopyBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                copy = true;
                Message.setText("Click on the new top-left position below to copy the selected shape.");
            } else {
                Message.setText("You need to pick a shape first to copy it.");
            }
        }

        if (event.getSource() == ResizeBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                resize = true;
                Message.setText("Click on the new right-button position below to resize the selected shape.");
            } else {
                Message.setText("You need to pick a shape first to resize it.");
            }
        }

        if (event.getSource() == UndoBtn) {
            if (primary.empty()) {
                Message.setText("We are back to zero point! .. Can Undo nothing more!");
                return;
            }
            undo();
        }

        if (event.getSource() == RedoBtn) {
            if (secondary.empty()) {
                Message.setText("There is no more history for me to get .. Go search history books.");
                return;
            }
            redo();
        }

        if (event.getSource() == SaveBtn) {
            showPathPane();
            save = true;
        }

        if (event.getSource() == LoadBtn) {
            showPathPane();
            load = true;
        }

        if (event.getSource() == ImportBtn) {
            showPathPane();
            importt = true;
        }

        if (event.getSource() == PathBtn) {
            if (PathText.getText().isEmpty()) {
                PathText.setText("You need to set the path of the file.");
                return;
            }
            if (save) {
                save = false;
                save(PathText.getText());
            } else if (load) {
                load = false;
                load(PathText.getText());
            } else if (importt) {
                importt = false;
                installPluginShape(PathText.getText());
            }
            hidePathPane();
        }


        
        /*  COMPOSITE GROUP FUNCTIONALITY
        if (event.getSource() == GroupBtn) {
            if (ShapeList.getSelectionModel().getSelectedItems().size() > 1) {
                createGroup();
            } else {
                Message.setText("Select multiple shapes to group");
            }
        }

        if (event.getSource() == UngroupBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                if (shapeList.get(index) instanceof ShapeGroup) {
                    ungroupShape(index);
                } else {
                    Message.setText("Select a group to ungroup");
                }
            }
        }
        // END COMPOSITE
        */

        
    }

    public void showPathPane() {
        Message.setVisible(false);
        PathPane.setVisible(true);
    }

    public void hidePathPane() {
        PathPane.setVisible(false);
        Message.setVisible(true);
    }

@FXML
private void onGroupToggle(ActionEvent e) {
    if (GroupToggle.isSelected()) {
        var selected = ShapeList.getSelectionModel().getSelectedIndices();
        if (selected.size() >= 2) {
            createGroup();
            Message.setText("Grouped selected shapes");
         
        } else {
            Message.setText("Select 2+ shapes to group");
            GroupToggle.setSelected(false);
        }
    } else {
        int idx = getSelectedGroupIndex();
        if (idx >= 0) {
            ungroupShape(idx);
            Message.setText("Ungrouped successfully");
        } else {
            Message.setText("Select a group to ungroup");
        }
    }
}

private int getSelectedGroupIndex() {
    var idxs = ShapeList.getSelectionModel().getSelectedIndices();
    for (int i : idxs) {
        if (i >= 0 && i < shapeList.size() && shapeList.get(i) instanceof ShapeGroup) return i;
    }
    int single = ShapeList.getSelectionModel().getSelectedIndex();
    if (single >= 0 && single < shapeList.size() && shapeList.get(single) instanceof ShapeGroup) return single;
    return -1;
}


private void createGroup() {
    var indices = new ArrayList<>(ShapeList.getSelectionModel().getSelectedIndices());
    if (indices.size() < 2) {
        Message.setText("Select 2+ shapes to group");
        return;
    }

   
    indices.sort((a, b) -> b - a);

    List<iShape> selectedShapes = new ArrayList<>();
    for (int i = indices.size() - 1; i >= 0; i--) {
        selectedShapes.add(shapeList.get(indices.get(i)));
    }

    
    int insertIndex = indices.get(indices.size() - 1);

    for (int idx : indices) shapeList.remove(idx);

    ShapeGroup group = new ShapeGroup(selectedShapes);
    shapeList.add(insertIndex, group);

   
    ShapeList.getSelectionModel().clearSelection();
    ShapeList.getSelectionModel().select(insertIndex);

    canvasManager.refreshWithHistory(shapeList, primary);
    ShapeList.setItems(getStringList());
    Message.setText("Shapes grouped successfully");
}


private void ungroupShape(int groupIndex) {
    ShapeGroup group = (ShapeGroup) shapeList.get(groupIndex);
    List<iShape> children = group.getShapes();

    shapeList.remove(groupIndex);
    shapeList.addAll(groupIndex, children);

   
    ShapeList.getSelectionModel().clearSelection();
    for (int i = 0; i < children.size(); i++) {
        ShapeList.getSelectionModel().select(groupIndex + i);
    }

    canvasManager.refreshWithHistory(shapeList, primary);
    ShapeList.setItems(getStringList());
    Message.setText("Group ungrouped successfully");
}


    
    /*  COMPOSITE METHODS1
    private void createGroup() {
        List<iShape> selectedShapes = new ArrayList<>();
        List<Integer> indices = new ArrayList<>(
        ShapeList.getSelectionModel().getSelectedIndices()
        );
    
    // Sort indices in descending order to avoid index shifting during removal
    indices.sort((a, b) -> b.compareTo(a));
    
    for (int index : indices) {
        selectedShapes.add(shapeList.get(index));
    }
    
    // Remove individual shapes and add group
    for (int index : indices) {
        shapeList.remove(index); // FIXED: Just use the primitive int directly
    }
    
    ShapeGroup group = new ShapeGroup(selectedShapes);
    shapeList.add(group);
    
    canvasManager.refreshWithHistory(shapeList, primary);
    ShapeList.setItems(getStringList());
    Message.setText("Shapes grouped successfully");
}


    private void ungroupShape(int groupIndex) {
        ShapeGroup group = (ShapeGroup) shapeList.get(groupIndex);
        List<iShape> individualShapes = group.getShapes();
        
        shapeList.remove(groupIndex);
        shapeList.addAll(individualShapes);
        
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
        Message.setText("Group ungrouped successfully");
    }

    */


    //decorator
    /* 
    @FXML
    private void handleShadowMenu(ActionEvent event) {
        if (!ShapeList.getSelectionModel().isEmpty()) {
            int index = ShapeList.getSelectionModel().getSelectedIndex();
            iShape s = shapeList.get(index);
            boolean has = false;
            iShape cur = s;
            while (cur instanceof ShapeDecorator) {
                if (cur instanceof ShadowDecorator) {
                    has = true;
                    break;
                }
                cur = ((ShapeDecorator) cur).getDecoratedShape();
            }
            if (!has) {
                shapeList.set(index, new ShadowDecorator(s));
            } else {
                shapeList.set(index, ShapeDecorator.removeDecoratorOfClass(s, ShadowDecorator.class));
            }
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        } else {
            Message.setText("You need to pick a shape first to toggle shadow.");
        }
    }
    */
    //decorator
    /* 
    @FXML
    private void handleBorderMenu(ActionEvent event) {
        if (!ShapeList.getSelectionModel().isEmpty()) {
            int index = ShapeList.getSelectionModel().getSelectedIndex();
            iShape s = shapeList.get(index);
            boolean has = false;
            iShape cur = s;
            while (cur instanceof ShapeDecorator) {
                if (cur instanceof BorderDecorator) {
                    has = true;
                    break;
                }
                cur = ((ShapeDecorator) cur).getDecoratedShape();
            }
            if (!has) {
                shapeList.set(index, new BorderDecorator(s, Color.BLACK, 3.0));
            } else {
                shapeList.set(index, ShapeDecorator.removeDecoratorOfClass(s, BorderDecorator.class));
            }
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        } else {
            Message.setText("You need to pick a shape first to toggle border.");
        }
    }*/

    public void startDrag(MouseEvent event) {
        start = new Point2D(event.getX(), event.getY());
        Message.setText("");
    }

    public void endDrag(MouseEvent event) throws CloneNotSupportedException {
        end = new Point2D(event.getX(), event.getY());
        if (end.equals(start)) {
            clickFunction();
        } else {
            dragFunction();
        }
    }

    public void clickFunction() throws CloneNotSupportedException {
        if (move) {
            move = false;
            moveFunction();
        } else if (copy) {
            copy = false;
            copyFunction();
        } else if (resize) {
            resize = false;
            resizeFunction();
        }
    }

    public void moveFunction() {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        shapeList.get(index).setTopLeft(start);
        // Use CanvasManager singleton for refresh operations
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    public void copyFunction() throws CloneNotSupportedException {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        iShape temp = shapeList.get(index).clone(); // Use clone() instead of cloneShape()
        if (temp == null) {
            System.out.println("Error cloning failed!");
        } else {
            shapeList.add(temp);
            shapeList.get(shapeList.size() - 1).setTopLeft(start);
            // Use CanvasManager singleton for refresh operations
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        }
    }

    public void resizeFunction() {
            int index = ShapeList.getSelectionModel().getSelectedIndex();
    if (index < 0) return;

    iShape selected = shapeList.get(index);
    iShape core = unwrap(selected);

    if (core instanceof ShapeGroup) {
        Message.setText("Resize not supported for grouped shapes. Please ungroup first.");
        return; 
    }

        Color c = shapeList.get(index).getFillColor();
        start = shapeList.get(index).getTopLeft();

        //Factory DP - Use the factory instance and iShape
        java.util.List<ShapeDecorator> decorators = new java.util.ArrayList<>();
        iShape cur = selected;
        while (cur instanceof ShapeDecorator) {
            decorators.add((ShapeDecorator) cur);
            cur = ((ShapeDecorator) cur).getDecoratedShape();
        }
        String type = core.getType();
        iShape newCore = shapeFactory.createShape(type, start, end, ColorBox.getValue());

        iShape wrapped = newCore;
        for (int i = decorators.size() - 1; i >= 0; --i) {
            ShapeDecorator d = decorators.get(i);
            try {
                if (d instanceof BorderDecorator) {
                    try {
                        Field fColor = BorderDecorator.class.getDeclaredField("borderColor");
                        Field fWidth = BorderDecorator.class.getDeclaredField("borderWidth");
                        fColor.setAccessible(true);
                        fWidth.setAccessible(true);
                        Color bc = (Color) fColor.get(d);
                        double bw = fWidth.getDouble(d);
                        wrapped = new BorderDecorator(wrapped, bc, bw);
                        continue;
                    } catch (Exception ex) {
                        // fallback to default BorderDecorator
                        wrapped = new BorderDecorator(wrapped);
                        continue;
                    }
                } else if (d instanceof ShadowDecorator) {
                    try {
                        Field fColor = ShadowDecorator.class.getDeclaredField("color");
                        Field fLayers = ShadowDecorator.class.getDeclaredField("layers");
                        Field fOffsetX = ShadowDecorator.class.getDeclaredField("offsetX");
                        Field fOffsetY = ShadowDecorator.class.getDeclaredField("offsetY");
                        Field fPadding = ShadowDecorator.class.getDeclaredField("padding");
                        fColor.setAccessible(true);
                        fLayers.setAccessible(true);
                        fOffsetX.setAccessible(true);
                        fOffsetY.setAccessible(true);
                        fPadding.setAccessible(true);
                        Color sc = (Color) fColor.get(d);
                        int layers = fLayers.getInt(d);
                        double ox = fOffsetX.getDouble(d);
                        double oy = fOffsetY.getDouble(d);
                        double pad = fPadding.getDouble(d);
                        ShadowDecorator sd = new ShadowDecorator(wrapped, sc, layers, ox, oy);
                        sd.setPadding(pad);
                        wrapped = sd;
                        continue;
                    } catch (Exception ex) {
                        wrapped = new ShadowDecorator(wrapped);
                        continue;
                    }
                }
                try {
                    Constructor<? extends ShapeDecorator> cons = d.getClass().getDeclaredConstructor(iShape.class);
                    cons.setAccessible(true);
                    wrapped = cons.newInstance(wrapped);
                } catch (Exception ex) {
                }
            } catch (Exception ex) {
            }
        }
        iShape temp = wrapped;
        if (temp.getType().equals("Line")) {
            Message.setText("Line doesn't support this command. Sorry :(");
            return;
        }
        shapeList.remove(index);
        temp.setFillColor(c);
        shapeList.add(index, temp);
        // Use CanvasManager singleton for refresh operations
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    

    public void dragFunction() {
        String type = ShapeBox.getValue();
        iShape sh;

        // Use factory interface
        try {
            sh = shapeFactory.createShape(type, start, end, ColorBox.getValue());
        } catch (Exception e) {
            Message.setText("Don't be in a hurry! Choose a shape first :D");
            return;
        }

        addShape(sh);
        canvasManager.drawShape(sh);
    }

    //Observer DP
    // COMPOSITE UPDATE -  getStringList to handle groups
    // ===== Helpers for decorators =====
private boolean hasDecorator(iShape s, Class<? extends ShapeDecorator> clazz) {
    iShape cur = s;
    while (cur instanceof ShapeDecorator) {
        if (clazz.isInstance(cur)) return true;
        cur = ((ShapeDecorator) cur).getDecoratedShape();
    }
    return false;
}

private iShape removeDecorator(iShape s, Class<? extends ShapeDecorator> clazz) {
    return ShapeDecorator.removeDecoratorOfClass(s, clazz);
}

private iShape toggleBorder(iShape s) {
    return hasDecorator(s, BorderDecorator.class)
            ? removeDecorator(s, BorderDecorator.class)
            : new BorderDecorator(s, Color.BLACK, 3.0);
}

private iShape toggleShadow(iShape s) {
    return hasDecorator(s, ShadowDecorator.class)
            ? removeDecorator(s, ShadowDecorator.class)
            : new ShadowDecorator(s);
}

@FXML
private void handleBorderMenu(ActionEvent event) {
    if (ShapeList.getSelectionModel().isEmpty()) {
        Message.setText("You need to pick a shape first to toggle border.");
        return;
    }
    int index = ShapeList.getSelectionModel().getSelectedIndex();
    iShape wrapped = shapeList.get(index);
    iShape core = unwrap(wrapped);

    if (core instanceof ShapeGroup g) {
       
        List<iShape> kids = new ArrayList<>();
        for (iShape child : g.getShapes()) {
            kids.add(toggleBorder(child));
        }
        shapeList.set(index, new ShapeGroup(kids));
        Message.setText("Toggled Border for group children");
    } else {
        shapeList.set(index, toggleBorder(wrapped));
        Message.setText("Toggled Border");
    }

    canvasManager.refreshWithHistory(shapeList, primary);
    ShapeList.setItems(getStringList());
}

@FXML
private void handleShadowMenu(ActionEvent event) {
    if (ShapeList.getSelectionModel().isEmpty()) {
        Message.setText("You need to pick a shape first to toggle shadow.");
        return;
    }
    int index = ShapeList.getSelectionModel().getSelectedIndex();
    iShape wrapped = shapeList.get(index);
    iShape core = unwrap(wrapped);

    if (core instanceof ShapeGroup g) {
        List<iShape> kids = new ArrayList<>();
        for (iShape child : g.getShapes()) {
            kids.add(toggleShadow(child));
        }
        shapeList.set(index, new ShapeGroup(kids));
        Message.setText("Toggled Shadow for group children");
    } else {
        shapeList.set(index, toggleShadow(wrapped));
        Message.setText("Toggled Shadow");
    }

    canvasManager.refreshWithHistory(shapeList, primary);
    ShapeList.setItems(getStringList());
}


    private iShape unwrap(iShape s) {
    while (s instanceof ShapeDecorator) {
        s = ((ShapeDecorator) s).getDecoratedShape();
    }
    return s;
    }

    private iShape rebuildWithSameDecorators(iShape original, iShape newCore) {
  
    java.util.List<ShapeDecorator> decorators = new java.util.ArrayList<>();
    iShape cur = original;
    while (cur instanceof ShapeDecorator) {
        decorators.add((ShapeDecorator) cur);
        cur = ((ShapeDecorator) cur).getDecoratedShape();
    }
    iShape wrapped = newCore;
    for (int i = decorators.size() - 1; i >= 0; --i) {
        ShapeDecorator d = decorators.get(i);
        try {
            if (d instanceof BorderDecorator) {
              
                try {
                    var fColor = BorderDecorator.class.getDeclaredField("borderColor");
                    var fWidth = BorderDecorator.class.getDeclaredField("borderWidth");
                    fColor.setAccessible(true); fWidth.setAccessible(true);
                    Color bc = (Color) fColor.get(d);
                    double bw = fWidth.getDouble(d);
                    wrapped = new BorderDecorator(wrapped, bc, bw);
                } catch (Exception ex) {
                    wrapped = new BorderDecorator(wrapped);
                }
                continue;
            }
            if (d instanceof ShadowDecorator) {
                try {
                    var fColor = ShadowDecorator.class.getDeclaredField("color");
                    var fLayers = ShadowDecorator.class.getDeclaredField("layers");
                    var fOffsetX = ShadowDecorator.class.getDeclaredField("offsetX");
                    var fOffsetY = ShadowDecorator.class.getDeclaredField("offsetY");
                    var fPadding = ShadowDecorator.class.getDeclaredField("padding");
                    fColor.setAccessible(true); fLayers.setAccessible(true);
                    fOffsetX.setAccessible(true); fOffsetY.setAccessible(true); fPadding.setAccessible(true);
                    Color sc = (Color) fColor.get(d);
                    int layers = fLayers.getInt(d);
                    double ox = fOffsetX.getDouble(d), oy = fOffsetY.getDouble(d), pad = fPadding.getDouble(d);
                    ShadowDecorator sd = new ShadowDecorator(wrapped, sc, layers, ox, oy);
                    sd.setPadding(pad);
                    wrapped = sd;
                } catch (Exception ex) {
                    wrapped = new ShadowDecorator(wrapped);
                }
                continue;
            }
            var cons = d.getClass().getDeclaredConstructor(iShape.class);
            cons.setAccessible(true);
            wrapped = cons.newInstance(wrapped);
        } catch (Exception ignore) {}
    }
    return wrapped;
}


public ObservableList<String> getStringList() {
    ObservableList<String> l = FXCollections.observableArrayList();
    try {
        int groupCount = 0;
        for (int i = 0; i < shapeList.size(); i++) {
            iShape wrapped = shapeList.get(i);
            iShape core = unwrap(wrapped);
            Point2D topLeft = core.getTopLeft();

            if (core instanceof ShapeGroup) {
                ShapeGroup g = (ShapeGroup) core;
                groupCount++;
                l.add("Group " + groupCount + " (" + g.getShapes().size() + " shapes) at (" +
                      (int) topLeft.getX() + "," + (int) topLeft.getY() + ")");
            } else {
                l.add(core.getType() + "  (" + (int) topLeft.getX() + "," + (int) topLeft.getY() + ")");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return l;
}


  

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
         // Enable multiple selection for ListView
        ShapeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Use factory interface
        shapeFactory = ShapeFactory.getInstance();

        // Get available shapes from factory
        ObservableList<String> availableShapes
                = FXCollections.observableArrayList(shapeFactory.getAvailableShapes());
        ShapeBox.setItems(availableShapes);

        ColorBox.setValue(Color.BLACK);

        canvasManager = CanvasManager.getInstance();
        canvasManager.initializeCanvas(CanvasBox);
        versionManager = new VersionManager();


        
GroupToggle.selectedProperty().addListener((obs, oldV, isOn) -> {
    GroupToggle.setStyle(isOn
        ? "-fx-background-radius:0; -fx-background-color:#5EC2FF;"  
        : "-fx-background-radius:0; -fx-background-color:#a8d8ea;"  
    );
});

    }

    @Override
    public void refresh(Object canvas) {
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    public void redraw(Canvas canvas) {
        canvasManager.redrawAll(shapeList);
    }

    @Override
    public void addShape(iShape shape) {
        shapeList.add(shape);
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    @Override
    public void removeShape(iShape shape) {
        shapeList.remove(shape);
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    @Override
    public void updateShape(iShape oldShape, iShape newShape) {
        shapeList.remove(oldShape);
        shapeList.add(newShape);
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    @Override
    public iShape[] getShapes() {
        return shapeList.toArray(new iShape[0]);
    }

    @Override
    public void undo() {
        if (secondary.size() < 21) {
            ArrayList<iShape> temp = primary.pop();
            secondary.push(temp);

            if (primary.empty()) {
                shapeList = new ArrayList<>();
            } else {
                temp = primary.peek();
                shapeList = new ArrayList<>(temp); // Create a new list to avoid reference issues
            }

            canvasManager.redrawAll(shapeList);
            ShapeList.setItems(getStringList());
        } else {
            Message.setText("Sorry, Cannot do more than 20 Undo's :'(");
        }
    }

    @Override
    public void redo() {
        ArrayList<iShape> temp = secondary.pop();
        primary.push(new ArrayList<>(temp)); // Create a copy

        shapeList = new ArrayList<>(temp); // Create a new list

        canvasManager.redrawAll(shapeList);
        ShapeList.setItems(getStringList());
    }

    @Override
    public void save(String path) {
        if (path.substring(path.length() - 4).equals(".xml")) {
            SaveToXML x = new SaveToXML(path, shapeList);
            if (x.checkSuccess()) {
                Message.setText("File Saved Successfully");
            } else {
                Message.setText("Error happened while saving, please check the path and try again!");
            }
        } else if (path.substring(path.length() - 5).equals(".json")) {
            Message.setText("Sorry, Json is not supported :(");
        } else {
            Message.setText("Wrong file format .. save to either .xml or .json");
        }
    }

    @Override
    public void load(String path) {
        if (path.substring(path.length() - 4).equals(".xml")) {
            try {
                LoadFromXML l = new LoadFromXML(path);
                if (l.checkSuccess()) {
                    shapeList = l.getList();
                    canvasManager.refreshWithHistory(shapeList, primary);
                    ShapeList.setItems(getStringList());
                    Message.setText("File loaded successfully");
                } else {
                    Message.setText("Error loading the file .. check the file path and try again!");
                }
            } catch (SAXException | ParserConfigurationException | IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                Message.setText("Error loading file: " + ex.getMessage());
            }
        } else if (path.substring(path.length() - 5).equals(".json")) {
            Message.setText("Sorry, Json is not supported :(");
        } else {
            Message.setText("Wrong file format .. load from either .xml or .json");
        }
    }

    @Override
    public List<Class<? extends iShape>> getSupportedShapes() {
        // Return actual supported shapes or leave as null for now
        return null;
    }

    @Override
    public void installPluginShape(String jarPath) {
        Message.setText("Not supported yet.");
    } 


    
    // Creates and saves a snapshot (version) of the current shapes list
    public void saveCurrentVersion(String name) {

    // Avoid saving empty canvas
    if (shapeList.isEmpty()) {
        System.out.println("saveCurrentVersion: No shapes to save.");
        return;
    }

    // Ensure the version manager exists
    if (versionManager == null) {
        versionManager = new VersionManager();
    }

    // Auto-generate version name if none provided
    String versionName = (name == null || name.isBlank())
            ? "Version " + (versionManager.size() + 1)
            : name;

    // Create snapshot and store it
    CanvasVersionMemento snapshot = new CanvasVersionMemento(shapeList, versionName);
    versionManager.addVersion(snapshot);

    System.out.println("Saved version: " + versionName +
                       " | Shapes count = " + shapeList.size());
    }


     // Restores a snapshot (version) from the stored versions list
     public void restoreVersionAt(int index) {

    // No versions saved yet
    if (versionManager == null || versionManager.size() == 0) {
        System.out.println("restoreVersionAt: No versions available.");
        return;
    }

    // Validate index
    CanvasVersionMemento snapshot = versionManager.getVersion(index);
    if (snapshot == null) {
        System.out.println("restoreVersionAt: Invalid version index -> " + index);
        return;
    }

    // Restore shapes state
    this.shapeList = new ArrayList<>(snapshot.getShapesSnapshot());

    // Redraw canvas and update UI
    canvasManager.redrawAll(shapeList);
    ShapeList.setItems(getStringList());

    System.out.println("Restored version: " + snapshot.getName() +
                       " | Shapes count = " + shapeList.size());
      }


 
}
