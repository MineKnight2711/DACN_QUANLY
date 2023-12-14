/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author WitherDragon
 */
public class Orders {
    private String orderID;
    private String status;
    private String deliveryInfo;
    private int quantity;
    private Integer score;
    private String feedBack;
    private Date dateFeedBack;
    private Voucher voucher;
    private Account account;
    private Date orderDate;

    // Constructor, getters, and setters
    // Constructors
    public Orders() {
        // Default constructor
    }

    public Orders(String orderID, String status, String deliveryInfo, int quantity, Integer score, String feedBack, Date dateFeedBack, Voucher voucher, Account account, Date orderDate) {
        this.orderID = orderID;
        this.status = status;
        this.deliveryInfo = deliveryInfo;
        this.quantity = quantity;
        this.score = score;
        this.feedBack = feedBack;
        this.dateFeedBack = dateFeedBack;
        this.voucher = voucher;
        this.account = account;
        this.orderDate = orderDate;
    }

    // Getters and setters for all fields
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public Date getDateFeedBack() {
        return dateFeedBack;
    }

    public void setDateFeedBack(Date dateFeedBack) {
        this.dateFeedBack = dateFeedBack;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
}
