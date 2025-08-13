package com.example.movietracker.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application{
	
	@Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(
                getClass().getResource("/com/example/movietracker/ui/view/search/SearchView.fxml")
        );

        Scene scene = new Scene(fxml.load());
        stage.setTitle("MovieTracker");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
