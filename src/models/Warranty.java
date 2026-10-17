package models;

import java.sql.Date;

public class Warranty {

    private int warrantyId;
    private int productId;
    private Date startDate;
    private Date endDate;

    public Warranty() {
    }

    public Warranty(int warrantyId, int productId,Date startDate,Date endDate) {
           
        this.warrantyId = warrantyId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(int warrantyId) {
        this.warrantyId = warrantyId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}