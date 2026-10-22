package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.services.ArrivalService;
import proj.markrone.library.client.gui.services.BookCopyService;
import proj.markrone.library.client.gui.services.BookService;
import proj.markrone.library.client.gui.utils.AlertUtil;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.model.entities.Arrival;
import proj.markrone.library.api.model.entities.Book;
import proj.markrone.library.api.model.entities.BookCopy;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Deserializer;
import proj.markrone.library.api.serializer.Serializer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class BookCopiesController implements Initializable {
    private static final Logger logger = LogManager.getLogger(BookCopiesController.class);

    @FXML private Button backToMenuButton;
//    @FXML private Button registerButton;
//    @FXML private Button includeInFundButton;
//    @FXML private Button deleteButton;
//    @FXML private Button writeOffButton;
//    @FXML private Button relocateButton;
//    @FXML private Button refreshButton;
//    @FXML private Button eraseButton;
    @FXML private TableView<BookCopy> copiesTable;
    @FXML private TableColumn<BookCopy, Integer> idColumn;
    @FXML private TableColumn<BookCopy, String> bookColumn;
    @FXML private TableColumn<BookCopy, String> arrivalColumn;
    @FXML private TableColumn<BookCopy, String> invColumn;
    @FXML private TableColumn<BookCopy, String> shelfColumn;
    @FXML private TableColumn<BookCopy, String> statusColumn;
    @FXML private ComboBox<Book> bookComboBox;
    @FXML private ComboBox<Arrival> arrivalComboBox;
    @FXML private TextField inventoryField;
    @FXML private TextField shelfField;
    @FXML private ComboBox<String> statusFilterComboBox;

    private final BookCopyService bookCopyService = new BookCopyService();
    private final BookService bookService = new BookService();
    private final ArrivalService arrivalService = new ArrivalService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        bookColumn.setCellValueFactory(cb -> {
            BookCopy c = cb.getValue();
            if (c == null || c.getBook() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty(c.getBook().getTitle());
        });
        arrivalColumn.setCellValueFactory(cb -> {
            BookCopy c = cb.getValue();
            if (c == null || c.getArrival() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty("ID " + c.getArrival().getId()
                    + (c.getArrival().getNotes() != null ? " — " + c.getArrival().getNotes() : ""));
        });
        invColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("inventoryNumber"));
        shelfColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("shelfLocation"));
        statusColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        if (statusFilterComboBox != null) {
            statusFilterComboBox.setItems(FXCollections.observableArrayList(
                    "Все", "RECEIVED", "IN_FUND", "ISSUED", "WRITTEN_OFF"));
            statusFilterComboBox.setValue("Все");
        }
        setupBookComboBox();
        setupArrivalComboBox();
        loadTable();
    }

    private void setupBookComboBox() {
        bookComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Book book) {
                return book == null ? "" : book.getTitle() + " (" + book.getAuthor() + ")";
            }
            @Override
            public Book fromString(String s) { return null; }
        });
        Response resp = bookService.getAll();
        if (resp != null && resp.isSuccess()) {
            List<Book> books = new Deserializer().extractListData(resp.getData(), Book.class);
            if (books != null) bookComboBox.setItems(FXCollections.observableArrayList(books));
        }
    }

    private void setupArrivalComboBox() {
        arrivalComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Arrival a) {
                return a == null ? "" : "ID " + a.getId() + " — " + a.getArrivalDate()
                        + (a.getNotes() != null ? " (" + a.getNotes() + ")" : "");
            }
            @Override
            public Arrival fromString(String s) { return null; }
        });
        Response resp = arrivalService.getAll();
        if (resp != null && resp.isSuccess()) {
            List<Arrival> arrivals = new Deserializer().extractListData(resp.getData(), Arrival.class);
            if (arrivals != null) arrivalComboBox.setItems(FXCollections.observableArrayList(arrivals));
        }
    }

    private void loadTable() {
        copiesTable.getItems().clear();
        Response response = bookCopyService.getAllBookCopies();
        if (response == null || !response.isSuccess()) {
            if (response != null) AlertUtil.error("Ошибка загрузки", response.getMessage());
            return;
        }
        List<BookCopy> list = new Deserializer().extractListData(response.getData(), BookCopy.class);
        if (list != null) copiesTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void onRegisterButton(ActionEvent event) {
        Book selectedBook = bookComboBox.getValue();
        Arrival selectedArrival = arrivalComboBox.getValue();
        String inv = inventoryField.getText();
        String shelf = shelfField.getText();

        if (selectedBook == null || selectedArrival == null || inv == null || inv.isBlank()) {
            AlertUtil.warning("Ввод", "Выберите книгу, поступление и укажите инвентарный номер.");
            return;
        }

        BookCopy copy = new BookCopy();
        copy.setBook(selectedBook);
        copy.setArrival(selectedArrival);
        copy.setInventoryNumber(inv.trim());
        copy.setShelfLocation(shelf != null && !shelf.isBlank() ? shelf.trim() : null);
        copy.setStatus("RECEIVED");

        Response response = bookCopyService.registerCopy(copy);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Успех", "Экземпляр зарегистрирован.");
            loadTable();
            eraseFields();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось зарегистрировать.");
        }
    }

    @FXML
    void onDeleteButton(ActionEvent event) {
        BookCopy selected = copiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.warning("Выбор", "Выберите экземпляр в таблице."); return; }
        Response response = bookCopyService.deleteCopy(selected.getId());
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Удаление", "Экземпляр удалён.");
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось удалить экземпляр.");
        }
    }

    @FXML
    void onIncludeInFundButton(ActionEvent event) {
        BookCopy selected = copiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.warning("Выбор", "Выберите экземпляр в таблице."); return; }
        Response response = bookCopyService.includeInFund(selected.getId());
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Фонд", "Экземпляр включён в фонд.");
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось включить в фонд.");
        }
    }

    @FXML
    void onWriteOffButton(ActionEvent event) {
        BookCopy selected = copiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.warning("Выбор", "Выберите экземпляр в таблице."); return; }
        Response response = bookCopyService.writeOffCopy(selected.getId());
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Списание", "Экземпляр списан.");
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось списать.");
        }
    }

    @FXML
    void onRelocateButton(ActionEvent event) {
        BookCopy selected = copiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertUtil.warning("Выбор", "Выберите экземпляр в таблице."); return; }
        String newShelf = shelfField.getText();
        if (newShelf == null || newShelf.isBlank()) {
            AlertUtil.warning("Ввод", "Укажите новую полку в поле «Полка».");
            return;
        }
        Response response = bookCopyService.relocateCopy(selected.getId(), newShelf.trim());
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Перемещение", "Экземпляр перемещён.");
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось переместить.");
        }
    }

    @FXML
    void onStatusFilter(ActionEvent event) {
        String status = statusFilterComboBox.getValue();
        if (status == null || "Все".equals(status)) {
            loadTable();
            return;
        }
        try {
            Response response = ServerClient.getInstance().sendRequest(
                    new Request(Operation.GET_COPIES_BY_STATUS, Serializer.toJson(status)));
            if (response != null && response.isSuccess()) {
                List<BookCopy> list = new Deserializer().extractListData(response.getData(), BookCopy.class);
                if (list != null) copiesTable.setItems(FXCollections.observableArrayList(list));
            }
        } catch (Exception e) {
            AlertUtil.error("Ошибка фильтрации", e.getMessage());
        }
    }

    @FXML
    void onRefreshButton(ActionEvent event) {
        if (statusFilterComboBox != null) statusFilterComboBox.setValue("Все");
        loadTable();
    }

    @FXML
    void onEraseButton(ActionEvent event) {
        eraseFields();
    }

    private void eraseFields() {
        bookComboBox.setValue(null);
        arrivalComboBox.setValue(null);
        inventoryField.setText("");
        shelfField.setText("");
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
}
