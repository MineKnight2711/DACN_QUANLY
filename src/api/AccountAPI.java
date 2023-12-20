/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import static api.BaseURL.BASE_URL;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
    
    public String signIn(String email,String password)
    {
        try 
        {
            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "account/sign-in").openConnection();    
            conn.setRequestMethod("POST");

            String body = String.format("email=%s&password=%s", 
                    URLEncoder.encode(email, StandardCharsets.UTF_8.toString()),  
                    URLEncoder.encode(password, StandardCharsets.UTF_8.toString()));

            conn.setDoOutput(true);
            try(var os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            // Send request  
            int status = conn.getResponseCode();


            String response = ""; 
            if (status ==  200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    response = scanner.useDelimiter("\\A").next();
                }
                return response;
            }
            return "Fail";
        } catch (Exception e) 
        {
            e.printStackTrace();
            return "Fail";
        } 
    }
}
