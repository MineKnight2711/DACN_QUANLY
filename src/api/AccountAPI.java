/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author WitherDragon
 */
public class AccountAPI {
    public String getAccountByEmail(String email) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+BaseURL.GET_ACCOUNT_BY_EMAIL+"/"+email);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return response.toString();
    }
    
    public String signIn(String email,String password){
        StringBuilder response = new StringBuilder();
        try {
            
            URL url = new URL(BaseURL.BASE_URL+BaseURL.SIGN_IN_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input and output streams
            connection.setDoOutput(true);

            // Set request headers if needed
            connection.setRequestProperty("Content-Type", "application/json");

            // Construct the JSON request body
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("email", email);
            jsonRequest.addProperty("password", password);
            // Cho phép trả về token đăng nhập 
            jsonRequest.addProperty("returnSecureToken", true);
            

            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.writeBytes(jsonRequest.toString());
                out.flush();
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
                

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return "Fail";
        }
        return response.toString();
    }
}
