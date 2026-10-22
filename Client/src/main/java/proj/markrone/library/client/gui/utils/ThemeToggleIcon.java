package proj.markrone.library.client.gui.utils;

import proj.markrone.library.client.Main;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;


public final class ThemeToggleIcon {

    private ThemeToggleIcon() {
    }

    public static void applyToButton(Button button) {
        String path = "LIGHT".equals(Main.themeName) ? "/images/light.png" : "/images/dark.png";
        var stream = Objects.requireNonNull(ThemeToggleIcon.class.getResourceAsStream(path), path);
        ImageView iv = new ImageView(new Image(stream));
        iv.setFitWidth(24);
        iv.setFitHeight(24);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        button.setGraphic(iv);
    }
}
