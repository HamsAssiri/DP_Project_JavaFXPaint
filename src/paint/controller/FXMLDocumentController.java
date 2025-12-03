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

import paint.command.*;
import paint.model.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import javafx.scene.control.SelectionMode; // For composite
import javafx.scene.control.TextInputDialog;// for Memento
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

    @FXML
     private ToggleButton GroupToggle;

    @FXML
    private Label Message;
    @FXML
    private ListView<String> ShapeList; // Added generic type
    
    // For Memento DP
    @FXML private ComboBox<String> VersionComboBox;
    @FXML private Button SaveVersionBtn;
    @FXML private Button RestoreVersionBtn;
    @FXML private Button DeleteVersionBtn;
    @FXML private Label VersionCountLabel;


    /**
     * *CLASS VARIABLES**
     */
    private Point2D start;
    private Point2D end;

    //SINGLETON DP - Canvas Manager
    private CanvasManager canvasManager;

    //Command Manager 
    CommandManager cmdManager = new CommandManager();


    //Shape list management - Use iShape consistently
    private ArrayList<iShape> shapeList = new ArrayList<>(); //object creation of group
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
                iShape selected = shapeList.get(index);
                Command deleteCmd = new DeleteShapeCommand(this, selected, index);
                cmdManager.executeCommand(deleteCmd);
            } else {
                Message.setText("You need to pick a shape first to delete it.");
            }
        }

        if (event.getSource() == RecolorBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                Color oldColor = shapeList.get(index).getFillColor();
                Color newColor = ColorBox.getValue();
                Command recolorCmd = new RecolorCommand(this, index, oldColor, newColor);
                cmdManager.executeCommand(recolorCmd);
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
            if (!cmdManager.canUndo()) {
                Message.setText("We are back to zero point! .. Can Undo nothing more!");
                return;
            }
            cmdManager.undo();
        }

        if (event.getSource() == RedoBtn) {
            if (!cmdManager.canRedo()) {
                Message.setText("There is no more history for me to get .. Go search history books.");
                return;
            }
            cmdManager.redo();
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
    }

    public void showPathPane() {
        Message.setVisible(false);
        PathPane.setVisible(true);
    }

    public void hidePathPane() {
        PathPane.setVisible(false);
        Message.setVisible(true);
    }

    // COMPOSITE DP - Grouping and Ungrouping

@FXML
public void onGroupToggle(ActionEvent e) {
    if (GroupToggle.isSelected()) {
        //var selected = ShapeList.getSelectionModel().getSelectedIndices();
        ObservableList<Integer> selected = ShapeList.getSelectionModel().getSelectedIndices();
        if (selected.size() >= 2) {
            Command groupCmd = new GroupShapesCommand(this, selected);
            cmdManager.executeCommand(groupCmd);
            Message.setText("Grouped selected shapes");
         
        } else {
            Message.setText("Select 2+ shapes to group");
            GroupToggle.setSelected(false);
        }
    } else {
        int idx = getSelectedGroupIndex();
        if (idx >= 0) {
            Command ungroupCmd = new UngroupShapesCommand(this, idx);
            cmdManager.executeCommand(ungroupCmd);
            Message.setText("Ungrouped successfully");
        } else {
            Message.setText("Select a group to ungroup");
        }
    }
}

public int getSelectedGroupIndex() {
    //var idxs = ShapeList.getSelectionModel().getSelectedIndices();
    ObservableList<Integer> idxs = ShapeList.getSelectionModel().getSelectedIndices();
    for (int i : idxs) {
        if (i >= 0 && i < shapeList.size() && shapeList.get(i) instanceof ShapeGroup) return i;
    }
    int single = ShapeList.getSelectionModel().getSelectedIndex();
    if (single >= 0 && single < shapeList.size() && shapeList.get(single) instanceof ShapeGroup) return single;
    return -1;
}

// COMPOSITE DP - initialize grouping and ungrouping
public void createGroup() {
    //var indices = new ArrayList<>(ShapeList.getSelectionModel().getSelectedIndices());
    ArrayList<Integer> indices = new ArrayList<>(ShapeList.getSelectionModel().getSelectedIndices());

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

// Returns the newly created ShapeGroup and its insertion index
public ShapeGroup groupShapesAtIndices(List<Integer> indices) {
    if (indices.size() < 2) return null;

    indices.sort((a, b) -> b - a); // sort descending
    List<iShape> selectedShapes = new ArrayList<>();
    for (int idx : indices) {
        selectedShapes.add(shapeList.get(idx));
        shapeList.remove(idx);
    }

    int insertIndex = indices.get(indices.size() - 1);
    ShapeGroup group = new ShapeGroup(selectedShapes);
    shapeList.add(insertIndex, group);

    canvasManager.refreshWithHistory(shapeList, primary);
    ShapeList.setItems(getStringList());

    return group; // return group for the command
}



public void ungroupShape(int groupIndex) {
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
            int index = ShapeList.getSelectionModel().getSelectedIndex();
            Point2D oldP = shapeList.get(index).getTopLeft();
            Point2D newP = start;
            Command moveCmd = new MoveShapeCommand(this, index, oldP, newP);
            cmdManager.executeCommand(moveCmd);
        } else if (copy) {
            copy = false;
            int index = ShapeList.getSelectionModel().getSelectedIndex();
            iShape copied = shapeList.get(index).clone();
            copied.setTopLeft(start);
            Command copyCmd = new CopyShapeCommand(this, copied, shapeList.size());
            cmdManager.executeCommand(copyCmd);
        } else if (resize) {
            resize = false;
            int index = ShapeList.getSelectionModel().getSelectedIndex();
            iShape selected = shapeList.get(index);
            iShape core = unwrap(selected);
            if (core instanceof ShapeGroup) {
                Message.setText("Resize not supported for grouped shapes. Please ungroup first.");
                return;
            }
            Color oldColor = selected.getFillColor();
            start = selected.getTopLeft();
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
            if (wrapped.getType().equals("Line")) {
                Message.setText("Line doesn't support this command. Sorry :(");
                return;
            }
            iShape oldSnapshot = selected;
            iShape newSnapshot = wrapped;
            newSnapshot.setFillColor(oldColor);
            Command resizeCmd = new ResizeShapeCommand(this, index, oldSnapshot, newSnapshot);
            cmdManager.executeCommand(resizeCmd);
        }
    }

    public void dragFunction() {
        String type = ShapeBox.getValue();
        iShape sh;
        try {
            sh = shapeFactory.createShape(type, start, end, ColorBox.getValue());
        } catch (Exception e) {
            Message.setText("Don't be in a hurry! Choose a shape first :D");
            return;
        }
        Command addCmd = new AddShapeCommand(this, sh, shapeList.size());
        cmdManager.executeCommand(addCmd);
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

public iShape toggleBorder(iShape s) {
    return hasDecorator(s, BorderDecorator.class)
            ? removeDecorator(s, BorderDecorator.class)
            : new BorderDecorator(s);
}

public iShape toggleShadow(iShape s) {
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
    Command borderCmd = new BorderDecoratorCommand(this, index);
    cmdManager.executeCommand(borderCmd);
    Message.setText("Toggled Border");
}

@FXML
private void handleShadowMenu(ActionEvent event) {
    if (ShapeList.getSelectionModel().isEmpty()) {
        Message.setText("You need to pick a shape first to toggle shadow.");
        return;
    }
    int index = ShapeList.getSelectionModel().getSelectedIndex();
    Command shadowCmd = new ShadowDecoratorCommand(this, index);
    cmdManager.executeCommand(shadowCmd);
    Message.setText("Toggled Shadow");
}


    private iShape unwrap(iShape s) {
    while (s instanceof ShapeDecorator) {
        s = ((ShapeDecorator) s).getDecoratedShape();
    }
    return s;
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
        


        
GroupToggle.selectedProperty().addListener((obs, oldV, isOn) -> {
    GroupToggle.setStyle(isOn
        ? "-fx-background-radius:0; -fx-background-color:#5EC2FF;"  
        : "-fx-background-radius:0; -fx-background-color:#a8d8ea;"  
    );
});

    // Initialize version combo box - MEMENTO DP
        refreshVersionComboBox();

        // Auto-save initial version
        saveCurrentVersion("Initial Version");
        refreshVersionComboBox();
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
    public iShape[] getShapes() {
        return shapeList.toArray(new iShape[0]);
    }

    @Override
    public void undo() {
        cmdManager.undo();
    }

    @Override
    public void redo() {
        cmdManager.redo();
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

    public void performRemoveAt(int index) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.remove(index);
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        }
    }

    public void performAddAt(int index, iShape shape) {
        shapeList.add(index, shape);
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }

    public void performSetTopLeftAt(int index, Point2D p) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.get(index).setTopLeft(p);
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        }
    }

    public void performSetFillColorAt(int index, Color c) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.get(index).setFillColor(c);
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        }
    }

    public void performReplaceAt(int index, iShape shape) {
        if (index >= 0 && index < shapeList.size()) {
            shapeList.set(index, shape);
            canvasManager.refreshWithHistory(shapeList, primary);
            ShapeList.setItems(getStringList());
        }
    }

    public iShape getShapeAt(int index) {
    if (index >= 0 && index < shapeList.size()) {
        return shapeList.get(index);
    }
    return null; // or throw exception if you prefer
    }

    public void restoreGroupAt(int index, ShapeGroup group) {
    if (index >= 0 && index <= shapeList.size()) {
        shapeList.add(index, group);
        canvasManager.refreshWithHistory(shapeList, primary);
        ShapeList.setItems(getStringList());
    }
}


   /* // Create a new version snapshot
   public CanvasVersionMemento createVersion(String name) {
    String versionName = (name == null || name.isEmpty())
            ? "Version " + (versionManager.size() + 1)
            : name;

    return new CanvasVersionMemento(shapeList, versionName);
}*/
    //Updated createVersion method using clone from shape class to save the states properly
    public CanvasVersionMemento createVersion(String name) {
        String versionName = (name == null || name.isEmpty())
                ? "Version " + (versionManager.size() + 1)
                : name;

        // Create deep copy of shapes
        List<iShape> clonedShapes = new ArrayList<>();
        for (iShape shape : shapeList) {
            try {
                clonedShapes.add(shape.clone());
            } catch (CloneNotSupportedException e) {
                System.err.println("Failed to clone shape for version: " + e.getMessage());
                // Fallback: add original to prevents crash
                clonedShapes.add(shape);
            }
        }

        return new CanvasVersionMemento(clonedShapes, versionName);
    }

    // Save current shapes as a version
    public void saveCurrentVersion(String name) {
    CanvasVersionMemento m = createVersion(name);
    versionManager.addVersion(m);
}

   // Restore version by index
    public void restoreVersionAt(int index) {
    CanvasVersionMemento m = versionManager.getVersion(index);
    if (m == null) return;

    // restore state
    this.shapeList = new ArrayList<>(m.getShapesSnapshot());

    // redraw the canvas
    canvasManager.redrawAll(shapeList);
    ShapeList.setItems(getStringList());
}

    //methods for Memento UI
    @FXML
    private void handleSaveVersionAction(ActionEvent event) {
        // Show dialog to get version name
        TextInputDialog dialog = new TextInputDialog("Version " + (versionManager.size() + 1));
        dialog.setTitle("Save Version");
        dialog.setHeaderText("Enter a name for this version:");
        dialog.setContentText("Version name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                saveCurrentVersion(name);
                refreshVersionComboBox();
                Message.setText("Version saved: " + name);
            }
        });
    }
    
    @FXML
    private void handleRestoreVersionAction(ActionEvent event) {
        int selectedIndex = VersionComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            restoreVersionAt(selectedIndex);
            Message.setText("Restored version: " + VersionComboBox.getValue());
        } else {
            Message.setText("Please select a version to restore");
        }
    }

    @FXML
    private void handleDeleteVersionAction(ActionEvent event) {
        int selectedIndex = VersionComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String versionName = VersionComboBox.getValue();
            // Remove from version manager
            if (selectedIndex < versionManager.size()) {
                // We need to add a remove method to VersionManager
                // For now, create a new list without the selected version
                List<CanvasVersionMemento> newVersions = new ArrayList<>();
                for (int i = 0; i < versionManager.size(); i++) {
                    if (i != selectedIndex) {
                        newVersions.add(versionManager.getVersion(i));
                    }
                }
                // Create new VersionManager (simplified approach)
                versionManager = new VersionManager();
                for (CanvasVersionMemento m : newVersions) {
                    versionManager.addVersion(m);
                }

                refreshVersionComboBox();
                Message.setText("Deleted version: " + versionName);
            }
        } else {
            Message.setText("Please select a version to delete");
        }
    }

    // Helper method to refresh the version combo box
    private void refreshVersionComboBox() {
        ObservableList<String> versionNames = FXCollections.observableArrayList();
        for (int i = 0; i < versionManager.size(); i++) {
            CanvasVersionMemento m = versionManager.getVersion(i);
            versionNames.add(m.getName());
        }
        VersionComboBox.setItems(versionNames);
        VersionCountLabel.setText("Versions: " + versionManager.size());
    }
    
}