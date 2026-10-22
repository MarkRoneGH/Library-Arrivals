package proj.markrone.library.client.gui.enums;

import lombok.Getter;

@Getter
public enum ScenePath {

    LOGIN("/views/login.fxml"),


    REGISTRATION("/views/register.fxml"),


    USER_MENU("/views/usermenu.fxml"),


    ADMIN_MENU("/views/adminmenu.fxml"),


    USERS("/views/users.fxml"),


    BOOKS("/views/books.fxml"),


    ARRIVALS("/views/arrivals.fxml"),


    ISSUES("/views/issues.fxml"),


    LIBRARIAN_MENU("/views/librarianmenu.fxml"),


    BOOK_COPIES("/views/bookcopies.fxml"),


    EDIT_INFO("/views/editinfo.fxml");

    private final String pathToFxml;

    /**
     * Конструктор перечисления.
     *
     * @param pathToFxml путь к FXML-файлу сцены
     * @throws IllegalArgumentException если путь к FXML-файлу пустой или null
     */
    ScenePath(String pathToFxml) {
        if (pathToFxml == null || pathToFxml.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к FXML-файлу не может быть пустым или null");
        }
        this.pathToFxml = pathToFxml;
    }

}