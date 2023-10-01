package com.narektm.xmltoexcelh.exception;

import javafx.scene.control.Alert;

import static com.narektm.xmltoexcelh.AlertService.createErrorAlertForException;

public class GlobalExceptionAlerter {

    public static void showAlertForException(Thread thread, Throwable throwable) {
        Alert alert = createErrorAlertForException(thread, throwable);
        alert.showAndWait();
    }
}
