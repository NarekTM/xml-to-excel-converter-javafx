package com.narektm.xmltoexcelh.product;

//TODO: change this class to an inner class of XmlToExcelConverter and use Lombok annotations
public final class ProductDynamicInfo {
    private final String formattedSupplyDate;
    private final String invoiceNumber;
    private final String warehouseCode;
    private final String supplierCode;
    private final String formattedSubmissionDate;
    private final String productName;
    private final String productCode;
    private final double productAmount;
    private final double price;
    private final double pricePerUnit;

    private ProductDynamicInfo(String formattedSupplyDate, String invoiceNumber, String warehouseCode, String supplierCode, String formattedSubmissionDate, String productName, String productCode, double productAmount, double price, double pricePerUnit) {
        this.formattedSupplyDate = formattedSupplyDate;
        this.invoiceNumber = invoiceNumber;
        this.warehouseCode = warehouseCode;
        this.supplierCode = supplierCode;
        this.formattedSubmissionDate = formattedSubmissionDate;
        this.productName = productName;
        this.productCode = productCode;
        this.productAmount = productAmount;
        this.price = price;
        this.pricePerUnit = pricePerUnit;
    }

    public String getFormattedSupplyDate() {
        return formattedSupplyDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public String getFormattedSubmissionDate() {
        return formattedSubmissionDate;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public double getProductAmount() {
        return productAmount;
    }

    public double getPrice() {
        return price;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String formattedSupplyDate;
        private String invoiceNumber;
        private String warehouseCode;
        private String supplierCode;
        private String formattedSubmissionDate;
        private String productName;
        private String productCode;
        private double productAmount;
        private double price;
        private double pricePerUnit;

        public Builder setFormattedSupplyDate(String formattedSupplyDate) {
            this.formattedSupplyDate = formattedSupplyDate;
            return this;
        }

        public Builder setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder setWarehouseCode(String warehouseCode) {
            this.warehouseCode = warehouseCode;
            return this;
        }

        public Builder setSupplierCode(String supplierCode) {
            this.supplierCode = supplierCode;
            return this;
        }

        public Builder setFormattedSubmissionDate(String formattedSubmissionDate) {
            this.formattedSubmissionDate = formattedSubmissionDate;
            return this;
        }

        public Builder setProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder setProductCode(String productCode) {
            this.productCode = productCode;
            return this;
        }

        public Builder setProductAmount(double productAmount) {
            this.productAmount = productAmount;
            return this;
        }

        public Builder setPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder setPricePerUnit(double pricePerUnit) {
            this.pricePerUnit = pricePerUnit;
            return this;
        }

        public ProductDynamicInfo build() {
            return new ProductDynamicInfo(formattedSupplyDate, invoiceNumber, warehouseCode, supplierCode,
                    formattedSubmissionDate, productName, productCode, productAmount, price, pricePerUnit);
        }
    }
}
