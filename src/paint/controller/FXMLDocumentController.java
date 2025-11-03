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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import paint.model.*;
import paint.model.iShape;

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
    }

    public void showPathPane() {
        Message.setVisible(false);
        PathPane.setVisible(true);
    }

    public void hidePathPane() {
        PathPane.setVisible(false);
        Message.setVisible(true);
    }

    //decorator
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
    //decorator
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
        Color c = shapeList.get(index).getFillColor();
        start = shapeList.get(index).getTopLeft();

        //Factory DP - Use the factory instance and iShape
        iShape temp = shapeFactory.createShape(shapeList.get(index).getClass().getSimpleName(), start, end, ColorBox.getValue());
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
    public ObservableList<String> getStringList() {
        ObservableList<String> l = FXCollections.observableArrayList();
        try {
            for (int i = 0; i < shapeList.size(); i++) {
                String temp = shapeList.get(i).getType() + "  ("
                        + (int) shapeList.get(i).getTopLeft().getX() + ","
                        + (int) shapeList.get(i).getTopLeft().getY() + ")";
                l.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Use factory interface
        shapeFactory = ShapeFactory.getInstance();

        // Get available shapes from factory
        ObservableList<String> availableShapes
                = FXCollections.observableArrayList(shapeFactory.getAvailableShapes());
        ShapeBox.setItems(availableShapes);

        ColorBox.setValue(Color.BLACK);

        canvasManager = CanvasManager.getInstance();
        canvasManager.initializeCanvas(CanvasBox);
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
}
