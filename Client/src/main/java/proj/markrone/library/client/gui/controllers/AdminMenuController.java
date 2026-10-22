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
public class AdminMenuController implements Initializable {
    private static final Logger logger = LogManager.getLogger(AdminMenuController.class);

    @FXML
    public Button arrivalsButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button booksButton;

    @FXML
    private Button bookCopiesButton;

    @FXML
    private Button issuesButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

    @FXML
    private Label helloLabel;

    @FXML
    private Button themeToggleButton;

    @FXML
    void onThemeToggle(ActionEvent event) {
        Main.themeName = "LIGHT".equals(Main.themeName) ? "DARK" : "LIGHT";
        ThemePreferences.saveTheme(Main.themeName);
        Loader.reloadScene(ScenePath.ADMIN_MENU);
    }

    /**
     * Обрабатывает нажатие кнопки перехода к списку пользователей.
     *
     * @param event событие нажатия
     */
    @FXML
    void onUsersButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) usersButton.getScene().getWindow(), ScenePath.USERS);
            logger.info("Переход к сцене пользователей");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене пользователей", e);
            throw new RuntimeException("Не удалось загрузить сцену пользователей", e);
        }
    }

    @FXML
    void onBooksButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) booksButton.getScene().getWindow(), ScenePath.BOOKS);
            logger.info("Переход к каталогу книг");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к каталогу книг", e);
            throw new RuntimeException("Не удалось загрузить сцену каталога книг", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к поступлениям.
     */
    @FXML
    void onArrivalsButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) arrivalsButton.getScene().getWindow(), ScenePath.ARRIVALS);
            logger.info("Переход к поступлениям");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к поступлениям", e);
            throw new RuntimeException("Не удалось загрузить сцену поступлений", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к экземплярам книг.
     */
    @FXML
    void onBookCopiesButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) bookCopiesButton.getScene().getWindow(), ScenePath.BOOK_COPIES);
            logger.info("Переход к экземплярам книг");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к экземплярам книг", e);
            throw new RuntimeException("Не удалось загрузить сцену экземпляров книг", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к выдаче и возврату книг.
     */
    @FXML
    void onIssuesButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) issuesButton.getScene().getWindow(), ScenePath.ISSUES);
            logger.info("Переход к выдаче и возврату книг");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к выдаче книг", e);
            throw new RuntimeException("Не удалось загрузить сцену выдачи книг", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки выхода в окно авторизации.
     *
     * @param event событие нажатия
     */
    @FXML
    void onExitButton(ActionEvent event) {
        try {
            ServerClient.getInstance().disconnect();
        } catch (Exception e) {
            logger.warn("Ошибка при отключении от сервера", e);
        }
        try {
            Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.LOGIN);
            logger.info("Переход к сцене авторизации");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене авторизации", e);
            throw new RuntimeException("Не удалось загрузить сцену авторизации", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к редактированию информации пользователя.
     *
     * @param event событие нажатия
     */
    @FXML
    void onEditInfoButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) editInfoButton.getScene().getWindow(), ScenePath.EDIT_INFO);
            logger.info("Переход к сцене редактирования информации");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене редактирования информации", e);
            throw new RuntimeException("Не удалось загрузить сцену редактирования информации", e);
        }
    }

    /**
     * Инициализация контроллера.
     * Устанавливает приветственное сообщение с именем текущего пользователя.
     *
     * @param url            URL, использованный для загрузки FXML
     * @param resourceBundle ресурсы локализации
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            var currentUser = ServerClient.getCurrentUser();
            if (currentUser == null || currentUser.getPerson() == null || currentUser.getPerson().getFirstName() == null) {
                logger.warn("Данные текущего пользователя недоступны");
                helloLabel.setText("Добро пожаловать!");
            } else {
                helloLabel.setText(String.format("Добро пожаловать, %s!", currentUser.getPerson().getFirstName()));
                logger.info("Установлено приветственное сообщение для пользователя {}", currentUser.getPerson().getFirstName());
            }
        } catch (Exception e) {
            logger.error("Ошибка при инициализации приветственного сообщения", e);
            helloLabel.setText("Добро пожаловать!");
        }
        ThemeToggleIcon.applyToButton(themeToggleButton);
    }
}
