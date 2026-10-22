package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.services.BookCopyService;
import proj.markrone.library.client.gui.services.IssueService;
import proj.markrone.library.client.gui.services.UserService;
import proj.markrone.library.client.gui.utils.AlertUtil;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.model.entities.BookCopy;
import proj.markrone.library.api.model.entities.Issue;
import proj.markrone.library.api.model.entities.User;
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
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;


public class IssuesController implements Initializable {

    @FXML private Button backToMenuButton;
    @FXML private TableView<Issue> issuesTable;
//    @FXML private Button refreshButton;
//    @FXML private Button returnButton;
    @FXML private Button issueButton;
    @FXML private Label titleLabel;
    @FXML private Label issueFormLabel;
    @FXML private TableColumn<Issue, Integer> idColumn;
    @FXML private TableColumn<Issue, String> bookTitleColumn;
    @FXML private TableColumn<Issue, String> invColumn;
    @FXML private TableColumn<Issue, String> readerColumn;
    @FXML private TableColumn<Issue, String> dueColumn;
    @FXML private TableColumn<Issue, String> returnedColumn;
    @FXML private ComboBox<BookCopy> copyComboBox;
    @FXML private ComboBox<User> readerComboBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> issueFilterComboBox;

    private final IssueService issueService = new IssueService();
    private final BookCopyService bookCopyService = new BookCopyService();
    private final UserService userService = new UserService();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleColumn.setCellValueFactory(cb -> {
            Issue i = cb.getValue();
            if (i == null || i.getBookCopy() == null || i.getBookCopy().getBook() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty(i.getBookCopy().getBook().getTitle());
        });
        invColumn.setCellValueFactory(cb -> {
            Issue i = cb.getValue();
            if (i == null || i.getBookCopy() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty(i.getBookCopy().getInventoryNumber());
        });
        readerColumn.setCellValueFactory(cb -> {
            Issue i = cb.getValue();
            if (i == null || i.getReader() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty(i.getReader().getUsername());
        });
        dueColumn.setCellValueFactory(cb -> {
            Issue i = cb.getValue();
            if (i == null || i.getDueDate() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty(i.getDueDate().format(DATE_FORMAT));
        });
        returnedColumn.setCellValueFactory(cb -> {
            Issue i = cb.getValue();
            if (i == null || i.getReturnedAt() == null) return new javafx.beans.property.SimpleStringProperty("—");
            return new javafx.beans.property.SimpleStringProperty(i.getReturnedAt().format(DATETIME_FORMAT));
        });

        if (issueFilterComboBox != null) {
            issueFilterComboBox.setItems(FXCollections.observableArrayList(
                    "Все", "Активные", "Просроченные", "Возвращённые"));
            issueFilterComboBox.setValue("Все");
        }

        boolean isAdmin = ServerClient.getCurrentUser() != null && "Admin".equals(ServerClient.getCurrentUser().getRole().getName());
        boolean isLibrarian = ServerClient.getCurrentUser() != null && "Librarian".equals(ServerClient.getCurrentUser().getRole().getName());
        boolean canIssue = isAdmin || isLibrarian;

        if (canIssue) {
            setupCopyComboBox();
            setupReaderComboBox();
            dueDatePicker.setValue(LocalDate.now().plusDays(14));
        } else {
            if (titleLabel != null) titleLabel.setText("Мои выдачи");
            if (issueFormLabel != null) { issueFormLabel.setVisible(false); issueFormLabel.setManaged(false); }
            if (issueButton != null) { issueButton.setVisible(false); issueButton.setManaged(false); }
            if (copyComboBox != null) { copyComboBox.setVisible(false); copyComboBox.setManaged(false); }
            if (readerComboBox != null) { readerComboBox.setVisible(false); readerComboBox.setManaged(false); }
            if (dueDatePicker != null) { dueDatePicker.setVisible(false); dueDatePicker.setManaged(false); }
        }

        loadTable();
    }

    private void setupCopyComboBox() {
        copyComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(BookCopy c) {
                if (c == null) return "";
                String bookTitle = c.getBook() != null ? c.getBook().getTitle() : "?";
                return c.getInventoryNumber() + " — " + bookTitle;
            }
            @Override
            public BookCopy fromString(String s) { return null; }
        });
        Response resp = bookCopyService.getAvailableCopies();
        if (resp != null && resp.isSuccess()) {
            List<BookCopy> copies = new Deserializer().extractListData(resp.getData(), BookCopy.class);
            if (copies != null) copyComboBox.setItems(FXCollections.observableArrayList(copies));
        }
    }

    private void setupReaderComboBox() {
        readerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User u) {
                if (u == null) return "";
                String name = u.getPerson() != null
                        ? u.getPerson().getLastName() + " " + u.getPerson().getFirstName()
                        : u.getUsername();
                return name + " (" + u.getUsername() + ")";
            }
            @Override
            public User fromString(String s) { return null; }
        });
        Response resp = userService.getAllReaders();
        if (resp != null && resp.isSuccess()) {
            List<User> readers = new Deserializer().extractListData(resp.getData(), User.class);
            if (readers != null) readerComboBox.setItems(FXCollections.observableArrayList(readers));
        }
    }

    private void loadTable() {
        issuesTable.getItems().clear();
        Response response;
        if (ServerClient.getCurrentUser() != null
                && ServerClient.getCurrentUser().getRole() != null
                && "Reader".equals(ServerClient.getCurrentUser().getRole().getName())) {
            response = issueService.getIssuesByReader(ServerClient.getCurrentUser().getId());
        } else {
            response = issueService.getAllIssues();
        }
        if (response == null || !response.isSuccess()) {
            if (response != null) AlertUtil.error("Ошибка загрузки", response.getMessage());
            return;
        }
        List<Issue> list = new Deserializer().extractListData(response.getData(), Issue.class);
        if (list != null) issuesTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void onIssueFilter(ActionEvent event) {
        String filter = issueFilterComboBox != null ? issueFilterComboBox.getValue() : "Все";
        if (filter == null || "Все".equals(filter)) {
            loadTable();
            return;
        }

        boolean isReader = ServerClient.getCurrentUser() != null
                && "Reader".equals(ServerClient.getCurrentUser().getRole().getName());

        if (isReader) {
            loadTable();
            List<Issue> all = new java.util.ArrayList<>(issuesTable.getItems());
            List<Issue> filtered = switch (filter) {
                case "Активные" -> all.stream().filter(i -> i.getReturnedAt() == null).toList();
                case "Просроченные" -> all.stream().filter(i -> i.getReturnedAt() == null
                        && i.getDueDate() != null && i.getDueDate().isBefore(LocalDate.now())).toList();
                case "Возвращённые" -> all.stream().filter(i -> i.getReturnedAt() != null).toList();
                default -> all;
            };
            issuesTable.setItems(FXCollections.observableArrayList(filtered));
        } else {
            try {
                Response response = switch (filter) {
                    case "Активные" -> ServerClient.getInstance().sendRequest(new Request(Operation.GET_ACTIVE_ISSUES));
                    case "Просроченные" -> ServerClient.getInstance().sendRequest(new Request(Operation.GET_OVERDUE_ISSUES));
                    case "Возвращённые" -> ServerClient.getInstance().sendRequest(new Request(Operation.GET_RETURNED_ISSUES));
                    default -> null;
                };
                if (response != null && response.isSuccess()) {
                    List<Issue> list = new Deserializer().extractListData(response.getData(), Issue.class);
                    if (list != null) issuesTable.setItems(FXCollections.observableArrayList(list));
                }
            } catch (Exception e) {
                AlertUtil.error("Ошибка фильтрации", e.getMessage());
            }
        }
    }

    @FXML
    void onRefreshButton(ActionEvent event) {
        if (issueFilterComboBox != null) issueFilterComboBox.setValue("Все");
        loadTable();
        boolean canIssue = ServerClient.getCurrentUser() != null
                && ("Admin".equals(ServerClient.getCurrentUser().getRole().getName())
                || "Librarian".equals(ServerClient.getCurrentUser().getRole().getName()));
        if (canIssue) setupCopyComboBox();
    }

    @FXML
    void onReturnButton(ActionEvent event) {
        Issue selected = issuesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Выбор", "Выберите выдачу в таблице.");
            return;
        }
        if (selected.getReturnedAt() != null) {
            AlertUtil.warning("Возврат", "Книга уже возвращена.");
            return;
        }
        Response response = issueService.returnBook(selected.getId());
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Возврат", "Книга принята обратно.");
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось принять книгу.");
        }
    }

    @FXML
    void onIssueButton(ActionEvent event) {
        BookCopy selectedCopy = copyComboBox.getValue();
        User selectedReader = readerComboBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (selectedCopy == null || selectedReader == null || dueDate == null) {
            AlertUtil.warning("Ввод", "Выберите экземпляр, читателя и укажите срок возврата.");
            return;
        }
        if (!dueDate.isAfter(LocalDate.now())) {
            AlertUtil.warning("Дата", "Срок возврата должен быть в будущем.");
            return;
        }

        int loanDays = (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        Response response = issueService.issueBook(selectedCopy.getId(), selectedReader.getId(), loanDays);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Выдача", "Книга выдана читателю.");
            loadTable();
            setupCopyComboBox();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось выдать книгу.");
        }
    }

    @FXML
    void onBackToMenuButton(ActionEvent event) {
        try {
            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            String roleName = ServerClient.getCurrentUser() != null ? ServerClient.getCurrentUser().getRole().getName() : "";
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
