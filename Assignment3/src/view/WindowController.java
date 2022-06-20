package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * The class WindowController contains all FXML elements which are part of the GUI and provides getter and setter to those.
 */
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
    private Menu viewMenu;

    @FXML
    private MenuItem wrapMenuItem;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab sequencesTab;

    @FXML
    private AnchorPane sequenceTabAnchorPane;

    @FXML
    private ToolBar checkBoxesToolbar;

    @FXML
    private CheckBox headersCheckBox;

    @FXML
    private CheckBox sequencesCheckBox;

    @FXML
    private TextArea textOutputArea;

    @FXML
    private Tab distancesTab;

    @FXML
    private AnchorPane distanceTabAnchorPane;

    @FXML
    private ToolBar distanceTabToolbar;

    @FXML
    private Label kmerSizeLabel;

    @FXML
    private TextField kmerSizeTextField;

    @FXML
    private Button applyButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane scrollPaneAnchorPane;

    @FXML
    private GridPane distancesOutputGridPane;

    @FXML
    private TextField bottomLineTextField;

    public BorderPane getBoderPane() {
        return borderPane;
    }

    public void setBoderPane(BorderPane boderPane) {
        this.borderPane = boderPane;
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

    public Menu getViewMenu() {
        return viewMenu;
    }

    public void setViewMenu(Menu viewMenu) {
        this.viewMenu = viewMenu;
    }

    public MenuItem getWrapMenuItem() {
        return wrapMenuItem;
    }

    public void setWrapMenuItem(MenuItem wrapMenuItem) {
        this.wrapMenuItem = wrapMenuItem;
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

    public Tab getSequencesTab() {
        return sequencesTab;
    }

    public void setSequencesTab(Tab sequencesTab) {
        this.sequencesTab = sequencesTab;
    }

    public AnchorPane getSequenceTabAnchorPane() {
        return sequenceTabAnchorPane;
    }

    public void setSequenceTabAnchorPane(AnchorPane sequenceTabAnchorPane) {
        this.sequenceTabAnchorPane = sequenceTabAnchorPane;
    }

    public ToolBar getCheckBoxesToolbar() {
        return checkBoxesToolbar;
    }

    public void setCheckBoxesToolbar(ToolBar checkBoxesToolbar) {
        this.checkBoxesToolbar = checkBoxesToolbar;
    }

    public CheckBox getHeadersCheckBox() {
        return headersCheckBox;
    }

    public void setHeadersCheckBox(CheckBox headersCheckBox) {
        this.headersCheckBox = headersCheckBox;
    }

    public CheckBox getSequencesCheckBox() {
        return sequencesCheckBox;
    }

    public void setSequencesCheckBox(CheckBox sequencesCheckBox) {
        this.sequencesCheckBox = sequencesCheckBox;
    }

    public TextArea getTextOutputArea() {
        return textOutputArea;
    }

    public void setTextOutputArea(TextArea textOutputArea) {
        this.textOutputArea = textOutputArea;
    }

    public Tab getDistancesTab() {
        return distancesTab;
    }

    public void setDistancesTab(Tab distancesTab) {
        this.distancesTab = distancesTab;
    }

    public AnchorPane getDistanceTabAnchorPane() {
        return distanceTabAnchorPane;
    }

    public void setDistanceTabAnchorPane(AnchorPane distanceTabAnchorPane) {
        this.distanceTabAnchorPane = distanceTabAnchorPane;
    }

    public ToolBar getDistanceTabToolbar() {
        return distanceTabToolbar;
    }

    public void setDistanceTabToolbar(ToolBar distanceTabToolbar) {
        this.distanceTabToolbar = distanceTabToolbar;
    }

    public Label getKmerSizeLabel() {
        return kmerSizeLabel;
    }

    public void setKmerSizeLabel(Label kmerSizeLabel) {
        this.kmerSizeLabel = kmerSizeLabel;
    }

    public TextField getKmerSizeTextField() {
        return kmerSizeTextField;
    }

    public void setKmerSizeTextField(TextField kmerSizeTextField) {
        this.kmerSizeTextField = kmerSizeTextField;
    }

    public Button getApplyButton() {
        return applyButton;
    }

    public void setApplyButton(Button applyButton) {
        this.applyButton = applyButton;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public AnchorPane getScrollPaneAnchorPane() {
        return scrollPaneAnchorPane;
    }

    public void setScrollPaneAnchorPane(AnchorPane scrollPaneAnchorPane) {
        this.scrollPaneAnchorPane = scrollPaneAnchorPane;
    }

    public GridPane getDistancesOutputGridPane() {
        return distancesOutputGridPane;
    }

    public void setDistancesOutputGridPane(GridPane distancesOutputGridPane) {
        this.distancesOutputGridPane = distancesOutputGridPane;
    }

    public TextField getBottomLineTextField() {
        return bottomLineTextField;
    }

    public void setBottomLineTextField(TextField bottomLineTextField) {
        this.bottomLineTextField = bottomLineTextField;
    }

}