/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import api.AccountAPI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import model.Account;
import model.LoginResponse;
import model.ResponseModel;
import utils.JsonHandle;

/**
 *
 * @author WitherDragon
 */
public class AccountController {
    private AccountAPI accountAPI;
    private JsonHandle jsonHandle;

    public AccountController() {
        accountAPI=new AccountAPI();
        jsonHandle=new JsonHandle();
    }
    public List<Account> getAllAdmin()
    {
        String response=accountAPI.getAllAdmin();
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
    public ResponseModel signIn(String emai,String password){
        ResponseModel responseModel=new ResponseModel();
        String signInResult=accountAPI.signIn(emai, password);
        if(!signInResult.equals("Fail")){
            ResponseModel responeModel=jsonHandle.getResponseFromJson(signInResult);
            String loginResponseJson=responeModel.getData().toString();
            if(!loginResponseJson.contains("error")){
                LoginResponse loginResponse=jsonHandle.getLoginResponseFromJson(loginResponseJson);
                String accountresult= accountAPI.getAccountByEmail(loginResponse.getEmail());
                System.out.println("Account result"+accountresult);
                responseModel=jsonHandle.getResponseFromJson(accountresult);
                return responseModel;
            }
            responseModel.setMessage("Fail");
            return responseModel;
        }
        responseModel.setMessage("Fail");
        return responseModel;
    }
    public ResponseModel createStaff(File image,Account acc){
        ResponseModel responseModel=new ResponseModel();
        try {
            
            String apiResult=accountAPI.createNewStaff(image, acc);
            responseModel=jsonHandle.getResponseFromJson(apiResult);
            return responseModel;
        } catch (IOException ex) {
            ex.printStackTrace();
            responseModel.setMessage("Fail");
            return responseModel;
        }
    }
//    public String deleteStaff(String accountID) {
//        
//    }
}
