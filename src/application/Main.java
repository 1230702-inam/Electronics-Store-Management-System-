package application;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import views.DashboardView;

public class Main extends Application {

    private static Stage primaryStage;

    private static StackPane rootStack;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        rootStack = new StackPane();
        rootStack.getChildren().add(new DashboardView());

        Scene scene = new Scene(rootStack, 1280, 780);

        scene.getStylesheets().add(
            getClass().getResource("/css/app.css").toExternalForm());

        stage.setTitle("ZainTech Electronics Store");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getStage() {
        return primaryStage;
    }

 
    public static void setView(Parent view) {
        rootStack.getChildren().clear();
        rootStack.getChildren().add(view);
    }

   
    public static void showOverlay(Parent overlay) {
        closeOverlay(); 
        rootStack.getChildren().add(overlay);
    }

    public static void closeOverlay() {
        if (rootStack.getChildren().size() > 1) {
            rootStack.getChildren().remove(1, rootStack.getChildren().size());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
