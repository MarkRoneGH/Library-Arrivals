package proj.markrone.library.client.gui.utils;

import java.util.prefs.Preferences;

/**
 * Сохранение выбранной темы оформления между запусками.
 */
public final class ThemePreferences {

    private static final Preferences PREFS = Preferences.userRoot().node("proj/markrone/library/client");
    private static final String KEY_THEME = "theme";


    public static String readThemeOrDefault(String defaultTheme) {
        return PREFS.get(KEY_THEME, defaultTheme);
    }

    public static void saveTheme(String themeName) {
        PREFS.put(KEY_THEME, themeName);
    }
}
