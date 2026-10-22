package proj.markrone.library.client.gui.enums;

import lombok.Getter;


@Getter
public enum ThemesPath {
    LIGHT("/styles/colors/colors-light.css"),

    DARK("/styles/colors/colors-dark.css");

    private final String pathToCss;

    /**
     * Конструктор перечисления.
     *
     * @param pathToCss путь к CSS-файлу темы
     * @throws IllegalArgumentException если путь к CSS-файлу пустой или null
     */
    ThemesPath(String pathToCss) {
        if (pathToCss == null || pathToCss.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к CSS-файлу не может быть пустым или null");
        }
        this.pathToCss = pathToCss;
    }
}