/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import model.ResponseModel;
import utils.JsonHandle;

/**
 *
 * @author WitherDragon
 */
public class OrderApi 
{
    private JsonHandle jsonHandle;

    public OrderApi() 
    {
        jsonHandle=new JsonHandle();
    }
    
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
    private String getResponse(HttpURLConnection connection) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }

        reader.close();

        return response.toString();

    }
    public ResponseModel updateOrder(String orderId,String orderStatus){
        try {
            String encodeVietnameseChareter=URLEncoder.encode(orderStatus, StandardCharsets.UTF_8.toString());
            String formattedURL=String.format("%sorder/update-order-status?orderId=%s&orderStatus=%s",
                BaseURL.BASE_URL, orderId,  encodeVietnameseChareter);
            URL url = new URL(formattedURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            String response=getResponse(connection);
            connection.disconnect();
            ResponseModel responseModel=jsonHandle.getResponseFromJson(response);
            
            return responseModel;
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseModel("Fail",null);
        }
    }
}
