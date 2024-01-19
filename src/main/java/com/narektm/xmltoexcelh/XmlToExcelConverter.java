package com.narektm.xmltoexcelh;

import com.narektm.xmltoexcelh.product.ProductDynamicInfo;
import com.narektm.xmltoexcelh.product.ProductStaticInfoSingleton;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: refactor this class
public class XmlToExcelConverter {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DRAFT_EXCEL_FILE_PATH = "/files/draft-to-fill.xlsx";
    private static final String PRODUCTS_EXCEL_FILE_PATH = System.getProperty("user.home") + "/Desktop/XML to Excel converter/Products.xlsx";
    private Sheet productsSheet;

    public void convertFirstType(String sourceXmlFilePath, String outputExcelFilePath, String companyType) {
        try (FileInputStream fileInputStream = new FileInputStream(sourceXmlFilePath);
             InputStream draftExcelFile = getClass().getResourceAsStream(DRAFT_EXCEL_FILE_PATH)) {
            assert draftExcelFile != null;
            try (Workbook workbook = new XSSFWorkbook(draftExcelFile)) {
                loadProductsSheet();
                Document document = getDocument(fileInputStream);

                NodeList signedDataList = document.getElementsByTagName("SignedData");

                Sheet sheet = workbook.getSheetAt(0);

                int rowCount = sheet.getLastRowNum() + 1;
                for (int i = 0; i < signedDataList.getLength(); i++) {
                    Element signedData = (Element) signedDataList.item(i);

                    String series = getElementValue(signedData, "Series")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier("Series can't be null."));
                    String number = getElementValue(signedData, "Number")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier("Number can't be null."));
                    String invoiceNumber = series + number;

                    String supplyDateText = getElementValue(signedData, "SupplyDate")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("SupplyDate", invoiceNumber)));
                    String submissionDateText = getElementValue(signedData, "SubmissionDate")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("SubmissionDate", invoiceNumber)));
                    String formattedSupplyDate = getFormattedDate(supplyDateText, "\\+", DATE_FORMAT);
                    String formattedSubmissionDate = getFormattedDate(submissionDateText, "T", DATE_FORMAT);

                    String deliveryLocation = getElementValue(signedData, "DeliveryLocation")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("DeliveryLocation", invoiceNumber)));
                    String warehouseCode = getWarehouseCode(deliveryLocation, invoiceNumber);

                    NodeList goods = signedData.getElementsByTagName("Good");
                    for (int j = 0; j < goods.getLength(); j++) {
                        Element good = (Element) goods.item(j);
                        Optional<String> priceOptional = getElementValue(good, "Price");
                        double price;
                        if (priceOptional.isPresent() && !Objects.equals(price = Double.parseDouble(priceOptional.get()), 0d)) {
                            String productName = getElementValue(good, "Description")
                                    .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("Description", invoiceNumber)));
                            int productAmountPerPackage = extractProductAmountPerPackage(productName);
                            String packageAmountText = getElementValue(good, "Amount")
                                    .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("Amount", invoiceNumber)));
                            double packageAmount = Double.parseDouble(packageAmountText);
                            double productAmount = packageAmount * productAmountPerPackage;
                            double pricePerUnit = price / productAmount;
                            String productCode = getProductCodeByProductNameOrElseEmptyString(productName);

                            ProductDynamicInfo productDynamicInfo =
                                    getProductDynamicInfo(companyType, formattedSupplyDate, invoiceNumber, warehouseCode,
                                            formattedSubmissionDate, productName, productCode, productAmount, price, pricePerUnit);

                            Row row = sheet.createRow(rowCount++);
                            fillRow(row, productDynamicInfo, ProductStaticInfoSingleton.getInstance());
                        }
                    }
                }

                // 3. Write to Excel file.
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputExcelFilePath)) {
                    workbook.write(fileOutputStream);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public void convertSecondType(String sourceXmlFilePath, String outputExcelFilePath, String companyType) {
        try (FileInputStream fileInputStream = new FileInputStream(sourceXmlFilePath);
             InputStream draftExcelFile = getClass().getResourceAsStream(DRAFT_EXCEL_FILE_PATH)) {
            assert draftExcelFile != null;
            try (Workbook workbook = new XSSFWorkbook(draftExcelFile)) {
                loadProductsSheet();
                Document document = getDocument(fileInputStream);

                NodeList signedDataList = document.getElementsByTagName("SignedData");

                Sheet sheet = workbook.getSheetAt(0);

                int rowCount = sheet.getLastRowNum() + 1;
                for (int i = 0; i < signedDataList.getLength(); i++) {
                    Element signedData = (Element) signedDataList.item(i);

                    String series = getElementValue(signedData, "Series")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier("Series can't be null."));
                    String number = getElementValue(signedData, "Number")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier("Number can't be null."));
                    String invoiceNumber = series + number;

                    String supplyDateText = getElementValue(signedData, "SupplyDate")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("SupplyDate", invoiceNumber)));
                    String submissionDateText = getElementValue(signedData, "SubmissionDate")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("SubmissionDate", invoiceNumber)));
                    String formattedSupplyDate = getFormattedDate(supplyDateText, "\\+", DATE_FORMAT);
                    String formattedSubmissionDate = getFormattedDate(submissionDateText, "T", DATE_FORMAT);

                    String deliveryLocation = getElementValue(signedData, "DeliveryLocation")
                            .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("DeliveryLocation", invoiceNumber)));
                    String warehouseCode = getWarehouseCode(deliveryLocation, invoiceNumber);

                    NodeList goods = signedData.getElementsByTagName("Good");
                    for (int j = 0; j < goods.getLength(); j++) {
                        Element good = (Element) goods.item(j);
                        Optional<String> priceOptional = getElementValue(good, "Price");
                        double price;
                        if (priceOptional.isPresent() && !Objects.equals(price = Double.parseDouble(priceOptional.get()), 0d)) {
                            String productName = getElementValue(good, "Description")
                                    .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("Description", invoiceNumber)));
                            String amountText = getElementValue(good, "Amount")
                                    .orElseThrow(throwIllegalArgumentExceptionSupplier(getFormattedExceptionMessage("Amount", invoiceNumber)));
                            double productAmount = Double.parseDouble(amountText);
                            double pricePerUnit = price / productAmount;
                            String productCode = getProductCodeByProductNameOrElseEmptyString(productName);

                            ProductDynamicInfo productDynamicInfo =
                                    getProductDynamicInfo(companyType, formattedSupplyDate, invoiceNumber, warehouseCode,
                                            formattedSubmissionDate, productName, productCode, productAmount, price, pricePerUnit);

                            Row row = sheet.createRow(rowCount++);
                            fillRow(row, productDynamicInfo, ProductStaticInfoSingleton.getInstance());
                        }
                    }
                }

                // 3. Write to Excel file.
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputExcelFilePath)) {
                    workbook.write(fileOutputStream);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProductsSheet() {
        try (FileInputStream fis = new FileInputStream(PRODUCTS_EXCEL_FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
            productsSheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new RuntimeException("Error reading the Products.xlsx file", e);
        }
    }

    private ProductDynamicInfo getProductDynamicInfo(String companyType, String formattedSupplyDate, String invoiceNumber,
                                                     String warehouseCode, String formattedSubmissionDate, String productName,
                                                     String productCode, double productAmount, double price, double pricePerUnit) {
        return ProductDynamicInfo.builder()
                .setFormattedSupplyDate(formattedSupplyDate)
                .setInvoiceNumber(invoiceNumber)
                .setWarehouseCode(warehouseCode)
                .setSupplierCode(getSupplierCode(companyType))
                .setFormattedSubmissionDate(formattedSubmissionDate)
                .setProductName(productName)
                .setProductCode(productCode)
                .setProductAmount(productAmount)
                .setPrice(price)
                .setPricePerUnit(pricePerUnit)
                .build();
    }

    private String getSupplierCode(String companyType) {
        if (Objects.equals(companyType, "Coca Cola")) {
            return "0001";
        } else if (Objects.equals(companyType, "RRR")) {
            return "0002";
        }
        throw new IllegalArgumentException(String.format("The wrong company type: %s.", companyType));
    }

    private int extractProductAmountPerPackage(String input) {
        // Look for 'X' followed by one or more digits and possibly surrounded by any kind of text
        Pattern pattern = Pattern.compile("X(\\d+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException(String.format("There is no 'X' followed by a number in the given input: %s.", input));
    }

    private void fillRow(Row row, ProductDynamicInfo productDynamicInfo, ProductStaticInfoSingleton productStaticInfoSingleton) {
        row.createCell(0).setCellValue(productDynamicInfo.getFormattedSupplyDate());
        row.createCell(1).setCellValue(productDynamicInfo.getInvoiceNumber());
        row.createCell(2).setCellValue(productDynamicInfo.getWarehouseCode());
        row.createCell(3).setCellValue(productStaticInfoSingleton.getCurrency());
        row.createCell(4).setCellValue(productStaticInfoSingleton.getExchangeRate());
        row.createCell(5).setCellValue(productStaticInfoSingleton.getTo());
        row.createCell(6).setCellValue(productDynamicInfo.getSupplierCode());
        row.createCell(7).setCellValue(productStaticInfoSingleton.getSupplierAccountNumber());
        row.createCell(8).setCellValue(productDynamicInfo.getInvoiceNumber());
        row.createCell(9).setCellValue(productDynamicInfo.getFormattedSubmissionDate());
        row.createCell(10).setCellValue("");
        row.createCell(11).setCellValue(productStaticInfoSingleton.getVatCalculationForm());
        row.createCell(12).setCellValue(productStaticInfoSingleton.getIncludeVatToCostPrice());
        row.createCell(13).setCellValue(productStaticInfoSingleton.getVatDisplayForm());
        row.createCell(14).setCellValue(productStaticInfoSingleton.getDocumentState());
        row.createCell(15).setCellValue(productDynamicInfo.getProductCode());
        row.createCell(16).setCellValue(productDynamicInfo.getProductName());
        row.createCell(17).setCellValue(productDynamicInfo.getProductAmount());
        row.createCell(18).setCellValue(productDynamicInfo.getPricePerUnit());
        row.createCell(19).setCellValue(productDynamicInfo.getPrice());
        row.createCell(20).setCellValue(productDynamicInfo.getPricePerUnit());
        row.createCell(21).setCellValue(productStaticInfoSingleton.getVat());
        row.createCell(22).setCellValue(productStaticInfoSingleton.getAccountNumber());
    }

    private String getProductCodeByProductNameOrElseEmptyString(String searchValue) {
        for (Row row : productsSheet) {
            Cell cellB = row.getCell(1);
            if (cellB != null && cellB.getCellType() == CellType.STRING && cellB.getStringCellValue().equals(searchValue)) {
                Cell cellA = row.getCell(0);
                return cellA != null ? cellA.getStringCellValue() : "";  // If there's a match in column B, return column A's value.
            }
        }
        return "";
    }

    private String getFormattedExceptionMessage(String elementName, String invoiceNumber) {
        return String.format("%s can't be null (invoice number %s).", elementName, invoiceNumber);
    }

    private Supplier<IllegalArgumentException> throwIllegalArgumentExceptionSupplier(String invoiceNumber) {
        return () -> new IllegalArgumentException(invoiceNumber);
    }

    private String getFormattedDate(String unformattedDate, String spliterator, String datePattern) {
        unformattedDate = unformattedDate.split(spliterator)[0];
        LocalDate date = LocalDate.parse(unformattedDate);

        return date.format(DateTimeFormatter.ofPattern(datePattern));
    }

    private String getWarehouseCode(String deliveryLocation, String invoiceNumber) {
        if ((deliveryLocation.contains("Րաֆֆ") || deliveryLocation.contains("Ռաֆֆ"))
                && deliveryLocation.contains("29/7")) {
            return "02";
        } else if ((deliveryLocation.contains("Սևան") || deliveryLocation.contains("Սեվան"))
                && deliveryLocation.contains("21")) {
            return "04";
        }
        throw new IllegalArgumentException(String.format("Unknown delivery location: %s, invoice number: %s.", deliveryLocation, invoiceNumber));
    }

    private Document getDocument(FileInputStream fileInputStream) throws ParserConfigurationException, SAXException, IOException {
        InputSource inputSource = new InputSource(fileInputStream);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(inputSource);
    }

    private Optional<String> getElementValue(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return Optional.of(list.item(0).getTextContent());
        }
        return Optional.empty();
    }
}
