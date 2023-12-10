package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author WitherDragon
 */
public class VoucherApi 
{
    public String getAllVoucher()
    {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"voucher/all");
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

    public String createNewDish(String newVoucher) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
  
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(BaseURL.BASE_URL+"voucher"))
          .POST(HttpRequest.BodyPublishers.ofString(newVoucher))
          .header("Content-Type", "application/json")
          .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  
  return response.body();
    }
}
