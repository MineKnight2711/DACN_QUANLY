/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;


import api.CategoryAPI;
import java.io.File;
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
    public String createCategory(String categoryName,File categoryImage){
        String categoriesResult=categoryAPI.createCategory(categoryName, categoryImage);
        ResponseModel responeModel=jsonHandle.getResponseFromJson(categoriesResult);
        String apiResult=responeModel.getMessage();
        if(apiResult.equals("Success")){
            return apiResult;
        }
        return apiResult;
    }
    public String deleteCategory(String categoryId){
        String categoriesResult=categoryAPI.deleteCategory(categoryId);
        ResponseModel responeModel=jsonHandle.getResponseFromJson(categoriesResult);
        String apiResult=responeModel.getMessage();
        if(apiResult.equals("Success")){
            return apiResult;
        }
        return apiResult;
    }
}
