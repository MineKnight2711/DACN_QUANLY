/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import api.AccountAPI;
import api.DeliveryAPI;
import java.util.List;
import model.Account;
import model.DeliveryResponse;
import model.ResponseModel;
import utils.JsonHandle;

/**
 *
 * @author WitherDragon
 */
public class DeliveryController 
{
    private AccountAPI accountAPI;
    private DeliveryAPI deliveryAPI;
    private JsonHandle jsonHandle;

    public DeliveryController() 
    {
        accountAPI=new AccountAPI();
        deliveryAPI=new DeliveryAPI();
        jsonHandle=new JsonHandle(); 
    }
     public List<Account> getAllDeliver()
    {
        String response=accountAPI.getAllDeliver();
        System.out.println("response"+response);
        if(!response.equals("Fail"))
        {
            ResponseModel responseModel=jsonHandle.getResponseFromJson(response);
            if(responseModel.getMessage().equals("Success"))
            {
                return jsonHandle.getAllAdminFromJson(responseModel.getData().toString());
            }
            return null;
        }
        return null;
    }

    public List<DeliveryResponse> getAccountDeliveryDetails(String accountID) {
        String apiResult= deliveryAPI.getAllDeliveryDetailsByAccount(accountID);
        ResponseModel response=jsonHandle.getResponseFromJson(apiResult);
        if(response.getMessage().equals("Success"))
        {
            List<DeliveryResponse> deliveryResponse=jsonHandle.getDeliveryResponseFromJson(response.getData().toString());
            return deliveryResponse;
        }
        return null;
    }

    public ResponseModel asignToDeliver(String orderID, String accountId) {
        String apiResult=deliveryAPI.asignToDeliver(orderID, accountId);
        ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
        if(responseModel.getMessage().equals("Success"))
        {
            return responseModel;
        }
        return new ResponseModel("Fail",null);
    }
    public ResponseModel checkOrder(String orderID) 
    {
        String apiResult=deliveryAPI.checkOrder(orderID);
        return jsonHandle.getResponseFromJson(apiResult);
    }
}
