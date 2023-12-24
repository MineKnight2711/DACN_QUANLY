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
public class DeliveryResponse 
{
    private Delivery delivery;
    private DeliveryDetailsDTO details;

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public DeliveryDetailsDTO getDetails() {
        return details;
    }

    public void setDetails(DeliveryDetailsDTO details) {
        this.details = details;
    }
    
}
