module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires commons.validator;

    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}