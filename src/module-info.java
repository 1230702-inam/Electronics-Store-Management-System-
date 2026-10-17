module ElectronicsStoreSystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens application to javafx.graphics;
    opens views to javafx.graphics;
    opens models to javafx.base;
    opens utils to javafx.base;
}