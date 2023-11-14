/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import api.DishAPI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Dish;
import model.ResponseModel;
import utils.JsonHandle;

/**
 *
 * @author MINHNHAT
 */
public class DishController {
    private final DishAPI dishAPI;
    private final JsonHandle jsonHandle;

    public DishController() {
        dishAPI=new DishAPI();
        jsonHandle=new JsonHandle();
    }
    
    public List<Dish> getAllDish(){
        String apiResult=dishAPI.getAllDish();
        ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
        if(responseModel.getMessage().equals("Success")){
            return jsonHandle.getDishesFromResponseModel(responseModel.getData().toString());
        }
        return null;
    }
    public String createNewDish(File image,Dish dish){
        try {
            String apiResult=dishAPI.createNewDish(image, dish);
            ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
            if(responseModel.getMessage().equals("Success")){
                return responseModel.getMessage();
            }
            return responseModel.getMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Unknown";
        }
    }
    public String deleteDish(String dishId){
        try {
            String apiResult=dishAPI.deleteDish(dishId);
            ResponseModel responseModel=jsonHandle.getResponseFromJson(apiResult);
            if(responseModel.getMessage().equals("Success")){
                return responseModel.getMessage();
            }
            return responseModel.getMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Unknown";
        }
    }
}
