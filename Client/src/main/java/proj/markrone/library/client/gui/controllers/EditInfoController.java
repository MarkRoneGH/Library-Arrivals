package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.services.UserService;
import proj.markrone.library.client.gui.utils.AlertUtil;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.api.model.entities.User;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class EditInfoController implements Initializable {
    private static final Logger logger = LogManager.getLogger(EditInfoController.class);

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button editButton;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField patronymicField;

    @FXML
    private TextField usernameField;

    private User currentUser;


    @FXML
    void onBackToMenuButton(ActionEvent event) {
        String roleName = ServerClient.getCurrentUser().getRole().getName();
        Stage stage = (Stage) backToMenuButton.getScene().getWindow();
        switch (roleName) {
            case "Admin" -> Loader.loadScene(stage, ScenePath.ADMIN_MENU);
            case "Librarian" -> Loader.loadScene(stage, ScenePath.LIBRARIAN_MENU);
            default -> Loader.loadScene(stage, ScenePath.USER_MENU);
        }
    }


    @FXML
    void onEditButton(ActionEvent event) {
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            AlertUtil.error("Ошибка обновления", "Пароли не совпадают.");
            logger.warn("Попытка обновления с несовпадающими паролями");
            return;
        }

        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String patronymic = patronymicField.getText().trim();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || patronymic.isEmpty()) {
            AlertUtil.warning("Ошибка ввода", "Заполните все поля (логин, имя, фамилия, отчество).");
            return;
        }

        currentUser.setUsername(username);

        if (newPassword != null && !newPassword.isEmpty()) {
            currentUser.setPasswordHash(newPassword);
            logger.info("Пароль пользователя ID {} обновлен", currentUser.getId());
        }

        currentUser.getPerson().setFirstName(firstName);
        currentUser.getPerson().setLastName(lastName);
        currentUser.getPerson().setPatronymic(patronymic);

        UserService userService = new UserService();
        Response response = userService.updateUser(currentUser);

        if (response.isSuccess()) {
            AlertUtil.info("Редактирование", response.getMessage());
            logger.info("Данные пользователя ID {} успешно обновлены", currentUser.getId());
            ServerClient.setCurrentUser(null);
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.LOGIN);
        } else {
            AlertUtil.error("Ошибка обновления", response.getMessage());
            logger.error("Ошибка обновления данных пользователя ID {}: {}", currentUser.getId(), response.getMessage());
        }
    }

    /**
     * Инициализация контроллера: загрузка текущих данных пользователя в поля ввода.
     *
     * @param url URL ресурса
     * @param resourceBundle пакет ресурсов
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = ServerClient.getCurrentUser();

        firstNameField.setText(currentUser.getPerson().getFirstName());
        lastNameField.setText(currentUser.getPerson().getLastName());
        patronymicField.setText(currentUser.getPerson().getPatronymic());
        usernameField.setText(currentUser.getUsername());
        passwordField.clear();
        confirmPasswordField.clear();
        passwordField.setPromptText("Введите новый пароль");
        confirmPasswordField.setPromptText("Повторите новый пароль");

        logger.info("Инициализирован EditInfoController для пользователя ID {}", currentUser.getId());
    }
}
