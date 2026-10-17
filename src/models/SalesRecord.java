package models;

import java.sql.Date;

public class SalesRecord {

    private int salesRecordId;
    private int orderId;
    private Date saleDate;
    private double totalSales;

    public SalesRecord() {
    }

    public SalesRecord(int salesRecordId, int orderId,Date saleDate,double totalSales) {
         
        this.salesRecordId = salesRecordId;
        this.orderId = orderId;
        this.saleDate = saleDate;
        this.totalSales = totalSales;
    }

    public int getSalesRecordId() {
        return salesRecordId;
    }

    public void setSalesRecordId(int salesRecordId) {
        this.salesRecordId = salesRecordId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }
}