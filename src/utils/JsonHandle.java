/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import model.Category;
import model.LoginResponse;
import model.ResponseModel;

/**
 *
 * @author WitherDragon
 */
public class JsonHandle {
    private Gson gson;
    public JsonHandle() {
        gson=new Gson();
    }
    
    public ResponseModel getResponseFromJson(String responeJson){
        return gson.fromJson(responeJson, ResponseModel.class);
    }
    public LoginResponse getLoginResponseFromJson(String loginResponeJson){
        String uncappedJson=gson.fromJson(loginResponeJson, String.class);
        return gson.fromJson(uncappedJson, LoginResponse.class);
    }
    public List<Category> getListCategoryFromJson(String listJson){
        return gson.fromJson(listJson, new TypeToken<List<Category>>() {}.getType());
    }
}
