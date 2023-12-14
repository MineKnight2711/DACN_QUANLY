/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 *
 * @author WitherDragon
 */
public class OrderApi 
{
    public String getAllOrder(){
        try {
            HttpClient client = HttpClient.newHttpClient();
  
            HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(BaseURL.BASE_URL+"order"))
              .GET()
              .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
