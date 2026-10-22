package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.services.UserService;
import proj.markrone.library.client.gui.utils.AlertUtil;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.api.model.entities.User;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Deserializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;


@SuppressWarnings("unused")
public class LoginController implements Initializable {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button regButton;

    @FXML
    private TextField usernameField;

    private UserService userService;


    @FXML
    void onLoginButton(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.warning("Внимание", "Пожалуйста, заполните все поля.");
            return;
        }

        Response response = userService.login(username, password);

        if (response.isSuccess()) {
            User loggedUser = new Deserializer().extractData(response.getData(), User.class);
            ServerClient.setCurrentUser(loggedUser);
            logger.info("Пользователь '{}' успешно вошел в систему с ролью '{}'", username, loggedUser.getRole().getId());

            String roleName = loggedUser.getRole().getName();
            switch (roleName) {
                case "Admin" -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
                case "Librarian" -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.LIBRARIAN_MENU);
                case "Reader" -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.USER_MENU);
                case "Waiting" -> {
                    AlertUtil.info("Ожидание одобрения", "Ваш аккаунт ожидает одобрения администратором.");
                    logger.warn("Пользователь '{}' не одобрен администратором", username);
                }
                default -> {
                    logger.error("Неизвестная роль пользователя '{}': {}", username, roleName);
                    throw new IllegalStateException("Unexpected role: " + roleName);
                }
            }
        } else {
            AlertUtil.error("Ошибка входа", response.getMessage());
            logger.warn("Неудачная попытка входа пользователя '{}': {}", username, response.getMessage());
        }
    }


    @FXML
    void onRegButton(ActionEvent event) {
        Loader.loadScene((Stage) regButton.getScene().getWindow(), ScenePath.REGISTRATION);
        logger.info("Переход на экран регистрации");
    }

    /**
     * Инициализация контроллера: создание сервиса пользователя.
     *
     * @param url            URL ресурса
     * @param resourceBundle пакет ресурсов
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService();
        logger.info("Инициализация LoginController завершена");
    }
}
