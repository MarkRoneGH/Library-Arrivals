package proj.markrone.library.client.gui.utils;

import proj.markrone.library.api.exceptions.NoConnectionException;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.client.Main;
import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.enums.ThemesPath;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Loader {
    private static final Logger logger = LogManager.getLogger(Loader.class);

    private static final ExecutorService CONNECTION_PROBE_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "library-connection-probe");
        t.setDaemon(true);
        return t;
    });

    private static Timeline connectionStatusTimeline;

    /**
     * Загружает сцену из указанного пути с выбросом исключения в случае ошибки.
     *
     * @param stage     основная сцена приложения
     * @param scenePath путь к FXML-файлу
     * @throws IOException если произошла ошибка при загрузке сцены
     */
    public static void loadSceneWithThrowException(Stage stage, ScenePath scenePath) throws IOException {
        logger.info("Загрузка сцены из пути: {}", scenePath.getPathToFxml());

        if (connectionStatusTimeline != null) {
            connectionStatusTimeline.stop();
            connectionStatusTimeline = null;
        }

        URL stagePathURL = Loader.class.getResource(scenePath.getPathToFxml());
        FXMLLoader loader = new FXMLLoader(stagePathURL);
        Parent loadedRoot = loader.load();

        if (scenePath != ScenePath.LOGIN && scenePath != ScenePath.REGISTRATION) {
            loadedRoot.getStyleClass().add("app-work-area");
        }

        Parent finalRoot = wrapWithConnectionStatusIfNeeded(loadedRoot, scenePath);

        stage.setResizable(true);

        var vb = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(finalRoot, vb.getWidth(), vb.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(Loader.class.getResource(String.valueOf(ThemesPath.valueOf(Main.themeName).getPathToCss()))).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Loader.class.getResource("/styles/styles.css")).toExternalForm());
        stage.setScene(scene);

        if (finalRoot instanceof Region region) {
            region.prefWidthProperty().bind(scene.widthProperty());
            region.prefHeightProperty().bind(scene.heightProperty());
        }

        applyTableCardClips(finalRoot);

        Platform.runLater(() -> applyTableCardClips(finalRoot));

        finalRoot.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(180), finalRoot);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        stage.setMaximized(true);
        Platform.runLater(() -> stage.setMaximized(true));

        logger.info("Сцена успешно загружена: {}", scenePath.getPathToFxml());
    }

    private static Parent wrapWithConnectionStatusIfNeeded(Parent loadedRoot, ScenePath scenePath) {
        if (scenePath == ScenePath.LOGIN || scenePath == ScenePath.REGISTRATION) {
            return loadedRoot;
        }

        BorderPane shell = new BorderPane();
        shell.setCenter(loadedRoot);
        if (loadedRoot instanceof Region r) {
            BorderPane.setAlignment(loadedRoot, Pos.CENTER);
            r.setMaxWidth(Double.MAX_VALUE);
            r.setMaxHeight(Double.MAX_VALUE);
        }

        HBox statusBar = new HBox(8);
        statusBar.setAlignment(Pos.CENTER_RIGHT);
        statusBar.setPadding(new Insets(6, 20, 10, 20));
        statusBar.getStyleClass().add("connection-status-bar");

        Circle dot = new Circle(4);
        dot.getStyleClass().add("status-dot-off");

        Label statusLabel = new Label("Нет подключения");
        statusLabel.getStyleClass().add("label");
        statusBar.getChildren().addAll(dot, statusLabel);

        shell.setBottom(statusBar);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5),
                e -> scheduleConnectionStatusProbe(dot, statusLabel)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        connectionStatusTimeline = timeline;

        scheduleConnectionStatusProbe(dot, statusLabel);

        return shell;
    }


    private static void scheduleConnectionStatusProbe(Circle dot, Label statusLabel) {
        CONNECTION_PROBE_EXECUTOR.execute(() -> {
            boolean ok = false;
            try {
                ServerClient client = ServerClient.getInstance();
                ok = client.isConnected() && client.probeServerAlive();
            } catch (NoConnectionException e) {
                ok = false;
            } catch (Exception e) {
                ok = false;
            }
            boolean connected = ok;
            Platform.runLater(() -> applyConnectionStatusUi(dot, statusLabel, connected));
        });
    }

    private static void applyConnectionStatusUi(Circle dot, Label statusLabel, boolean ok) {
        dot.getStyleClass().removeAll("status-dot-on", "status-dot-off");
        if (ok) {
            dot.getStyleClass().add("status-dot-on");
            statusLabel.setText("Подключено");
        } else {
            dot.getStyleClass().add("status-dot-off");
            statusLabel.setText("Нет подключения");
        }
    }

    private static void applyTableCardClips(Parent root) {
        for (Node node : root.lookupAll(".table-card")) {
            if (node instanceof Region region) {
                Rectangle clip = new Rectangle();
                clip.setArcWidth(24);
                clip.setArcHeight(24);
                clip.widthProperty().bind(region.widthProperty());
                clip.heightProperty().bind(region.heightProperty());
                region.setClip(clip);
            }
        }
    }

    /**
     * Загружает сцену из указанного пути и обрабатывает возможные ошибки.
     *
     * @param stage     основная сцена приложения
     * @param scenePath путь к FXML-файлу
     */
    public static void loadScene(Stage stage, ScenePath scenePath) {
        try {
            loadSceneWithThrowException(stage, scenePath);
        } catch (IOException e) {
            logger.error("Ошибка при загрузке сцены: {}", scenePath.getPathToFxml(), e);
            AlertUtil.error("Ошибка навигации", "Не удалось перейти к сцене.");
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при загрузке сцены: {}", scenePath.getPathToFxml(), e);
            AlertUtil.error("Неожиданная ошибка", "Произошла ошибка при загрузке сцены.");
        }
    }


    public static void reloadScene(ScenePath scenePath) {
        loadScene(Main.getPrimaryStage(), scenePath);
    }
}
