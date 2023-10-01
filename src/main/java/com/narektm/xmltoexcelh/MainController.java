package com.narektm.xmltoexcelh;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

import static com.narektm.xmltoexcelh.AlertService.createAlert;

public class MainController {

    private final FileChooser fileChooser = new FileChooser();

    private final XmlToExcelConverter xmlToExcelConverter = new XmlToExcelConverter();

    private final ExcelToExcelDataTransformer excelToExcelDataTransformer = new ExcelToExcelDataTransformer();

    @FXML
    private Label sourceXmlFilePathLabel;

    private String sourceXmlFilePath;

    private boolean isXmlToExcelConversionDone = false;

    @FXML
    private ComboBox<String> companyTypeComboBox;

    @FXML
    private Label sourceExcelFilePathLabel;

    private String sourceExcelFilePath;

    @FXML
    public void initialize() {
        companyTypeComboBox.getItems().addAll("Coca Cola", "RRR");
    }

    @FXML
    protected void onChooseXmlFileButtonClick() {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        setFileChooserInitialDirectory();

        File selectedXmlFile = fileChooser.showOpenDialog(null);
        if (selectedXmlFile != null) {
            sourceXmlFilePath = selectedXmlFile.getAbsolutePath();
            sourceXmlFilePathLabel.setText(selectedXmlFile.getName());
        }
    }

    @FXML
    protected void onConvertXmlToExcelButtonClick() {
        if (sourceXmlFilePath == null) {
            createAlert(Alert.AlertType.WARNING, "Please select an XML file before conversion.").showAndWait();
            return;
        }
        String companyType = companyTypeComboBox.getValue();
        if (companyType == null) {
            createAlert(Alert.AlertType.WARNING, "Please select a company type before conversion.").showAndWait();
            return;
        }
        String excelFilePath = sourceXmlFilePath.replace(".xml", ".xlsx");
        if (Objects.equals(companyType, "Coca Cola")) {
            xmlToExcelConverter.convertFirstType(sourceXmlFilePath, excelFilePath, companyType);
        } else if (Objects.equals(companyType, "RRR")) {
            xmlToExcelConverter.convertSecondType(sourceXmlFilePath, excelFilePath, companyType);
        }
        isXmlToExcelConversionDone = true;
        createAlert(Alert.AlertType.INFORMATION, "File has been converted successfully.\nJust fill missing product codes.").showAndWait();
    }

    @FXML
    protected void onFillIncomeImportButtonClick() {
        if (sourceXmlFilePath != null && isXmlToExcelConversionDone) {
            sourceExcelFilePath = sourceXmlFilePath.replace(".xml", ".xlsx");
        } else if (sourceExcelFilePath == null) {
            createAlert(Alert.AlertType.INFORMATION, "Please select an Excel file converted from an XML file before transformation.").showAndWait();
            return;
        }

        excelToExcelDataTransformer.transform(sourceExcelFilePath);
        createAlert(Alert.AlertType.INFORMATION, "Data has been transformed successfully.").showAndWait();
    }

    @FXML
    protected void onChooseExcelFileButtonClick() {
        if (sourceXmlFilePath != null && isXmlToExcelConversionDone) {
            createAlert(Alert.AlertType.WARNING, "You don't need to choose an Excel file just click the next button.").showAndWait();
            return;
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        setFileChooserInitialDirectory();

        File selectedExcelFile = fileChooser.showOpenDialog(null);
        if (selectedExcelFile != null) {
            sourceExcelFilePath = selectedExcelFile.getAbsolutePath();
            sourceExcelFilePathLabel.setText(selectedExcelFile.getName());
        }
    }

    private void setFileChooserInitialDirectory() {
        // Specified folder path to be opened by fileChooser if exists
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        File specifiedFolder = new File(desktop, "XML to Excel converter");

        // Check if the specified folder exists, if it does set it as the initial directory
        if (specifiedFolder.exists() && specifiedFolder.isDirectory()) {
            fileChooser.setInitialDirectory(specifiedFolder);
        } else {
            // If the specified folder doesn't exist, set Desktop as the initial directory
            fileChooser.setInitialDirectory(desktop);
        }
    }
}
