package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.client.Main;
import proj.markrone.library.client.gui.utils.ThemePreferences;
import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.client.gui.utils.ThemeToggleIcon;
import proj.markrone.library.api.network.ServerClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class LibrarianMenuController implements Initializable {
    private static final Logger logger = LogManager.getLogger(LibrarianMenuController.class);

    @FXML private Button booksButton;
    @FXML private Button arrivalsButton;
    @FXML private Button bookCopiesButton;
    @FXML private Button issuesButton;
    @FXML private Button exitButton;
    @FXML private Button editInfoButton;
    @FXML private Label helloLabel;
    @FXML private Button themeToggleButton;

    @FXML
    void onThemeToggle(ActionEvent event) {
        Main.themeName = "LIGHT".equals(Main.themeName) ? "DARK" : "LIGHT";
        ThemePreferences.saveTheme(Main.themeName);
        Loader.reloadScene(ScenePath.LIBRARIAN_MENU);
    }

    @FXML
    void onBooksButton(ActionEvent event) {
        Loader.loadScene((Stage) booksButton.getScene().getWindow(), ScenePath.BOOKS);
    }

    @FXML
    void onArrivalsButton(ActionEvent event) {
        Loader.loadScene((Stage) arrivalsButton.getScene().getWindow(), ScenePath.ARRIVALS);
    }

    @FXML
    void onBookCopiesButton(ActionEvent event) {
        Loader.loadScene((Stage) bookCopiesButton.getScene().getWindow(), ScenePath.BOOK_COPIES);
    }

    @FXML
    void onIssuesButton(ActionEvent event) {
        Loader.loadScene((Stage) issuesButton.getScene().getWindow(), ScenePath.ISSUES);
    }

    @FXML
    void onEditInfoButton(ActionEvent event) {
        Loader.loadScene((Stage) editInfoButton.getScene().getWindow(), ScenePath.EDIT_INFO);
    }

    @FXML
    void onExitButton(ActionEvent event) {
        try {
            ServerClient.getInstance().disconnect();
        } catch (Exception e) {
            logger.warn("Ошибка при отключении от сервера", e);
        }
        Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.LOGIN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            var currentUser = ServerClient.getCurrentUser();
            if (currentUser != null && currentUser.getPerson() != null && currentUser.getPerson().getFirstName() != null) {
                helloLabel.setText(String.format("Добро пожаловать, %s!", currentUser.getPerson().getFirstName()));
            } else {
                helloLabel.setText("Добро пожаловать!");
            }
        } catch (Exception e) {
            logger.error("Ошибка при инициализации приветственного сообщения", e);
            helloLabel.setText("Добро пожаловать!");
        }
        ThemeToggleIcon.applyToButton(themeToggleButton);
    }
}
