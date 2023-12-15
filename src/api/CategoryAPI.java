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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import model.Category;
import model.Dish;

/**
 *
 * @author WitherDragon
 */
public class CategoryAPI {
    
    public String createCategory(String categoryName, File imageFile) {
        String LINE_FEED = "\r\n";
        String BOUNDARY = "boundary";
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"category");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set the content type to indicate a file upload
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            // Create the POST data
            String boundary = "----" + BOUNDARY;
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream os = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
                // Add the categoryName parameter
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"categoryName\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(categoryName).append(LINE_FEED);

                // Add the image file
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(imageFile.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(imageFile.getName())).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                // Write the image file bytes
                byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                os.write(imageBytes);
                os.flush();

                // End of the request
                writer.append(LINE_FEED);
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
            }

            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
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
    public String getAllCategory() {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"category");
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
    public String deleteCategory(String categoryId){
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"category"+"/"+categoryId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

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
    public String updateCategory(File image, Category category) throws IOException {
        String LINE_FEED = "\r\n";
        String BOUNDARY = "boundary";   

        URL url = new URL(BaseURL.BASE_URL+"category/"+category.getCategoryID()); 
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try (OutputStream os = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
                // Parse các thông tin của dish
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"categoryName\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(category.getCategoryName()).append(LINE_FEED);
                
                
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
