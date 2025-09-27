package paint.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import paint.model.*;
import paint.view.CanvasManager;

public class FXMLDocumentController implements Initializable, DrawingEngine {

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
    private Button CopyBtn;
    @FXML
    private Label Message;
    @FXML
    private ListView<String> ShapeList;
    @FXML
    //Singleton Canvas
    private Canvas CanvasBox;

    private Point2D start;
    private Point2D end;

    private ArrayList<Shape> shapeList = new ArrayList<>();
    
    private boolean move = false;
    private boolean copy = false;
    private boolean resize = false;
    private boolean save = false;
    private boolean load = false;
    private boolean importt = false;
    
    private Stack<ArrayList<Shape>> primary = new Stack<>();
    private Stack<ArrayList<Shape>> secondary = new Stack<>();

    @FXML
    private void handleButtonAction(ActionEvent event) throws CloneNotSupportedException {
        Message.setText("");
        if (event.getSource() == StartBtn) {
            Before.setVisible(false);
            After.setVisible(true);
            After.toFront();
            return;
        }

        if (event.getSource() == DeleteBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                removeShape(shapeList.get(ShapeList.getSelectionModel().getSelectedIndex()));
            } else {
                Message.setText("Pick a shape first to delete it.");
            }
        }

        if (event.getSource() == RecolorBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                shapeList.get(index).setFillColor(ColorBox.getValue());
                refresh(CanvasBox);
            } else {
                Message.setText("Pick a shape first to recolor it.");
            }
        }

        if (event.getSource() == MoveBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                move = true;
                Message.setText("Click new position to move.");
            } else {
                Message.setText("Pick a shape first to move it.");
            }
        }

        if (event.getSource() == CopyBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                copy = true;
                Message.setText("Click new position to copy.");
            } else {
                Message.setText("Pick a shape first to copy it.");
            }
        }

        if (event.getSource() == ResizeBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                resize = true;
                Message.setText("Click new position to resize.");
            } else {
                Message.setText("Pick a shape first to resize it.");
            }
        }

        if (event.getSource() == UndoBtn) {
            if (!primary.empty()) {
                undo();
            } else {
                Message.setText("Cannot undo.");
            }
        }
        if (event.getSource() == RedoBtn) {
            if (!secondary.empty()) {
                redo();
            } else {
                Message.setText("Cannot redo.");
            }
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
                PathText.setText("Set the file path.");
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

    private void clickFunction() throws CloneNotSupportedException {
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

    private void moveFunction() {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        shapeList.get(index).setTopLeft(start);
        refresh(CanvasBox);
    }

    private void copyFunction() throws CloneNotSupportedException {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        Shape temp = shapeList.get(index).cloneShape();
        shapeList.add(temp);
        shapeList.get(shapeList.size() - 1).setTopLeft(start);
        refresh(CanvasBox);
    }

    private void resizeFunction() {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        Color c = shapeList.get(index).getFillColor();
        start = shapeList.get(index).getTopLeft();
        Shape temp = new ShapeFactory().createShape(shapeList.get(index).getClass().getSimpleName(), start, end, ColorBox.getValue());
        if (temp.getClass().getSimpleName().equals("Line")) {
            Message.setText("Line cannot be resized.");
            return;
        }
        shapeList.remove(index);
        temp.setFillColor(c);
        shapeList.add(index, temp);
        refresh(CanvasBox);
    }

    private void dragFunction() {
        String type = ShapeBox.getValue();
        Shape sh;
        try {
            sh = new ShapeFactory().createShape(type, start, end, ColorBox.getValue());
        } catch (Exception e) {
            Message.setText("Choose a shape first.");
            return;
        }
        addShape(sh);
        sh.draw(CanvasBox);
    }

    public ObservableList<String> getStringList() {
        ObservableList<String> l = FXCollections.observableArrayList();
        for (Shape s : shapeList) {
            l.add(s.getClass().getSimpleName() + " (" + (int) s.getTopLeft().getX() + "," + (int) s.getTopLeft().getY() + ")");
        }
        return l;
    }

    public ArrayList<Shape> cloneList(ArrayList<Shape> l) throws CloneNotSupportedException {
        ArrayList<Shape> temp = new ArrayList<>();
        for (Shape s : l) {
            temp.add(s.cloneShape());
        }
        return temp;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ShapeBox.setItems(FXCollections.observableArrayList("Circle", "Ellipse", "Rectangle", "Square", "Triangle", "Line"));
        ColorBox.setValue(Color.BLACK);
        redraw(CanvasBox);
    }

    @Override
    public void refresh(Object canvas) {
        try {
            primary.push(new ArrayList<>(cloneList(shapeList)));
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        redraw((Canvas) canvas);
        ShapeList.setItems(getStringList());
    }

    public void redraw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape s : shapeList) {
            s.draw(canvas);
        }
    }

    @Override
    public void addShape(Shape shape) {
        shapeList.add(shape);
        refresh(CanvasBox);
    }

    @Override
    public void removeShape(Shape shape) {
        shapeList.remove(shape);
        refresh(CanvasBox);
    }

    @Override
    public void updateShape(Shape oldShape, Shape newShape) {
        shapeList.remove(oldShape);
        shapeList.add(newShape);
        refresh(CanvasBox);
    }

    @Override
    public Shape[] getShapes() {
        return shapeList.toArray(new Shape[0]);
    }

    @Override
    public void undo() {
        if (!primary.isEmpty()) {
            ArrayList<Shape> temp = primary.pop();
            secondary.push(temp);
            shapeList = primary.isEmpty() ? new ArrayList<>() : primary.peek();
            redraw(CanvasBox);
            ShapeList.setItems(getStringList());
        }
    }

    @Override
    public void redo() {
        if (!secondary.isEmpty()) {
            ArrayList<Shape> temp = secondary.pop();
            primary.push(temp);
            shapeList = primary.peek();
            redraw(CanvasBox);
            ShapeList.setItems(getStringList());
        }
    }

    @Override
    public void save(String path) {
        if (path.endsWith(".xml")) {
            SaveToXML x = new SaveToXML(path, shapeList);
            Message.setText(x.checkSuccess() ? "File Saved Successfully" : "Error saving file");
        } else {
            Message.setText("Save format must be .xml");
        }
    }

    @Override
    public void load(String path) {
        if (path.endsWith(".xml")) {
            try {
                LoadFromXML l = new LoadFromXML(path);
                if (l.checkSuccess()) {
                    shapeList = l.getList();
                    refresh(CanvasBox);
                    Message.setText("File loaded successfully");
                } else {
                    Message.setText("Error loading file");
                }
            } catch (Exception e) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            Message.setText("Load format must be .xml");
        }
    }

    @Override
    public java.util.List<Class<? extends Shape>> getSupportedShapes() {
        return null;
    }

    @Override
    public void installPluginShape(String jarPath) {
        Message.setText("Not supported yet.");
    }

}
