package proj.markrone.library.client.gui.utils;

import proj.markrone.library.client.Main;
import proj.markrone.library.client.gui.enums.ThemesPath;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class AlertUtil {
    private String header;
    private String content;
    private Alert.AlertType alertType;

    public void realise() {
        complete().showAndWait();
    }

    /**
     * Создает и настраивает алерт.
     *
     * @return настроенный алерт
     */
    public Alert complete() {
        Alert alert = new Alert(alertType);
        alert.setTitle("Библиотека");
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.getDialogPane().setContent(wrapMessageContent(content));
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        /* Компактное окно, вытянутое по горизонтали — меньше «шпиля» по вертикали */
        alert.getDialogPane().setPrefWidth(440);
        alert.getDialogPane().setMaxWidth(480);
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertUtil.class.getResource(String.valueOf(ThemesPath.valueOf(Main.themeName).getPathToCss()))).toExternalForm());
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertUtil.class.getResource("/styles/styles.css")).toExternalForm());

        return alert;
    }

    /**
     * Текст сообщения в прокручиваемой области с переносом строк, чтобы длинные ошибки
     * с сервера и многострочный текст не обрезались стандартной подписью Alert.
     */
    private static Node wrapMessageContent(String raw) {
        String text = raw != null ? raw : "";
        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);
        area.setFocusTraversable(false);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setMaxHeight(Double.MAX_VALUE);
        int lineBreaks = text.split("\r?\n", -1).length;
        /* Умеренная высота строк — без лишней вертикальной вытянутости */
        int rows = Math.clamp(lineBreaks, 4, 12);
        area.setPrefRowCount(rows);

        final double viewportW = 400;
        final double viewportH = 168;

        ScrollPane scroll = new ScrollPane(area);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportWidth(viewportW);
        scroll.setMinViewportWidth(viewportW);
        scroll.setPrefViewportHeight(viewportH);
        scroll.setMinViewportHeight(viewportH);
        scroll.setMaxHeight(viewportH);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return scroll;
    }

    /**
     * Отображает алерт с типом ошибки.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     */
    public static void error(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.ERROR)
                .header(header)
                .content(content)
                .build().realise();
    }

    /**
     * Отображает алерт с типом предупреждения.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     */
    public static void warning(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.WARNING)
                .header(header)
                .content(content)
                .build().realise();
    }

    /**
     * Отображает информационный алерт.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     */
    public static void info(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(header)
                .content(content)
                .build().realise();
    }
}
