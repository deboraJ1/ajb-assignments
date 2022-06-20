package view;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class WindowController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu fileMenu;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private MenuItem closeFileMenuItem;

    @FXML
    private Menu editMenu;

    @FXML
    private MenuItem clearMenuItem;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab textTab;

    @FXML
    private BorderPane txtTabBorderPane;

    @FXML
    private TextArea outputAreaCTxtTab;

    @FXML
    private HBox txtTabBottomHBox;

    @FXML
    private Label charLabelBTxtTab;

    @FXML
    private Button clearButtonBTxtTab;

    @FXML
    private Button parseButtonBTxtTab;

    @FXML
    private Tab treeTab;

    @FXML
    private BorderPane treeTabBorderPane;

    @FXML
    private ChoiceBox<String> choiceBoxTBorderPane;

    @FXML
    private CheckBox scaleCheckBoxRBorderPane;

    @FXML
    private ScrollPane scrollPaneCBorderPane;

    @FXML
    private Pane drawPaneScrollPaneC;

    @FXML
    private Button drawButtonBBorderPane;

    @FXML
    private FlowPane statusFPBbP;

    @FXML
    private Label nodesStatusLabelFP;

    @FXML
    private Label edgesStatusLabelFP;

    @FXML
    private Label leavesStatusLabelFP;

    @FXML
    private Label binaryStatusLabelFP;

    @FXML
    private Label totalLengthStatusLabelFP;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public void setFileMenu(Menu fileMenu) {
        this.fileMenu = fileMenu;
    }

    public MenuItem getOpenFileMenuItem() {
        return openFileMenuItem;
    }

    public void setOpenFileMenuItem(MenuItem openFileMenuItem) {
        this.openFileMenuItem = openFileMenuItem;
    }

    public MenuItem getCloseFileMenuItem() {
        return closeFileMenuItem;
    }

    public void setCloseFileMenuItem(MenuItem closeFileMenuItem) {
        this.closeFileMenuItem = closeFileMenuItem;
    }

    public Menu getEditMenu() {
        return editMenu;
    }

    public void setEditMenu(Menu editMenu) {
        this.editMenu = editMenu;
    }

    public MenuItem getClearMenuItem() {
        return clearMenuItem;
    }

    public void setClearMenuItem(MenuItem clearMenuItem) {
        this.clearMenuItem = clearMenuItem;
    }

    public Menu getHelpMenu() {
        return helpMenu;
    }

    public void setHelpMenu(Menu helpMenu) {
        this.helpMenu = helpMenu;
    }

    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public void setAboutMenuItem(MenuItem aboutMenuItem) {
        this.aboutMenuItem = aboutMenuItem;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public Tab getTextTab() {
        return textTab;
    }

    public void setTextTab(Tab textTab) {
        this.textTab = textTab;
    }

    public BorderPane getTxtTabBorderPane() {
        return txtTabBorderPane;
    }

    public void setTxtTabBorderPane(BorderPane txtTabBorderPane) {
        this.txtTabBorderPane = txtTabBorderPane;
    }

    public TextArea getOutputAreaCTxtTab() {
        return outputAreaCTxtTab;
    }

    public void setOutputAreaCTxtTab(TextArea outputAreaCTxtTab) {
        this.outputAreaCTxtTab = outputAreaCTxtTab;
    }

    public HBox getTxtTabBottomHBox() {
        return txtTabBottomHBox;
    }

    public void setTxtTabBottomHBox(HBox txtTabBottomHBox) {
        this.txtTabBottomHBox = txtTabBottomHBox;
    }

    public Label getCharLabelBTxtTab() {
        return charLabelBTxtTab;
    }

    public void setCharLabelBTxtTab(Label charLabelBTxtTab) {
        this.charLabelBTxtTab = charLabelBTxtTab;
    }

    public Button getClearButtonBTxtTab() {
        return clearButtonBTxtTab;
    }

    public void setClearButtonBTxtTab(Button clearButtonBTxtTab) {
        this.clearButtonBTxtTab = clearButtonBTxtTab;
    }

    public Button getParseButtonBTxtTab() {
        return parseButtonBTxtTab;
    }

    public void setParseButtonBTxtTab(Button parseButtonBTxtTab) {
        this.parseButtonBTxtTab = parseButtonBTxtTab;
    }

    public Tab getTreeTab() {
        return treeTab;
    }

    public void setTreeTab(Tab treeTab) {
        this.treeTab = treeTab;
    }

    public BorderPane getTreeTabBorderPane() {
        return treeTabBorderPane;
    }

    public void setTreeTabBorderPane(BorderPane treeTabBorderPane) {
        this.treeTabBorderPane = treeTabBorderPane;
    }

    public ChoiceBox<String> getChoiceBoxTBorderPane() {
        return choiceBoxTBorderPane;
    }

    public void setChoiceBoxTBorderPane(ChoiceBox<String> choiceBoxTBorderPane) {
        this.choiceBoxTBorderPane = choiceBoxTBorderPane;
    }

    public CheckBox getScaleCheckBoxRBorderPane() {
        return scaleCheckBoxRBorderPane;
    }

    public void setScaleCheckBoxRBorderPane(CheckBox scaleCheckBoxRBorderPane) {
        this.scaleCheckBoxRBorderPane = scaleCheckBoxRBorderPane;
    }

    public ScrollPane getScrollPaneCBorderPane() {
        return scrollPaneCBorderPane;
    }

    public void setScrollPaneCBorderPane(ScrollPane scrollPaneCBorderPane) {
        this.scrollPaneCBorderPane = scrollPaneCBorderPane;
    }

    public Pane getDrawPaneScrollPaneC() {
        return drawPaneScrollPaneC;
    }

    public void setDrawPaneScrollPaneC(Pane drawPaneScrollPaneC) {
        this.drawPaneScrollPaneC = drawPaneScrollPaneC;
    }

    public Button getDrawButtonBBorderPane() {
        return drawButtonBBorderPane;
    }

    public void setDrawButtonBBorderPane(Button drawButtonBBorderPane) {
        this.drawButtonBBorderPane = drawButtonBBorderPane;
    }

    public FlowPane getStatusFPBbP() {
        return statusFPBbP;
    }

    public void setStatusFPBbP(FlowPane statusFPBbP) {
        this.statusFPBbP = statusFPBbP;
    }

    public Label getNodesStatusLabelFP() {
        return nodesStatusLabelFP;
    }

    public void setNodesStatusLabelFP(Label nodesStatusLabelFP) {
        this.nodesStatusLabelFP = nodesStatusLabelFP;
    }

    public Label getEdgesStatusLabelFP() {
        return edgesStatusLabelFP;
    }

    public void setEdgesStatusLabelFP(Label edgesStatusLabelFP) {
        this.edgesStatusLabelFP = edgesStatusLabelFP;
    }

    public Label getLeavesStatusLabelFP() {
        return leavesStatusLabelFP;
    }

    public void setLeavesStatusLabelFP(Label leavesStatusLabelFP) {
        this.leavesStatusLabelFP = leavesStatusLabelFP;
    }

    public Label getBinaryStatusLabelFP() {
        return binaryStatusLabelFP;
    }

    public void setBinaryStatusLabelFP(Label binaryStatusLabelFP) {
        this.binaryStatusLabelFP = binaryStatusLabelFP;
    }

    public Label getTotalLengthStatusLabelFP() {
        return totalLengthStatusLabelFP;
    }

    public void setTotalLengthStatusLabelFP(Label totalLengthStatusLabelFP) {
        this.totalLengthStatusLabelFP = totalLengthStatusLabelFP;
    }
}
