package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15, 12, 12, 15));
        vBox.setStyle("-fx-background-color: #336699;");

        Button btnStart = new Button("启动");



        primaryStage.show();

    }
}
