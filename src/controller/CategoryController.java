/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;


import api.CategoryAPI;
import java.util.List;
import model.Category;
import model.ResponseModel;
import utils.JsonHandle;

/**
 *
 * @author WitherDragon
 */
public class CategoryController {
    private CategoryAPI categoryAPI;
    private JsonHandle jsonHandle;

    public CategoryController() {
        categoryAPI=new CategoryAPI();
        jsonHandle=new JsonHandle();
    }
    
    public List<Category> getAllCategory(){
        String categoriesResult=categoryAPI.getAllCategory();
        ResponseModel responeModel=jsonHandle.getResponseFromJson(categoriesResult);
        if(responeModel.getMessage().equals("Success")){
            return jsonHandle.getListCategoryFromJson(responeModel.getData().toString());
        }
        return null;
    }
}
