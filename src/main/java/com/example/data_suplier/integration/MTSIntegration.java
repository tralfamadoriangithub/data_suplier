package com.example.data_suplier.integration;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static java.net.http.HttpResponse.BodyHandlers.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

@Log
@Component
public class MTSIntegration {

    public String request() throws Throwable {
        System.out.println("Request triggered");
        log.log(Level.INFO, "Request log");

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://internet.mts.by/"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .GET()
                .build();

        HttpResponse<Stream<String>> response = httpClient.send(request, ofLines());
        Map<String, List<String>> headersMap = response.headers().map();
        Set<Map.Entry<String, List<String>>> entries = headersMap.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            entry.getValue().forEach(v -> System.out.println(key + ": " + v));
        }

        response.body().forEach(System.out::println);

        return null;
    }

    private void draft() throws Throwable {

        //https://www.f1news.ru/
        URL url = new URL("https://internet.mts.by/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        connection.setRequestMethod("GET");

        connection.setDoOutput(true);

//        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

        int responseCode = connection.getResponseCode();

//        System.out.println("Response code " + responseCode);
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//            System.out.println(content.toString());
//        }
//        in.close();
    }
}