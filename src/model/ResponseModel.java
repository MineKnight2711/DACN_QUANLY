/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import com.google.gson.JsonElement;
import java.util.UUID;

/**
 *
 * @author WitherDragon
 */
public class ResponseModel {
    private String id;
    private String apiVersion;
    private String message;
    private JsonElement data;

    public ResponseModel() {
    
    }
    
    public ResponseModel(String message, JsonElement data) {
        this.id = UUID.randomUUID().toString();
        this.apiVersion = "1.0";
        this.message = message;
        this.data = data;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }
    
    
}
