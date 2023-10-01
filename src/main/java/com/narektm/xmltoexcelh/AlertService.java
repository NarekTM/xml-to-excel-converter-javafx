package com.narektm.xmltoexcelh;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

import static com.narektm.xmltoexcelh.exception.ExceptionUtil.getRootCauseMessage;

public class AlertService {

    public static Alert createAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(IconService.getMainIcon());
        alert.setTitle(type.toString() + "!");

        return alert;
    }

    public static Alert createErrorAlertForException(Thread thread, Throwable throwable) {
        String message = String.format("Thread name: %s\nException message: %s", thread.getName(), getRootCauseMessage(throwable));
        Alert alert = createAlert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("An error occurred. Send a screenshot to Narek or just show him this message!");

        return alert;
    }
}
