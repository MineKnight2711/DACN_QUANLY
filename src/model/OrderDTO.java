/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;

/**
 *
 * @author WitherDragon
 */
public class OrderDTO {
    private Orders order;
    private String paymentMethod;
    private List<DetailsDTO> detailList;

    public OrderDTO() {
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public List<DetailsDTO> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailsDTO> detailList) {
        this.detailList = detailList;
    }
    
    public static class DetailsDTO
    {
        private Dish dish;
        private int amount;
        private double price;

        public DetailsDTO() {
        }

        public Dish getDish() {
            return dish;
        }

        public void setDish(Dish dish) {
            this.dish = dish;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
        
    }
}

