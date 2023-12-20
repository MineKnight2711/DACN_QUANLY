/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import api.AccountAPI;
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
    
    public String signIn(String emai,String password){
        String signInResult=accountAPI.signIn(emai, password);
        if(!signInResult.equals("Fail")){
            ResponseModel responeModel=jsonHandle.getResponseFromJson(signInResult);
            String loginResponseJson=responeModel.getData().toString();
            if(!loginResponseJson.contains("error")){
                LoginResponse loginResponse=jsonHandle.getLoginResponseFromJson(loginResponseJson);
                String accountresult= accountAPI.getAccountByEmail(loginResponse.getEmail());
                ResponseModel accountResponseModel=jsonHandle.getResponseFromJson(accountresult);
                return accountResponseModel.getMessage();
            }
            return responeModel.getMessage();
        }
        return signInResult;
    }
}
