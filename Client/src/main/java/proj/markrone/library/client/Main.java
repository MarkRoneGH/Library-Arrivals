package proj.markrone.library.client;

import proj.markrone.library.client.gui.utils.ThemePreferences;
import proj.markrone.library.client.gui.enums.ScenePath;
import proj.markrone.library.client.gui.utils.Loader;
import proj.markrone.library.api.network.ServerClient;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import lombok.Getter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;


public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static String themeName = "LIGHT";

    @Getter
    private static Stage primaryStage;

    /**
     * Точка входа в JavaFX приложение.
     *
     * @param stage Основная сцена, автоматически создаваемая JavaFX
     * @throws Exception Если произошла ошибка при инициализации приложения
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            logger.info("Запуск приложения...");
            primaryStage = stage;

            themeName = ThemePreferences.readThemeOrDefault("LIGHT");
            if (!"LIGHT".equals(themeName) && !"DARK".equals(themeName)) {
                themeName = "LIGHT";
            }

            configurePrimaryStage();
            Loader.loadScene(primaryStage, ScenePath.LOGIN);

            logger.info("Отображение основного окна...");
            primaryStage.show();
            logger.info("Приложение успешно запущено");
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения", e);
            logger.info("Аварийное завершение работы...");
            throw new Exception("Ошибка при запуске приложения", e);
        }
    }

    /**
     * Настраивает основные параметры главного окна приложения.
     *
     * @throws RuntimeException Если не удалось загрузить иконку приложения
     */
    private void configurePrimaryStage() {
        logger.info("Настройка основного окна...");

        primaryStage.setTitle("Система учета поступлений в библиотеку");

        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                primaryStage.getIcons().add(icon);
            } else {
                logger.warn("Иконка приложения не найдена, используется стандартная");
            }
        } catch (Exception e) {
            logger.warn("Не удалось загрузить иконку приложения, используется стандартная", e);
        }
    }

    @Override
    public void stop() {
        try {
            ServerClient.getInstance().disconnect();
        } catch (Exception ignored) {
        }
    }

}