package view;

import java.io.IOException;
import field.Dungeon;
import game.Console;
import controller.MainController;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application
{
    @Override
    public void start(final Stage primaryStage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("application.fxml"));
        final Scene root = loader.load();
        root.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        final MainController controller = loader.getController();
        Console.setController(controller);
        controller.initialize(new Dungeon());
        primaryStage.setTitle("Interractive fiction");
        primaryStage.setMaximized(true);
        primaryStage.setScene(root);
        primaryStage.show();
    }
    
    public static void main(final String[] args) {
        Application.launch(args);
    }
}

