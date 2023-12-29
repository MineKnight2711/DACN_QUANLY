/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import static api.BaseURL.BASE_URL;
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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import model.Account;

/**
 *
 * @author WitherDragon
 */
public class AccountAPI {
    public String getAllAdmin()
    {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"account/get-all-staff");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            connection.disconnect();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return response.toString();
    }
    public String getAllDeliver()
    {
         StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(BaseURL.BASE_URL+"account/get-all-deliver/Deliver");
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
    public String createNewStaff(File image, Account account) throws IOException {
        String LINE_FEED = "\r\n";
        String BOUNDARY = "boundary";   
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        URL url = new URL(BaseURL.BASE_URL+"account/create-staff"); 
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try (OutputStream os = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
                // Parse các thông tin của dish
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"password\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(account.getPassword()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"fullName\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(account.getFullName()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"email\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(account.getEmail()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"birthday\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(sdf.format(account.getBirthday())).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"gender\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(account.getGender()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"phoneNumber\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(account.getPhoneNumber()).append(LINE_FEED);
                
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"role\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(account.getRole()).append(LINE_FEED);

                // Add the image file
                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(image.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(image.getName())).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                byte[] imageBytes = Files.readAllBytes(image.toPath());
                os.write(imageBytes);
                os.flush();

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

    public String updateAccount(String acc) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
  
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(BaseURL.BASE_URL+"account/update-staff"))
          .PUT(HttpRequest.BodyPublishers.ofString(acc))
          .header("Content-Type", "application/json")
          .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  
        return response.body();

    }
}
