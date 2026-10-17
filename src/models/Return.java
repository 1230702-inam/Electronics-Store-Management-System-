package models;

import java.sql.Date;

public class Return {

    private int returnId;
    private int orderId;
    private int productId;
    private String reason;
    private Date returnDate;

    public Return() {
    }

    public Return(int returnId,int orderId,int productId,String reason,Date returnDate) {
    
        this.returnId = returnId;
        this.orderId = orderId;
        this.productId = productId;
        this.reason = reason;
        this.returnDate = returnDate;
    }

    public int getReturnId() {
        return returnId;
    }

    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}