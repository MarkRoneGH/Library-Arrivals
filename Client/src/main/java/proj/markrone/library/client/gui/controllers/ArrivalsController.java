package proj.markrone.library.client.gui.controllers;

import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.services.ArrivalService;
import proj.markrone.library.client.gui.utils.AlertUtil;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.api.model.entities.Arrival;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.serializer.Deserializer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


public class ArrivalsController implements Initializable {

    @FXML private Button backToMenuButton;
    @FXML private TableView<Arrival> arrivalsTable;
    @FXML private Button deleteButton;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button eraseButton;
    @FXML private TableColumn<Arrival, Integer> idColumn;
    @FXML private TableColumn<Arrival, LocalDate> dateColumn;
    @FXML private TableColumn<Arrival, String> notesColumn;
    @FXML private TextField notesField;

    private final ArrivalService arrivalService = new ArrivalService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        loadTable();
        arrivalsTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) notesField.setText(nv.getNotes() != null ? nv.getNotes() : "");
            else notesField.setText("");
        });
    }

    private void loadTable() {
        arrivalsTable.getItems().clear();
        Response response = arrivalService.getAll();
        if (response == null || !response.isSuccess()) {
            if (response != null) AlertUtil.error("Ошибка загрузки", response.getMessage());
            return;
        }
        List<Arrival> list = new Deserializer().extractListData(response.getData(), Arrival.class);
        if (list != null) arrivalsTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void onAddButton(ActionEvent event) {
        Arrival arrival = new Arrival();
        arrival.setArrivalDate(LocalDate.now());
        arrival.setNotes(notesField.getText() != null ? notesField.getText().trim() : null);
        Response response = arrivalService.addArrival(arrival);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Успех", "Поступление зарегистрировано.");
            loadTable();
            notesField.setText("");
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось зарегистрировать поступление.");
        }
    }

    @FXML
    void onUpdateButton(ActionEvent event) {
        Arrival selected = arrivalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Выбор", "Выберите поступление в таблице.");
            return;
        }
        selected.setNotes(notesField.getText() != null ? notesField.getText().trim() : null);
        Response response = arrivalService.updateArrival(selected);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Обновление", "Поступление обновлено.");
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось обновить.");
        }
    }

    @FXML
    void onDeleteButton(ActionEvent event) {
        Arrival selected = arrivalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Выбор", "Выберите поступление в таблице.");
            return;
        }
        Response response = arrivalService.deleteArrival(selected);
        if (response != null && response.isSuccess()) {
            AlertUtil.info("Удаление", response.getMessage());
            loadTable();
        } else {
            AlertUtil.error("Ошибка", response != null ? response.getMessage() : "Не удалось удалить.");
        }
    }

    @FXML
    void onEraseButton(ActionEvent event) {
        notesField.setText("");
    }

    @FXML
    void onBackToMenuButton(ActionEvent event) {
        try {
            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            String roleName = proj.markrone.library.api.network.ServerClient.getCurrentUser() != null
                    ? proj.markrone.library.api.network.ServerClient.getCurrentUser().getRole().getName() : "";
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
