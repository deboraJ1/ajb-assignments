package view;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class WindowController {

    @FXML
    private VBox root;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu fileMenu;

    @FXML
    private MenuItem openMI;

    @FXML
    private MenuItem closeMI;

    @FXML
    private Menu editMenu;

    @FXML
    private MenuItem undoMI;

    @FXML
    private MenuItem redoMI;

    @FXML
    private Menu editMenu1;

    @FXML
    private MenuItem selectMI;

    @FXML
    private MenuItem clearMI;

    @FXML
    private MenuItem showBiojava;

    @FXML
    private CheckMenuItem darkThemeMI;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem aboutMI;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<String> idListView;

    @FXML
    private Pane moleculeViewPane;

    @FXML
    private Pane selectionPane;

    @FXML
    private ComboBox<String> styleComboBox;

    @FXML
    private ComboBox<String> coloringComboBox;

    @FXML
    private Button bioJavaButton;

    @FXML
    private CheckBox showBallsCheckbox;

    @FXML
    private CheckBox showBondsCheckbox;

    @FXML
    private ChoiceBox<String> atomChoiceBox;

    @FXML
    private Button explosionButton;

    @FXML
    private Button animationButton;

    @FXML
    private Slider moleculeSizeSlider;

    @FXML
    private Slider bondsSizeSlider;

    @FXML
    private Label moleculeProgressBarLabel;

    @FXML
    private ProgressBar moleculeProgressBar;

    @FXML
    private Label setupViewProgressBarLabel;

    @FXML
    private ProgressBar setupViewProgressBar;

    @FXML
    private Label moleculeNameLabel;

    @FXML
    private Label fileProgressBarLabel;

    @FXML
    private ProgressBar fileProgressBar;

    @FXML
    private TextArea fileOutputTextArea;

    @FXML
    private Label chartsTitle;

    @FXML
    private PieChart aminoAcidsPieChart;

    @FXML
    private PieChart nucleotidesPieChart;

    @FXML
    private BarChart<String, Number> atomsBarChart;

    @FXML
    private CategoryAxis atomsBCCategories;

    @FXML
    private NumberAxis atomsBCNumbers;

    @FXML
    private BarChart<String, Number> atomsPerChainStructBarChart;

    @FXML
    private CategoryAxis atomsPerChainStructBCCategories;

    @FXML
    private NumberAxis atomsPerChainStructBCNumbers;

    @FXML
    private HBox buttonHBoxT;

    @FXML
    private Button sarsCovButton;

    @FXML
    private Button listAllButton;

    @FXML
    private Label statusLabelB;

    public VBox getRoot() {
        return root;
    }

    public void setRoot(VBox root) {
        this.root = root;
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

    public MenuItem getOpenMI() {
        return openMI;
    }

    public void setOpenMI(MenuItem openMI) {
        this.openMI = openMI;
    }

    public MenuItem getCloseMI() {
        return closeMI;
    }

    public void setCloseMI(MenuItem closeMI) {
        this.closeMI = closeMI;
    }

    public Menu getEditMenu() {
        return editMenu;
    }

    public void setEditMenu(Menu editMenu) {
        this.editMenu = editMenu;
    }

    public MenuItem getUndoMI() {
        return undoMI;
    }

    public void setUndoMI(MenuItem undoMI) {
        this.undoMI = undoMI;
    }

    public MenuItem getRedoMI() {
        return redoMI;
    }

    public void setRedoMI(MenuItem redoMI) {
        this.redoMI = redoMI;
    }

    public Menu getEditMenu1() {
        return editMenu1;
    }

    public void setEditMenu1(Menu editMenu1) {
        this.editMenu1 = editMenu1;
    }

    public MenuItem getSelectMI() {
        return selectMI;
    }

    public void setSelectMI(MenuItem selectMI) {
        this.selectMI = selectMI;
    }

    public MenuItem getClearMI() {
        return clearMI;
    }

    public void setClearMI(MenuItem clearMI) {
        this.clearMI = clearMI;
    }

    public MenuItem getShowBiojava() {
        return showBiojava;
    }

    public void setShowBiojava(MenuItem showBiojava) {
        this.showBiojava = showBiojava;
    }

    public CheckMenuItem getDarkThemeMI() {
        return darkThemeMI;
    }

    public void setDarkThemeMI(CheckMenuItem darkThemeMI) {
        this.darkThemeMI = darkThemeMI;
    }

    public Menu getHelpMenu() {
        return helpMenu;
    }

    public void setHelpMenu(Menu helpMenu) {
        this.helpMenu = helpMenu;
    }

    public MenuItem getAboutMI() {
        return aboutMI;
    }

    public void setAboutMI(MenuItem aboutMI) {
        this.aboutMI = aboutMI;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    public TextField getSearchTextField() {
        return searchTextField;
    }

    public void setSearchTextField(TextField searchTextField) {
        this.searchTextField = searchTextField;
    }

    public ListView<String> getIdListView() {
        return idListView;
    }

    public void setIdListView(ListView<String> idListView) {
        this.idListView = idListView;
    }

    public Pane getMoleculeViewPane() {
        return moleculeViewPane;
    }

    public void setMoleculeViewPane(Pane moleculeViewPane) {
        this.moleculeViewPane = moleculeViewPane;
    }

    public Pane getSelectionPane() {
        return selectionPane;
    }

    public void setSelectionPane(Pane selectionPane) {
        this.selectionPane = selectionPane;
    }

    public ComboBox<String> getStyleComboBox() {
        return styleComboBox;
    }

    public void setStyleComboBox(ComboBox<String> styleComboBox) {
        this.styleComboBox = styleComboBox;
    }

    public ComboBox<String> getColoringComboBox() {
        return coloringComboBox;
    }

    public void setColoringComboBox(ComboBox<String> coloringComboBox) {
        this.coloringComboBox = coloringComboBox;
    }

    public Button getBioJavaButton() {
        return bioJavaButton;
    }

    public void setBioJavaButton(Button bioJavaButton) {
        this.bioJavaButton = bioJavaButton;
    }

    public CheckBox getShowBallsCheckbox() {
        return showBallsCheckbox;
    }

    public void setShowBallsCheckbox(CheckBox showBallsCheckbox) {
        this.showBallsCheckbox = showBallsCheckbox;
    }

    public CheckBox getShowBondsCheckbox() {
        return showBondsCheckbox;
    }

    public void setShowBondsCheckbox(CheckBox showBondsCheckbox) {
        this.showBondsCheckbox = showBondsCheckbox;
    }

    public ChoiceBox<String> getAtomChoiceBox() {
        return atomChoiceBox;
    }

    public void setAtomChoiceBox(ChoiceBox<String> atomChoiceBox) {
        this.atomChoiceBox = atomChoiceBox;
    }

    public Button getExplosionButton() {
        return explosionButton;
    }

    public void setExplosionButton(Button explosionButton) {
        this.explosionButton = explosionButton;
    }

    public Button getAnimationButton() {
        return animationButton;
    }

    public void setAnimationButton(Button animationButton) {
        this.animationButton = animationButton;
    }

    public Slider getMoleculeSizeSlider() {
        return moleculeSizeSlider;
    }

    public void setMoleculeSizeSlider(Slider moleculeSizeSlider) {
        this.moleculeSizeSlider = moleculeSizeSlider;
    }

    public Label getMoleculeProgressBarLabel() {
        return moleculeProgressBarLabel;
    }

    public void setMoleculeProgressBarLabel(Label moleculeProgressBarLabel) {
        this.moleculeProgressBarLabel = moleculeProgressBarLabel;
    }

    public ProgressBar getMoleculeProgressBar() {
        return moleculeProgressBar;
    }

    public void setMoleculeProgressBar(ProgressBar moleculeProgressBar) {
        this.moleculeProgressBar = moleculeProgressBar;
    }

    public Slider getBondsSizeSlider() {
        return bondsSizeSlider;
    }

    public void setBondsSizeSlider(Slider bondsSizeSlider) {
        this.bondsSizeSlider = bondsSizeSlider;
    }

    public Label getSetupViewProgressBarLabel() {
        return setupViewProgressBarLabel;
    }

    public void setSetupViewProgressBarLabel(Label setupViewProgressBarLabel) {
        this.setupViewProgressBarLabel = setupViewProgressBarLabel;
    }

    public ProgressBar getSetupViewProgressBar() {
        return setupViewProgressBar;
    }

    public void setSetupViewProgressBar(ProgressBar setupViewProgressBar) {
        this.setupViewProgressBar = setupViewProgressBar;
    }

    public Label getMoleculeNameLabel() {
        return moleculeNameLabel;
    }

    public void setMoleculeNameLabel(Label moleculeNameLabel) {
        this.moleculeNameLabel = moleculeNameLabel;
    }

    public Label getFileProgressBarLabel() {
        return fileProgressBarLabel;
    }

    public void setFileProgressBarLabel(Label fileProgressBarLabel) {
        this.fileProgressBarLabel = fileProgressBarLabel;
    }

    public ProgressBar getFileProgressBar() {
        return fileProgressBar;
    }

    public void setFileProgressBar(ProgressBar fileProgressBar) {
        this.fileProgressBar = fileProgressBar;
    }

    public TextArea getFileOutputTextArea() {
        return fileOutputTextArea;
    }

    public void setFileOutputTextArea(TextArea fileOutputTextArea) {
        this.fileOutputTextArea = fileOutputTextArea;
    }

    public Label getChartsTitle() {
        return chartsTitle;
    }

    public void setChartsTitle(Label chartsTitle) {
        this.chartsTitle = chartsTitle;
    }

    public PieChart getAminoAcidsPieChart() {
        return aminoAcidsPieChart;
    }

    public void setAminoAcidsPieChart(PieChart aminoAcidsPieChart) {
        this.aminoAcidsPieChart = aminoAcidsPieChart;
    }

    public PieChart getNucleotidesPieChart() {
        return nucleotidesPieChart;
    }

    public void setNucleotidesPieChart(PieChart nucleotidesPieChart) {
        this.nucleotidesPieChart = nucleotidesPieChart;
    }

    public BarChart<String, Number> getAtomsBarChart() {
        return atomsBarChart;
    }

    public void setAtomsBarChart(BarChart<String, Number> atomsBarChart) {
        this.atomsBarChart = atomsBarChart;
    }

    public CategoryAxis getAtomsBCCategories() {
        return atomsBCCategories;
    }

    public void setAtomsBCCategories(CategoryAxis atomsBCCategories) {
        this.atomsBCCategories = atomsBCCategories;
    }

    public NumberAxis getAtomsBCNumbers() {
        return atomsBCNumbers;
    }

    public void setAtomsBCNumbers(NumberAxis atomsBCNumbers) {
        this.atomsBCNumbers = atomsBCNumbers;
    }

    public BarChart<String, Number> getAtomsPerChainStructBarChart() {
        return atomsPerChainStructBarChart;
    }

    public void setAtomsPerChainStructBarChart(BarChart<String, Number> atomsPerChainStructBarChart) {
        this.atomsPerChainStructBarChart = atomsPerChainStructBarChart;
    }

    public CategoryAxis getAtomsPerChainStructBCCategories() {
        return atomsPerChainStructBCCategories;
    }

    public void setAtomsPerChainStructBCCategories(CategoryAxis atomsPerChainStructBCCategories) {
        this.atomsPerChainStructBCCategories = atomsPerChainStructBCCategories;
    }

    public NumberAxis getAtomsPerChainStructBCNumbers() {
        return atomsPerChainStructBCNumbers;
    }

    public void setAtomsPerChainStructBCNumbers(NumberAxis atomsPerChainStructBCNumbers) {
        this.atomsPerChainStructBCNumbers = atomsPerChainStructBCNumbers;
    }

    public HBox getButtonHBoxT() {
        return buttonHBoxT;
    }

    public void setButtonHBoxT(HBox buttonHBoxT) {
        this.buttonHBoxT = buttonHBoxT;
    }

    public Button getSarsCovButton() {
        return sarsCovButton;
    }

    public void setSarsCovButton(Button sarsCovButton) {
        this.sarsCovButton = sarsCovButton;
    }

    public Button getListAllButton() {
        return listAllButton;
    }

    public void setListAllButton(Button listAllButton) {
        this.listAllButton = listAllButton;
    }

    public Label getStatusLabelB() {
        return statusLabelB;
    }

    public void setStatusLabelB(Label statusLabelB) {
        this.statusLabelB = statusLabelB;
    }
    
    
}

