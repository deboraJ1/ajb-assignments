
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

public class WindowController {

    @FXML
    private CheckBox ballsCheckButton;

    @FXML
    private Button smallerButton;

    @FXML
    private Button largerButton;

    @FXML
    private CheckBox sticksCheckButton;

    @FXML
    private Label infoLabel;

    @FXML
    private Pane centerPane;

    @FXML
    private MenuItem closeMenuItem;

    public CheckBox getBallsCheckButton() {
        return ballsCheckButton;
    }

    public Button getSmallerButton() {
        return smallerButton;
    }

    public Button getLargerButton() {
        return largerButton;
    }

    public CheckBox getSticksCheckButton() {
        return sticksCheckButton;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public Pane getCenterPane() {
        return centerPane;
    }

    public MenuItem getCloseMenuItem() {
        return closeMenuItem;
    }
}
