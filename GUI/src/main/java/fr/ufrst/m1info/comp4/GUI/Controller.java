package fr.ufrst.m1info.comp4.GUI;

import fr.ufrst.m1info.comp4.compiler.CompilerMJJ;
import fr.ufrst.m1info.comp4.interpreter.debugger.Debugger;
import fr.ufrst.m1info.comp4.interpreter.jajacode.InterpreterJJC;
import fr.ufrst.m1info.comp4.interpreter.minijaja.InterpreterMJJ;

import fr.ufrst.m1info.comp4.memory.Memory;
import fr.ufrst.m1info.comp4.memory.SymbolTable;
import fr.ufrst.m1info.comp4.parser.jajacode.Jajacode;
import fr.ufrst.m1info.comp4.parser.jajacode.VisitorJJCException;
import fr.ufrst.m1info.comp4.parser.minijaja.Minijaja;
import fr.ufrst.m1info.comp4.parser.minijaja.ParseException;
import fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode;
import fr.ufrst.m1info.comp4.parser.minijaja.VisitorMJJException;
import fr.ufrst.m1info.comp4.typeChecker.ErrorType;
import fr.ufrst.m1info.comp4.typeChecker.TypeChecker;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.flowless.VirtualizedScrollPane;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Controller {
    private Stage stage;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label lineNoLabel, colNoLabel, selectTextLabel,debugLabel;
    @FXML
    private HBox statusBar;
    @FXML
    HBox buttonsDebug;
    private VirtualizedScrollPane virtualizedScrollPane;
    @FXML
    private CodeArea outputJajacode, compileJajacode, outputMinijaja, outputStack, outputHeap;

    @FXML
    private TextField findInput, replaceInput;
    @FXML
    private Label currIndexLabel, totalIndexLabel;
    @FXML
    private HBox findBox, replaceBox, containerFind;
    @FXML
    private ToolBar toolbarDebug;
    ArrayList<ArrayList<Integer>> coordinateList = new ArrayList<>();
    AtomicInteger currWordIndex = new AtomicInteger(0);

    private Memory  memory= new Memory();
    private Memory lastMemoryJJC = new Memory();
    private InterpreterMJJ interpreterMJJ = new InterpreterMJJ(null);
    private InterpreterJJC interpreterJJC= new InterpreterJJC(null);
    private final CompilerMJJ compiler = new CompilerMJJ(null);
    private final Debugger debugger = new Debugger();
    private SymbolTable symbolTableJJC = new SymbolTable();
    private List<CodeAreaWithBreakPoints> codeAreas = new ArrayList<>();

    private void setupStatusBar(CodeArea codeArea) {

        codeArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            int currentLine = codeArea.getCurrentParagraph() + 1;
            int currentColumn = codeArea.getCaretColumn() + 1;
            int selectionStart = codeArea.getSelection().getStart();
            int selectionEnd = codeArea.getSelection().getEnd();
            lineNoLabel.setText(String.valueOf(currentLine));
            colNoLabel.setText(String.valueOf(currentColumn));
            if (selectionStart == selectionEnd) {
                selectTextLabel.setText("");
            } else {
                selectTextLabel.setText(String.format("(%d selected)", selectionEnd - selectionStart));
            }
        });

        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();

                if (currentParagraph > 0) {
                    String previousParagraphText = codeArea.getParagraph(currentParagraph - 1).getText().trim();

                    // Check if the previous paragraph is not empty
                    if (!previousParagraphText.isEmpty()) {
                        char lastChar = previousParagraphText.charAt(previousParagraphText.length() - 1);

                        /* Check if the last character of the previous line is '{' */
                        if (lastChar == '{') {
                            codeArea.insertText(caretPosition, "\t");
                            codeArea.moveTo(caretPosition + 1);
                            e.consume();
                        }
                    }
                }
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        new SyntaxHighlighter().applySyntaxHighlightingOutput(outputMinijaja);
        new SyntaxHighlighter().applySyntaxHighlightingOutput(outputJajacode);

        restoreFileSession();
        this.stage.setOnCloseRequest(event -> closeWindow());
    }

    private void restoreFileSession() {
        // create a tab for each file in the session
        JSONParser parser = new JSONParser();
        try {
            String tmpDirPath = System.getProperty("java.io.tmpdir");
            String filePath = tmpDirPath + File.separator + "data.json";
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray files = (JSONArray) jsonObject.get("files");

            for (Object file : files) {
                JSONObject fileObj = (JSONObject) file;
                String path = (String) fileObj.get("path");
                String content = (String) fileObj.get("content");
                boolean unsaved = (boolean) fileObj.get("unsaved");
                boolean modified = (boolean) fileObj.get("modified");

                File tmpFile = createTempFile("hello");
                if (tmpFile == null){return ;}
                try (FileWriter fileWriter = new FileWriter(tmpFile)) {
                    fileWriter.write(content);
                } catch (IOException e) {
                    System.out.println("An error occurred during file saving.");
                    System.out.println(e.getMessage());
                }

                Tab newTab = new Tab(path);
                CodeAreaWithBreakPoints codeArea = new CodeAreaWithBreakPoints();
                setupStatusBar(codeArea.getCodeArea());
                loadFileContent(tmpFile, codeArea.getCodeArea());

                newTab.setContent(codeArea.getCodeArea());
                newTab.setOnCloseRequest(event -> onTabClose(newTab));
                tabPane.getTabs().add(newTab);

                setupTab(codeArea.getCodeArea(), newTab);

                codeArea.getCodeArea().moveTo(0, 0);
                codeArea.getCodeArea().requestFollowCaret();
                codeArea.getCodeArea().requestFocus();

                codeAreas.add(codeArea);

                if (unsaved){
                    newTab.getStyleClass().add("unsaved");
                    newTab.setText("Untitled*");
                }
                if (modified){
                    newTab.getStyleClass().add("modified");
                }
            }

            if (!tabPane.getTabs().isEmpty()){
                statusBar.setVisible(true);
            }
        } catch (Exception e) {
            System.out.println("An error occurred during file loading.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("/"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MiniJaja", "*.mjj")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            openFileInNewTab(selectedFile);
            tabPane.getSelectionModel().selectLast();
            statusBar.setVisible(true);
        }
    }
    private Tab getSelectedTab(){
        return tabPane.getSelectionModel().getSelectedItem();
    }


    private void openFileInNewTab(File file) {
        Tab newTab = new Tab(file.getAbsolutePath());
        CodeAreaWithBreakPoints codeArea = new CodeAreaWithBreakPoints();
        setupStatusBar(codeArea.getCodeArea());
        loadFileContent(file, codeArea.getCodeArea());

        newTab.setContent(codeArea.getCodeArea());
        newTab.setOnCloseRequest(event -> onTabClose(newTab));
        tabPane.getTabs().add(newTab);

        setupTab(codeArea.getCodeArea(), newTab);

        codeArea.getCodeArea().moveTo(0, 0);
        codeArea.getCodeArea().requestFollowCaret();
        codeArea.getCodeArea().requestFocus();

        codeAreas.add(codeArea);
    }

    public void closeSelectedTab(){
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            if (selectedTab.getStyleClass().contains("modified")){
                // Save modified file
                saveFile(null);
            }

            onTabClose(selectedTab);
            tabPane.getTabs().remove(selectedTab);
        }
    }
    private void onTabClose(Tab tab){
        CodeArea codeArea = (CodeArea) ((VirtualizedScrollPane) tab.getContent()).getContent();
        codeAreas.removeIf(codeAreaWithBreakPoints -> codeAreaWithBreakPoints.getCodeArea() == codeArea);
        if (tabPane.getTabs().size() <= 1){
            statusBar.setVisible(false);
        }
    }

    private void loadFileContent(File file, CodeArea codeArea) {
        try {
            String content = readFileContent(file);
            codeArea.replaceText(0, 0, content);
        } catch (IOException e) {
            System.out.println("Could not load file content");
            System.out.println(e.getMessage());
        }
    }

    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Scanner scanner = new Scanner(fileInputStream, "UTF-8");
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Could not read file content");
            System.out.println(e.getMessage());
        }

        return content.toString();
    }

    private File createTempFile(String prefix){
        try {

            // Create a temporary file
            Path temp = Files.createTempFile(prefix, ".tmp");
            //System.out.println("Temp file : " + temp);
            return new File(String.valueOf(temp));

        } catch (IOException e) {
            System.out.println("An error occurred during temp file creation.");
            System.out.println(e.getMessage());
        }
        return null;
    }

    @FXML
    public void newFile(ActionEvent actionEvent) {
        statusBar.setVisible(true);
        File file = createTempFile("hello");

        if (file == null){return ;}
        openFileInNewTab(file);
        tabPane.getSelectionModel().selectLast();
        Tab newTab = getSelectedTab();
        newTab.getStyleClass().add("unsaved");
        newTab.setText("Untitled*");
        // Create tmp file ? ask for file directory and name immediately ?
    }

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        Tab selectedTab = getSelectedTab();
        if (selectedTab == null) { return; }//Only save if a tab is selected

        if (selectedTab.getStyleClass().contains("unsaved")){saveAsFile(null);return;}
        CodeArea codeArea = getCodeArea();
        File file = new File(selectedTab.getText());
        if (saveContentToFile(file, codeArea)){
            selectedTab.getStyleClass().remove("modified");
        }
    }

    @FXML
    public void saveAsFile(ActionEvent actionEvent) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) { return;}// Only save if there is a current tab selected (i.e at least 1 open tab)
        CodeArea codeArea = getCodeArea();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("/"));
        fileChooser.setInitialFileName("new_file.mjj");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MiniJaja", "*.mjj")
        );
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile == null) {return;}//We sucessfully selected a file to save our project

        if (saveContentToFile(selectedFile, codeArea)){
            selectedTab.setText(selectedFile.getAbsolutePath());
            selectedTab.getStyleClass().remove("unsaved"); //remove the modified indicator
            selectedTab.getStyleClass().remove("modified"); //remove the modified indicator
        }
    }

    private boolean saveContentToFile(File file, CodeArea codeArea) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(codeArea.getText());
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred during file saving.");
            System.out.println(e.getMessage());
            // Handle the exception in case the fileWriter could not be initialized
        }
        return false;
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow() {
        JSONObject obj = new JSONObject();  // main obj in which all data will be stored.
        JSONArray list = new JSONArray();  // it contains the data of opened file.
        JSONObject fileObj;  // contains data of single opened file.
        boolean unsaved;
        boolean modified;
        for (Tab tab : tabPane.getTabs()) {
            unsaved = false;
            modified = false;
            CodeArea codeArea = (CodeArea) ((VirtualizedScrollPane) tab.getContent()).getContent();
            if (tab.getStyleClass().contains("modified")) {
                modified = true;
            }
            if (tab.getStyleClass().contains("unsaved")) {
                unsaved = true;
            }
            fileObj = new JSONObject();
            fileObj.put("path", tab.getText());
            fileObj.put("content", codeArea.getText());
            fileObj.put("unsaved", unsaved);
            fileObj.put("modified", modified);
            list.add(fileObj);
        }
        obj.put("files", list);

        try {
            String tmpDirPath = System.getProperty("java.io.tmpdir");
            String filePath = tmpDirPath + File.separator + "data.json";

            FileWriter file = new FileWriter(filePath);
            file.write(obj.toJSONString());
            file.close();
        }catch (IOException e) {
            System.out.println("An error occurred during file saving.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void undo(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.undo();
    }

    @FXML
    public void redo(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.redo();
    }

    @FXML
    public void cut(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.cut();
    }

    @FXML
    public void copy(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.copy();
    }

    @FXML
    public void paste(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.paste();
    }

    @FXML
    public void find(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        containerFind.setVisible(true);
        containerFind.setPrefHeight(41);
        toolbarDebug.setPrefHeight(32);
        findBox.setTranslateY(14);
        replaceBox.setTranslateY(42);
        replaceBox.setVisible(false);
        findInput.requestFocus();
    }

    private CodeArea getCodeArea(){
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeArea codeArea = (CodeArea) ((VirtualizedScrollPane) selectedTab.getContent()).getContent();
            for (CodeAreaWithBreakPoints codeAreaWithBreakPoints : codeAreas) {
                if (codeAreaWithBreakPoints.getCodeArea() == codeArea) {
                    return codeAreaWithBreakPoints.getCodeArea();
                }
            }
        }
        return null;
    }

    @FXML
    public void selectAll(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.selectAll();
    }

    @FXML
    public void changeTheme(ActionEvent actionEvent) {
        Scene scene = stage.getScene();
        if (scene != null){
            ObservableList<String> styleSheets = scene.getStylesheets();
            String current = styleSheets.get(0);
            if (current.matches(".*/light_theme.css$")){
                styleSheets.set(0,Ide.class.getResource("stylesheets/dark_theme.css").toExternalForm());
            }else{
                styleSheets.set(0,Ide.class.getResource("stylesheets/light_theme.css").toExternalForm());
            }
        }
    }

    @FXML
    public void duplicateSelection(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        IndexRange selectedText = codeArea.getSelection();
        String selected = codeArea.getText(selectedText);
        codeArea.insertText(codeArea.getCaretPosition(), selected);
    }

    @FXML
    public void replace(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        containerFind.setVisible(true);
        containerFind.setPrefHeight(79);
        toolbarDebug.setPrefHeight(38);
        findBox.setTranslateY(0);
        replaceBox.setTranslateY(0);
        replaceBox.setVisible(true);
        findInput.requestFocus();
    }

    @FXML
    public void copyLineDown(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        IndexRange range = codeArea.getCaretSelectionBind().getRange();
        int oldStart = range.getStart();
        if (codeArea.getSelectedText().isEmpty()) {
            codeArea.selectLine();
            range = codeArea.getCaretSelectionBind().getRange();
            String str = codeArea.getSelectedText();
            codeArea.insertText(range.getEnd(), "\n"+str);
            return;
        }

        codeArea.getCaretSelectionBind().moveTo(range.getEnd());
        codeArea.selectLine();
        range = codeArea.getCaretSelectionBind().getRange();
        int end = range.getEnd();

        codeArea.getCaretSelectionBind().moveTo(oldStart);
        codeArea.selectLine();
        range = codeArea.getCaretSelectionBind().getRange();
        int start = range.getStart();
        codeArea.getCaretSelectionBind().selectRange(start, end);
        codeArea.insertText(end, '\n' + codeArea.getSelectedText());
        codeArea.requestFollowCaret();
    }

    @FXML
    public void copyLineUp(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        IndexRange range = codeArea.getCaretSelectionBind().getRange();
        int oldStart = range.getStart();
        if (codeArea.getSelectedText().isEmpty()) {
            codeArea.selectLine();
            range = codeArea.getCaretSelectionBind().getRange();
            String str = codeArea.getSelectedText();
            codeArea.insertText(range.getEnd(), "\n"+str);
            return;
        }

        codeArea.getCaretSelectionBind().moveTo(range.getEnd());
        codeArea.selectLine();
        range = codeArea.getCaretSelectionBind().getRange();
        int end = range.getEnd();

        codeArea.getCaretSelectionBind().moveTo(oldStart);
        codeArea.selectLine();
        range = codeArea.getCaretSelectionBind().getRange();
        int start = range.getStart();
        codeArea.getCaretSelectionBind().selectRange(start, end);
        codeArea.insertText(start-1, '\n' + codeArea.getSelectedText());
        codeArea.requestFollowCaret();
    }

    @FXML
    public void moveLineUp(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.moveTo(codeArea.getCurrentParagraph()-1, codeArea.getParagraphLength(codeArea.getCurrentParagraph()-1));
    }

    @FXML
    public void moveLineDown(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.moveTo(codeArea.getCurrentParagraph()+1, codeArea.getParagraphLength(codeArea.getCurrentParagraph()+1));
    }

    @FXML
    public void help(ActionEvent actionEvent) {
        Stage popupwindow=new Stage();
        popupwindow.setTitle("Help");
        popupwindow.setResizable(false);

        Label label1= new Label("Compilation project M1 ISL - 2023-2024");
        Label label2= new Label("Anthony GASCA");
        Label label3= new Label("Baturay TURAN");
        Label label4= new Label("Mathéo LEONARD");
        Label label5= new Label("Ivann ROUX");
        Label label6= new Label("Yasser OMARI");

        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1,label2,label3,label4,label5, label6);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);

        popupwindow.setScene(scene1);
        popupwindow.setX(stage.getX() + stage.getWidth() / 2 - 200);
        popupwindow.setY(stage.getY() + stage.getHeight() / 2 - 200);
        popupwindow.setOnCloseRequest(e -> popupwindow.close());
        popupwindow.show();
    }

    void setupTab(CodeArea codeArea, Tab tab) {
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!tabPane.getSelectionModel().getSelectedItem().getStyleClass().contains("modified")){
                tabPane.getSelectionModel().getSelectedItem().getStyleClass().add("modified");
            }
        });
        virtualizedScrollPane = new VirtualizedScrollPane(codeArea);
        tab.setContent(virtualizedScrollPane);
    }

    public void resetCompileJajacodeOutput() {
        for (CodeAreaWithBreakPoints codeAreaWithBreakPoints : codeAreas) {
            if (codeAreaWithBreakPoints.getCodeArea() == compileJajacode) {
                codeAreaWithBreakPoints.setBreakPoints(new TreeSet<>());
                codeAreaWithBreakPoints.apply(true);
                return;
            }
        }
        codeAreas.add(new CodeAreaWithBreakPoints(compileJajacode));
    }

    private SimpleNode parseMinijaja(String code) throws ParseException {
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        return parser.classe();
    }

    private fr.ufrst.m1info.comp4.parser.jajacode.SimpleNode parseJajacode(String code) throws fr.ufrst.m1info.comp4.parser.jajacode.ParseException {
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Jajacode parser = new Jajacode(stream);
        return parser.classe();
    }

    private String runTypeChecker(SimpleNode root) throws VisitorMJJException {
        TypeChecker typeChecker = new TypeChecker(root);
        memory = new Memory();
        typeChecker.setTable(memory.getSymbolTable());
        typeChecker.check();
        StringBuilder builder = new StringBuilder();
        for (ErrorType error : typeChecker.getErrors()) {
            builder.append(error.toString()).append("\n");
        }
        return builder.toString();
    }

    @FXML
    public void runMinijaja(ActionEvent actionEvent) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null || codeArea.getText().isEmpty()) {
            return;
        }

        clearOutputMemory();
        clearOutputMinijaja();
        writeOutputMinijaja("Execution start");

        try {
            SimpleNode root = parseMinijaja(codeArea.getText());
            String errors = runTypeChecker(root);
            if (!errors.isEmpty()) {
                writeOutputMinijaja(errors);
                writeOutputMinijaja("Execution failed");
                return;
            }

            interpreterMJJ = new InterpreterMJJ(root);
            interpreterMJJ.setMemory(memory);
            interpreterMJJ.interpret();

            writeOutputMinijaja(interpreterMJJ.getOutput());
            writeOutputMinijaja("Execution finished");
        } catch (ParseException | VisitorMJJException e) {
            writeOutputMinijaja(interpreterMJJ.getOutput());
            writeOutputMinijaja(e.getMessage());
            writeOutputMinijaja("Execution failed");
        }
    }

    @FXML
    public void compileToJaJaCode(ActionEvent actionEvent){
        CodeArea codeArea = getCodeArea();
        if (codeArea == null || codeArea.getText().isEmpty()) {
            return;
        }

        clearOutputMemory();
        clearOutputJajacode();
        clearCompileJajacode();
        writeOutputJajacode("Compilation start");

        try {
            SimpleNode rootMJJ = parseMinijaja(codeArea.getText());
            String errors = runTypeChecker(rootMJJ);

            if (!errors.isEmpty()) {
                writeOutputJajacode(errors);
                writeOutputJajacode("Compilation failed");
                return;
            }
            lastMemoryJJC = memory;
            this.symbolTableJJC = lastMemoryJJC.getSymbolTable();

            compiler.setRoot(rootMJJ);
            compiler.compile();
            resetCompileJajacodeOutput();

            compileJajacode.appendText(compiler.getInstrsWithoutAddress());
            compileJajacode.moveTo(0, 0);
            compileJajacode.requestFollowCaret();
            compileJajacode.requestFocus();
            writeOutputJajacode("Compilation finished");
        } catch (ParseException | VisitorMJJException e) {
            writeOutputJajacode(e.getMessage());
            writeOutputJajacode("Compilation failed");
        }
    }

    @FXML
    public void runJajacode(ActionEvent actionEvent){
        if (compileJajacode.getText().isEmpty()) {
            return;
        }

        clearOutputMemory();
        writeOutputJajacode("Execution start");

        try{
            String codeJJC = compiler.getInstrs();
            fr.ufrst.m1info.comp4.parser.jajacode.SimpleNode rootJJC = parseJajacode(codeJJC);

            interpreterJJC = new InterpreterJJC(rootJJC);
            interpreterJJC.setMemory(lastMemoryJJC);
            interpreterJJC.interpret();

            writeOutputJajacode(interpreterJJC.getOutput());
            writeOutputJajacode("Execution finished");
        }catch (fr.ufrst.m1info.comp4.parser.jajacode.ParseException | VisitorJJCException e) {
            //writeOutputMemory(lastMemoryJJC.toStringTopStack(), lastMemoryJJC.toStringHeap());
            writeOutputJajacode(interpreterJJC.getOutput());
            writeOutputJajacode(e.getMessage());
            writeOutputJajacode("Execution failed");
        }

        lastMemoryJJC.clear(this.symbolTableJJC);
    }

    @FXML
    public void debugMinijaja() throws ParseException, VisitorMJJException {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null || codeArea.getText().isEmpty()) {
            return;
        }
        clearOutputMemory();
        SimpleNode root = parseMinijaja(codeArea.getText());
        String errors = runTypeChecker(root);
        if (!errors.isEmpty()) {
            writeOutputMinijaja(errors);
            writeOutputMinijaja("Execution failed");
            return;
        }

        interpreterMJJ = new InterpreterMJJ(root);
        interpreterMJJ.setMemory(memory);
        debugger.setInterpreterMJJ(interpreterMJJ);
        debugger.setBreakPoints(getBreakPoints(codeArea));
        debugger.startDebugMJJ();
        clearOutputMinijaja();
        writeOutputMinijaja("Debugging started");
        debugLabel.setText("Debugging MiniJaja");
        actionToolBar(true);
        codeArea.showParagraphAtCenter(root.getLine()-1);
        codeArea.moveTo(root.getLine()-1, 0);
    }

    public Set<Integer> getBreakPoints(CodeArea codeArea) {
        for (CodeAreaWithBreakPoints codeAreaWithBreakPoints : codeAreas) {
            if (codeAreaWithBreakPoints.getCodeArea() == codeArea) {
                return codeAreaWithBreakPoints.getBreakPoints();
            }
        }
        return null;
    }

    @FXML
    public void debugJajacode() throws fr.ufrst.m1info.comp4.parser.jajacode.ParseException {
        if (compileJajacode.getText().isEmpty()) {
            return;
        }
        clearOutputMemory();
        fr.ufrst.m1info.comp4.parser.jajacode.SimpleNode rootJJC = parseJajacode(compiler.getInstrs());

        interpreterJJC = new InterpreterJJC(rootJJC);
        interpreterJJC.setMemory(lastMemoryJJC);
        debugger.setInterpreterJJC(interpreterJJC);
        interpreterJJC.setDebugger(debugger);
        debugger.setBreakPoints(getBreakPoints(compileJajacode));
        debugger.startDebugJJC();
        clearOutputJajacode();
        writeOutputJajacode("Debugging started");
        debugLabel.setText("Debugging JajaCode");
        actionToolBar(true);
        compileJajacode.showParagraphAtCenter(0);
        compileJajacode.moveTo(0, 0);
    }

    public void actionToolBar(Boolean visible) {
        buttonsDebug.setVisible(visible);
    }

    @FXML
    public void debugStop(){
        debugger.stop();
        if (debugger.debuggingMJJ()) {
            CodeArea codeArea = getCodeArea();
            if (codeArea == null || codeArea.getText().isEmpty()) {
                return;
            }
            clearOutputMinijaja();
            writeOutputMinijaja("Debugging started");
            writeOutputMinijaja(interpreterMJJ.getOutput());
            writeOutputMinijaja(debugger.getError());
            writeOutputMinijaja("Debugging stopped");
        } else {
            clearOutputJajacode();
            writeOutputJajacode("Debugging started");
            writeOutputJajacode(interpreterJJC.getOutput());
            writeOutputJajacode(debugger.getError());
            writeOutputJajacode("Debugging stopped");
        }
        actionToolBar(false);
        lastMemoryJJC.clear(this.symbolTableJJC);
    }

    @FXML
    public void debuggerContinue(ActionEvent actionEvent) {
        debugger.continu();
        catchResult();
    }

    @FXML
    public void debuggerStep(ActionEvent actionEvent){
        debugger.step();
        catchResult();
    }

    public void catchResult() {
        AtomicBoolean running = new AtomicBoolean(true);
        Thread thread = new Thread(() -> {
            while (running.get()) {
                if (debugger.isFinished()){
                    Object[] res = debugger.getPauseResult();
                    Platform.runLater(this::clearOutputMemory);
                    Platform.runLater(() -> writeOutputMemory((String) res[1], (String) res[2]));
                    Platform.runLater(this::debugStop);
                    running.set(false);
                }
                if (debugger.isPaused()) {
                    Object[] res = debugger.getPauseResult();
                    Platform.runLater(() -> nextStep(res));
                    running.set(false);
                }
            }
        });
        thread.start();
    }

    private void nextStep(Object[] res) {
        clearOutputMemory();
        writeOutputMemory((String) res[1], (String) res[2]);
        if (debugger.debuggingMJJ()) {
            clearOutputMinijaja();
            writeOutputMinijaja("Debugging started");
            writeOutputMinijaja(interpreterMJJ.getOutput());
            highlightCurrentLineMinijaja((int) res[0]);
        } else {
            clearOutputJajacode();
            writeOutputJajacode("Debugging started");
            writeOutputJajacode(interpreterJJC.getOutput());
            highlightCurrentLineJajacode((int) res[0]);
        }
    }

    private void highlightCurrentLineMinijaja(int lineNumber) {
        CodeArea codeArea = getCodeArea();
        if (codeArea == null || codeArea.getText().isEmpty()){return;}
        hightlightLine(lineNumber, codeArea);
    }

    private void highlightCurrentLineJajacode(int lineNumber) {
        hightlightLine(lineNumber, compileJajacode);
    }

    private void hightlightLine(int lineNumber, CodeArea codeArea) {
        codeArea.showParagraphAtCenter(lineNumber-1);
        codeArea.moveTo(lineNumber-1, 0);
    }

    public void clearOutputJajacode() {
        outputJajacode.clear();
    }

    public void writeOutputJajacode(String message) {
        outputJajacode.appendText(message + "\n");
    }

    public void clearOutputMinijaja() {
        outputMinijaja.clear();
    }

    public void writeOutputMinijaja(String message) {
        outputMinijaja.appendText(message + "\n");
    }

    public void clearOutputMemory(){
        outputStack.clear();
        outputHeap.clear();
    }

    public void writeOutputMemory(String stack, String heap){
        outputStack.appendText(stack);
        outputHeap.appendText(heap);
    }

    public void clearCompileJajacode() {
        compileJajacode.clear();
        compileJajacode.setId(null);
        compileJajacode.setParagraphGraphicFactory(null);
    }

    /****************************************/
    /****************FIND BOX****************/
    /****************************************/

    @FXML
    void findWord() {
        if (findInput.getText().isEmpty()) return;
        highlightText(findInput, coordinateList, currWordIndex, getCodeArea());
        if (coordinateList.isEmpty()) return;
        totalIndexLabel.setText(String.valueOf(coordinateList.size()));
        currIndexLabel.setText(String.valueOf(currWordIndex.get()+1));
    }

    @FXML
    void closeFind() {
        containerFind.setVisible(false);
        replaceBox.setVisible(false);
        toolbarDebug.setPrefHeight(32);
        containerFind.setPrefHeight(41);
        findBox.setTranslateY(14);
        replaceBox.setTranslateY(42);
        removeHighlightedTxt(coordinateList, getCodeArea(), currWordIndex);
    }

    @FXML
    void nextWord() {
        if (coordinateList.isEmpty()) return;
        nextOccurence(coordinateList, currWordIndex, getCodeArea());
        currIndexLabel.setText(String.valueOf(currWordIndex.get()+1));
    }

    @FXML
    void prevWord() {
        if (coordinateList.isEmpty()) return;
        prevOccurence(coordinateList, currWordIndex, getCodeArea());
        currIndexLabel.setText(String.valueOf(currWordIndex.get()+1));
    }

    @FXML
    void replaceWord() {
        if (coordinateList.isEmpty()) return;
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.replaceText(coordinateList.get(currWordIndex.get()).get(0), coordinateList.get(currWordIndex.get()).get(1), replaceInput.getText());
        highlightText(findInput, coordinateList, currWordIndex, codeArea);
    }

    @FXML
    void replaceAll() {
        if (coordinateList.isEmpty()) return;
        CodeArea codeArea = getCodeArea();
        if (codeArea == null){return;}
        codeArea.replaceText(codeArea.getText().replaceAll("\\b(" + findInput.getText() + ")\\b", replaceInput.getText()));
    }

    @FXML
    void toggleReplace() {
        if (containerFind.getPrefHeight()<=47) {
            containerFind.setPrefHeight(79);
            toolbarDebug.setPrefHeight(38);
            findBox.setTranslateY(0);
            replaceBox.setTranslateY(0);
        } else {
            containerFind.setPrefHeight(41);
            toolbarDebug.setPrefHeight(32);
            findBox.setTranslateY(14);
            replaceBox.setTranslateY(42);
        }
        replaceBox.setVisible(!replaceBox.isVisible());
    }

    public void nextOccurence(ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea codeArea) {
        if (currWordIndex.get() >= (coordinateList.size()-1) && !coordinateList.isEmpty()) return;
        currWordIndex.incrementAndGet();
        int index = currWordIndex.get();
        codeArea.getCaretSelectionBind().moveTo(coordinateList.get(currWordIndex.get()).get(0));
    }

    public void prevOccurence(ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea codeArea) {
        if (currWordIndex.get() <= 0 && !coordinateList.isEmpty()) return;
        currWordIndex.decrementAndGet();
        int index = currWordIndex.get();
        codeArea.getCaretSelectionBind().moveTo(coordinateList.get(currWordIndex.get()).get(0));
    }

    public void removeHighlightedTxt(ArrayList<ArrayList<Integer>> coordinateList, StyleClassedTextArea currCodeArea, AtomicInteger currWordIndex) {
        if (!coordinateList.isEmpty()) {
            coordinateList.clear();
            currWordIndex.set(0);
        }
    }

    public void highlightText(TextField textField, ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea currCodeArea) {
        removeHighlightedTxt(coordinateList, currCodeArea, currWordIndex);
        Pattern pattern = Pattern.compile("\\b("+textField.getText()+")\\b");
        Matcher matcher = pattern.matcher(currCodeArea.getText());
        while (matcher.find()) {
            coordinateList.add(new ArrayList<>(Arrays.asList(matcher.start(), matcher.end())));
        }
        if (!coordinateList.isEmpty()) currCodeArea.getCaretSelectionBind().moveTo(coordinateList.get(0).get(0));
        currCodeArea.requestFollowCaret();
    }

}