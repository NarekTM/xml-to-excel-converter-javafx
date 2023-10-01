package com.narektm.xmltoexcelh.product;

//TODO: add fields with initial values, change this class to an inner class of XmlToExcelConverter
// and use Lombok annotations
public final class ProductStaticInfoSingleton {
    private static final ProductStaticInfoSingleton INSTANCE = new ProductStaticInfoSingleton();

    private ProductStaticInfoSingleton() {
    }

    public static ProductStaticInfoSingleton getInstance() {
        return INSTANCE;
    }

    public String getCurrency() {
        return "AMD";
    }

    public String getExchangeRate() {
        return "1";
    }

    public String getTo() {
        return "1";
    }

    public String getSupplierAccountNumber() {
        return "52121";
    }

    public String getVatCalculationForm() {
        return "1";
    }

    public String getIncludeVatToCostPrice() {
        return "0";
    }

    public String getVatDisplayForm() {
        return "2";
    }

    public String getDocumentState() {
        return "0";
    }

    public String getVat() {
        return "1";
    }

    public String getAccountNumber() {
        return "2161";
    }
}
