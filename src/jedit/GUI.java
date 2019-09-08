package jedit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author l
 */
public class GUI extends Application implements Runnable {

    private Stage primary;
    private TextArea text;
    private long textSize = 0;
    File mainFile;

    @Override
    public void run() {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {

        this.primary = stage;

        BorderPane root = new BorderPane();

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primary.widthProperty());
        root.setTop(menuBar);
        root.setStyle("-fx-background-color: #000000");

        Menu fileMenu = new Menu("Archivo");
        MenuItem newMenuItem = new MenuItem("Nuevo");
        MenuItem openMenuItem = new MenuItem("Abrir...");
        MenuItem saveMenuItem = new MenuItem("Guardar");
        MenuItem saveAsMenuItem = new MenuItem("Guardar como..");
        MenuItem exitMenuItem = new MenuItem("Salir");

        Menu editMenu = new Menu("Editar");
        MenuItem undoMenuItem = new MenuItem("Deshacer");
        MenuItem cutMenuItem = new MenuItem("Cortar");
        MenuItem copyMenuItem = new MenuItem("Copiar");
        MenuItem pasteMenuItem = new MenuItem("Pagar");
        MenuItem deleteMenuItem = new MenuItem("Borrar");
        MenuItem selectAllMenuItem = new MenuItem("Seleccionar todo");

        Menu helpMenu = new Menu("Ayuda");
        MenuItem aboutMenuItem = new MenuItem("Acerca de");

        fileMenu.getItems().addAll(
                newMenuItem,
                openMenuItem,
                saveMenuItem,
                saveAsMenuItem,
                new SeparatorMenuItem(),
                exitMenuItem
        );
        fileMenu.setStyle("-fx-background-color: #1c1c1c; -fx-text-fill: #ffffff");

        editMenu.getItems().addAll(
                undoMenuItem, 
                new SeparatorMenuItem(),
                cutMenuItem, 
                copyMenuItem, 
                pasteMenuItem, 
                deleteMenuItem,
                new SeparatorMenuItem(), 
                selectAllMenuItem
        );
        editMenu.setStyle("-fx-background-color: #1c1c1c; -fx-text-fill: #ffffff");
        
        helpMenu.getItems().addAll(
                aboutMenuItem
        );
        helpMenu.setStyle("-fx-background-color: #1c1c1c; -fx-text-fill: #ffffff");

        menuBar.getMenus().addAll(
                fileMenu, 
                editMenu, 
                helpMenu
        );
        menuBar.setStyle("-fx-background-color: #1c1c1c; -fx-text-fill: #ffffff");

        text = new TextArea();
        text.setPrefRowCount(10);
        text.setPrefColumnCount(100);
        text.setWrapText(true);
        text.setPrefWidth(150);
        text.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff");
        
        undoMenuItem.setOnAction(event -> {
            text.undo();
        });

        copyMenuItem.setOnAction(event -> {
            text.copy();
        });

        cutMenuItem.setOnAction(event -> {
            text.cut();
        });

        pasteMenuItem.setOnAction(event -> {
            text.paste();
        });

        selectAllMenuItem.setOnAction(event -> {
            text.selectAll();
        });

        deleteMenuItem.setOnAction(event -> {
            IndexRange selection = text.getSelection();
            text.deleteText(selection);
        });

        aboutMenuItem.setOnAction(event -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Acerca de JEdit");
            alert.setHeaderText("JEdit");
            alert.setContentText("JEdit V1 \nElaborado con JavaFX");

            alert.showAndWait();
        });

        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        newMenuItem.setOnAction((event) -> {

            if (!checkTextStatus()) {
                return;
            }

            text.setText("");
            primary.setTitle("JEdit");
            textSize = 0;
            mainFile = null;
        });

        saveMenuItem.setOnAction((event) -> {

            save();

        });

        saveAsMenuItem.setOnAction((event) -> {

            saveAs();

        });

        openMenuItem.setOnAction((event) -> {

            if (!checkTextStatus()) {
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir archivo");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt")
            );

            File file = fileChooser.showOpenDialog(primary);

            if (file == null) {
                return;
            }

            mainFile = file;

            primary.setTitle("JEdit | " + file.getName());
            try ( BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                text.setText(everything);
                textSize = text.getText().length();
                text.positionCaret((int) textSize);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        root.setCenter(text);

        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));

        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        cutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        pasteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        selectAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        Scene scene = new Scene(root, 800, 550);

        primary.setOnCloseRequest((WindowEvent we) -> {
            if (!checkTextStatus())
            {
                we.consume();
            }
        });
        primary.setTitle("JEdit");
        primary.setScene(scene);
        primary.initStyle(StageStyle.TRANSPARENT);
        primary.show();
        
        Region region = ( Region ) text.lookup( ".content" );
        region.setStyle("-fx-background-color: #353535");

        
    }
    
    private void save() {
        if (mainFile == null) {
            saveAs();
        } else {
            saveFile(mainFile);
        }
    }

    private void saveAs() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setTitle("Guardar archivo");

        File file = fileChooser.showSaveDialog(primary);

        if (file == null) {
            return;
        }
        mainFile = file;

        saveFile(file);
    }


    private void saveFile(File file) {
        primary.setTitle("JEdit | " + file.getName());

        textSize = text.getText().length();

        try ( Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {

            String[] split = text.getText().split("\n");
            for (String string : split) {
                writer.append(string);
                writer.append(System.lineSeparator());
            }

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    private boolean checkTextStatus() {
        if (textSize != text.getText().length()) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            alert.setTitle("JEdit");
            alert.setHeaderText("Desea guardar los cambios ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
                save();

            } else if (result.get() == ButtonType.CANCEL) {
                return false;
            }

        }
        return true;
    }

}
