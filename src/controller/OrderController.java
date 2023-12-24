/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import api.OrderApi;
import java.util.List;
import model.OrderDTO;
import model.ResponseModel;
import utils.JsonHandle;

/**
 *
 * @author WitherDragon
 */
public class OrderController 
{
    private final OrderApi orderApi;
    private final JsonHandle jsonHandle;

    public OrderController() {
        orderApi=new OrderApi();
        jsonHandle=new JsonHandle();
    }
    
    public List<OrderDTO> getAllOrders(){
        String apiResult=orderApi.getAllOrder();
        ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
         
        if(responseModel.getMessage().equals("Success")){
            return jsonHandle.getOrdersFromResponseModel(responseModel.getData().toString());
        }
        return null;
    }
    public String updateOrder(String orderId,String orderStatus){
        ResponseModel apiResult=orderApi.updateOrder(orderId,orderStatus);
        if(apiResult.getMessage().equals("Success"))
        {
            return "Success";
        }
        return apiResult.getData().toString();
    }
}
