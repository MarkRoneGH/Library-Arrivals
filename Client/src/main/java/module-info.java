
module proj.markrone.library.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.prefs;
    requires java.desktop;
    requires static lombok;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.google.gson;

    opens proj.markrone.library.client.gui.controllers to javafx.fxml;
    opens proj.markrone.library.api.model.entities to com.google.gson, javafx.base;
    opens proj.markrone.library.api.utils to com.google.gson, javafx.base;
    opens proj.markrone.library.api.network to com.google.gson;
    opens proj.markrone.library.client.gui.services to javafx.fxml;
    exports proj.markrone.library.api.enums to com.google.gson;
    exports proj.markrone.library.client;
    opens proj.markrone.library.client.gui.utils to com.google.gson, javafx.base;
}
