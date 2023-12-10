/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import model.Dish;

/**
 *
 * @author MINHNHAT
 */
public class DishAPI {
    public String getAllDish(){
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"dish");
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
    public String createNewDish(File image, Dish dish) throws IOException {
        String LINE_FEED = "\r\n";
        String BOUNDARY = "boundary";   

        URL url = new URL(BaseURL.BASE_URL+"dish"); 
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try (OutputStream os = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
                // Parse các thông tin của dish
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"dishName\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(dish.getDishName()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"description\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(dish.getDescription()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"price\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(String.valueOf(dish.getPrice())).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"inStock\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(String.valueOf(dish.getInStock())).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"categoryID\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(dish.getCategory().getCategoryID()).append(LINE_FEED);

                // Add the image file
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(image.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(image.getName())).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                // Write the image file bytes
                byte[] imageBytes = Files.readAllBytes(image.toPath());
                os.write(imageBytes);
                os.flush();

                // End of the request
                writer.append(LINE_FEED);
                writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
            }

        // Handle response
        String response = getResponse(connection);

        connection.disconnect();

        return response;

      }
    public String updateNewDish(File image, Dish dish) throws IOException {
        String LINE_FEED = "\r\n";
        String BOUNDARY = "boundary";   

        URL url = new URL(BaseURL.BASE_URL+"dish/"+dish.getDishID()); 
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try (OutputStream os = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
                // Parse các thông tin của dish
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"dishName\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(dish.getDishName()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"description\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(dish.getDescription()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"price\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(String.valueOf(dish.getPrice())).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"inStock\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(String.valueOf(dish.getInStock())).append(LINE_FEED);        
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"categoryID\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(dish.getCategory().getCategoryID());
                // Add the image file
                if(image!=null)
                {
                    writer.append(LINE_FEED);
                    writer.append("--").append(BOUNDARY).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(image.getName()).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(image.getName())).append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    // Write the image file bytes
                    byte[] imageBytes = Files.readAllBytes(image.toPath());
                    os.write(imageBytes);
                    os.flush();
                    // End of the request
                    
                }
                writer.append(LINE_FEED);
                writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
                
            }

        // Handle response
        String response = getResponse(connection);

        connection.disconnect();

        return response;

      }
    public String deleteDish(String dishId) throws IOException {
            URL url = new URL(BaseURL.BASE_URL+"dish"+"/"+dishId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            String response=getResponse(connection);
            connection.disconnect();
            return response;
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
}
