/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import model.Category;
import model.Dish;
import model.LoginResponse;
import model.OrderDTO;
import model.ResponseModel;
import model.Voucher;

/**
 *
 * @author WitherDragon
 */
public class JsonHandle {
    private final Gson gson;
    private final Gson prettyJson;
    public JsonHandle() {
        gson=new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        prettyJson=new GsonBuilder().setPrettyPrinting().create();
    }
    public String toPrettyJson(Object object)
    {
        return prettyJson.toJson(object);
    }
    public String toJson(Object object)
    {
        return gson.toJson(object);
    }
    public ResponseModel getResponseFromJson(String responeJson){
        return gson.fromJson(responeJson, ResponseModel.class);
    }
    public LoginResponse getLoginResponseFromJson(String loginResponeJson){
        return gson.fromJson(loginResponeJson, LoginResponse.class);
    }
    public List<Category> getListCategoryFromJson(String listJson){
        return gson.fromJson(listJson, new TypeToken<List<Category>>() {}.getType());
    }

    public List<Dish> getDishesFromResponseModel(String listJson) {
        return gson.fromJson(listJson, new TypeToken<List<Dish>>() {}.getType());
    }
    public List<Voucher> getVouchersFromResponseModel(String listJson) {
        return gson.fromJson(listJson, new TypeToken<List<Voucher>>() {}.getType());
    }
    public List<OrderDTO> getOrdersFromResponseModel(String listJson) {
        return gson.fromJson(listJson, new TypeToken<List<OrderDTO>>() {}.getType());
    }
}
