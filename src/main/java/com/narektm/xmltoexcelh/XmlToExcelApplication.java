package com.narektm.xmltoexcelh;

import com.narektm.xmltoexcelh.exception.GlobalExceptionAlerter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.narektm.xmltoexcelh.exception.GlobalExceptionAlerter.showAlertForException;

public class XmlToExcelApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Setting the global uncaught exception handler for the JavaFX thread
        Thread.currentThread().setUncaughtExceptionHandler((thread, exception) ->
                Platform.runLater(() -> showAlertForException(thread, exception)));

        FXMLLoader fxmlLoader = new FXMLLoader(XmlToExcelApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 320);
        stage.setTitle("XML to Excel Converter for Haykuhi");
        stage.getIcons().add(IconService.getMainIcon());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionAlerter::showAlertForException);
        launch();
    }
}
