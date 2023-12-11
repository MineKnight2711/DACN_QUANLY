/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;


import api.VoucherApi;
import java.io.IOException;
import java.util.List;
import model.ResponseModel;
import model.Voucher;
import utils.JsonHandle;

/**
 *
 * @author WitherDragon
 */
public class VoucherController 
{
    private final VoucherApi voucherApi;
    private final JsonHandle jsonHandle;

    public VoucherController() {
        voucherApi=new VoucherApi();
        jsonHandle=new JsonHandle();
    }
    
    public List<Voucher> getAllVoucher(){
        String apiResult=voucherApi.getAllVoucher();
        ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
         
        if(responseModel.getMessage().equals("Success")){
            return jsonHandle.getVouchersFromResponseModel(responseModel.getData().toString());
        }
        return null;
    }

    public String createNewVoucher(Voucher newVoucher) 
    {
        try {
            String apiResult=voucherApi.createNewVoucher(jsonHandle.toJson(newVoucher));
            ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
            if(responseModel.getMessage().equals("Success")){
                return responseModel.getMessage();
            }
            return responseModel.getMessage();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Fail";
        }
    }
    public String updateVoucher(Voucher newVoucher) 
    {
        try {
            String apiResult=voucherApi.updateVoucher(jsonHandle.toJson(newVoucher));
            ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
            if(responseModel.getMessage().equals("Success")){
                return responseModel.getMessage();
            }
            return responseModel.getMessage();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Fail";
        }
    }
    public String deleteVoucher(String voucherId) 
    {
        try {
            String apiResult=voucherApi.deleteVoucher(voucherId);
            ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
            if(responseModel.getMessage().equals("Success")){
                return responseModel.getMessage();
            }
            return responseModel.getMessage();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Fail";
        }
    }
}
