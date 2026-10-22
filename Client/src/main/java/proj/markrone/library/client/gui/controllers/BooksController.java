package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.services.BookService;
import proj.markrone.library.client.gui.utils.*;
import proj.markrone.library.api.model.entities.Book;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Deserializer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;


public class BooksController implements Initializable {

    @FXML private Button backToMenuButton;
    @FXML private TableView<Book> booksTable;
//    @FXML private Button deleteButton;
//    @FXML private Button addButton;
//    @FXML private Button eraseButton;
//    @FXML private Button updateButton;
    @FXML private TableColumn<Book, Integer> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, Integer> yearColumn;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField yearField;
    @FXML private Button exportToExcelButton;
//    @FXML private Button exportToPDFButton;
//    @FXML private Button exportToJSONButton;
    @FXML private TextField searchField;

    private final BookService bookService = new BookService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("yearPublished"));
        loadTable();
        booksTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) fillFields();
            else eraseFields();
        });
    }

    private void loadTable() {
        booksTable.getItems().clear();
        Response response = bookService.getAll();
        if (response == null || !response.isSuccess()) {
            if (response != null) AlertUtil.error("Ошибка загрузки", response.getMessage());
            return;
        }
        List<Book> list = new Deserializer().extractListData(response.getData(), Book.class);
        if (list != null) booksTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void onAddButton(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        if (title == null || title.isBlank()) {
            AlertUtil.error("Ошибка", "Название книги не может быть пустым.");
            return;
        }
        if (author == null || author.isBlank()) {
            AlertUtil.error("Ошибка", "Укажите автора.");
            return;
        }
        Book book = new Book();
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setIsbn(isbnField.getText() != null && !isbnField.getText().isBlank() ? isbnField.getText().trim() : null);
        try {
            if (yearField.getText() != null && !yearField.getText().isBlank())
                book.setYearPublished(Integer.parseInt(yearField.getText().trim()));
        } catch (NumberFormatException e) {
            AlertUtil.error("Ошибка", "Год издания должен быть числом.");
            return;
        }
        Response response = bookService.addBook(book);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Успех", "Книга добавлена в каталог.");
            loadTable();
            eraseFields();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось добавить книгу.");
        }
    }

    @FXML
    void onDeleteButton(ActionEvent event) {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Выбор", "Выберите книгу в таблице.");
            return;
        }
        Response response = bookService.deleteBook(selected);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Удаление", response.getMessage());
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось удалить.");
        }
    }

    @FXML
    void onUpdateButton(ActionEvent event) {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Выбор", "Выберите книгу в таблице.");
            return;
        }
        String title = titleField.getText();
        String author = authorField.getText();
        if (title == null || title.isBlank() || author == null || author.isBlank()) {
            AlertUtil.error("Ошибка", "Название и автор не могут быть пустыми.");
            return;
        }
        selected.setTitle(title.trim());
        selected.setAuthor(author.trim());
        selected.setIsbn(isbnField.getText() != null && !isbnField.getText().isBlank() ? isbnField.getText().trim() : null);
        try {
            selected.setYearPublished(yearField.getText() != null && !yearField.getText().isBlank() ? Integer.parseInt(yearField.getText().trim()) : null);
        } catch (NumberFormatException e) {
            AlertUtil.error("Ошибка", "Год издания должен быть числом.");
            return;
        }
        Response response = bookService.updateBook(selected);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Обновление", response.getMessage());
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось обновить.");
        }
    }

    @FXML
    void onSearchAction(ActionEvent event) {
        String query = searchField.getText();
        if (query == null || query.isBlank()) {
            loadTable();
            return;
        }
        try {
            Response response = ServerClient.getInstance().sendRequest(
                    new Request(Operation.SEARCH_BOOKS, proj.markrone.library.api.serializer.Serializer.toJson(query.trim())));
            if (response != null && response.isSuccess()) {
                List<Book> results = new Deserializer().extractListData(response.getData(), Book.class);
                if (results != null) booksTable.setItems(FXCollections.observableArrayList(results));
            } else {
                AlertUtil.error("Ошибка поиска", response != null ? response.getMessage() : "Ошибка связи");
            }
        } catch (Exception e) {
            AlertUtil.error("Ошибка поиска", e.getMessage());
        }
    }

    @FXML
    void onEraseButton(ActionEvent event) {
        eraseFields();
    }

    @FXML
    void onBackToMenuButton(ActionEvent event) {
        try {
            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            String roleName = ServerClient.getCurrentUser() != null
                    ? ServerClient.getCurrentUser().getRole().getName() : "";
            switch (roleName) {
                case "Admin" -> Loader.loadScene(stage, ScenePath.ADMIN_MENU);
                case "Librarian" -> Loader.loadScene(stage, ScenePath.LIBRARIAN_MENU);
                default -> Loader.loadScene(stage, ScenePath.USER_MENU);
            }
        } catch (Exception e) {
            AlertUtil.error("Ошибка", e.getMessage());
        }
    }

    @FXML
    void onExportToExcelButton(ActionEvent event) {
        serverExport(Operation.EXPORT_BOOKS_EXCEL, "books.xlsx", "Excel файлы", "*.xlsx");
    }

    @FXML
    void onExportToPDFButton(ActionEvent event) {
        serverExport(Operation.EXPORT_BOOKS_PDF, "books.pdf", "PDF файлы", "*.pdf");
    }

    @FXML
    void onExportToJSONButton(ActionEvent event) {
        serverExport(Operation.EXPORT_BOOKS_JSON, "books.json", "JSON файлы", "*.json");
    }

    private void serverExport(Operation operation, String defaultName, String filterName, String filterExt) {
        try {
            Response response = ServerClient.getInstance().sendRequest(new Request(operation));
            if (response == null || !response.isSuccess()) {
                AlertUtil.error("Ошибка экспорта", response != null ? response.getMessage() : "Ошибка связи с сервером");
                return;
            }
            FileChooser fc = new FileChooser();
            fc.setTitle("Сохранить файл");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterName, filterExt));
            fc.setInitialFileName(defaultName);
            File file = fc.showSaveDialog(exportToExcelButton.getScene().getWindow());
            if (file == null) return;
            byte[] bytes = Base64.getDecoder().decode(response.getData());
            try (OutputStream out = new FileOutputStream(file)) {
                out.write(bytes);
            }
            AlertUtil.info("Экспорт", "Файл сохранён: " + file.getAbsolutePath());
        } catch (Exception e) {
            AlertUtil.error("Ошибка экспорта", e.getMessage());
        }
    }

    private void fillFields() {
        Book b = booksTable.getSelectionModel().getSelectedItem();
        if (b == null) return;
        titleField.setText(b.getTitle());
        authorField.setText(b.getAuthor());
        isbnField.setText(b.getIsbn() != null ? b.getIsbn() : "");
        yearField.setText(b.getYearPublished() != null ? String.valueOf(b.getYearPublished()) : "");
    }

    private void eraseFields() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        yearField.setText("");
    }

}
