package com.example.data_suplier.integration.mts;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.tomcat.util.digester.DocumentProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import javax.print.Doc;

import static java.net.http.HttpResponse.BodyHandlers.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

@Log
@Component
public class MTSIntegration {

    public String requestMainPage() throws Throwable {
        log.log(Level.INFO, "Request log");

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://internet.mts.by/"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .GET()
                .build();

        HttpResponse<Stream<String>> response = httpClient.send(request, ofLines());

        List<String> cookiesList = response.headers().allValues("set-cookie");
//        Map<String, List<String>> headersMap = response.headers().map();
//        Set<Map.Entry<String, List<String>>> entries = headersMap.entrySet();
//        for (Map.Entry<String, List<String>> entry : entries) {
//            String key = entry.getKey();
//            entry.getValue().forEach(v -> System.out.println(key + ": " + v));
//        }

        String auth = response.body().filter(s -> s.contains("name=\"authenticity_token\"")).findFirst().orElse("Not found");
        System.out.println(auth);

        return MtsHelper.getAuthenticityToken(response);
    }

    public String requestSession() throws Throwable {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest requestMainPage = MtsHelper.createGetRequest("https://internet.mts.by/");
        HttpResponse<Stream<String>> responseMainPage = processRequest(requestMainPage, httpClient);

        String authenticityToken = MtsHelper.getAuthenticityToken(responseMainPage);

        List<String> cookiesMainPage = responseMainPage.headers().allValues("set-cookie");


        var mtsPortalSession = MtsHelper.cutString(cookiesMainPage.get(0), "_mts_portal_session=", ";");

        Thread.sleep(5000);

        //parse authenticity_token from main page
        //HttpClient httpClient = HttpClient.newHttpClient();

        //Document document = new Document();

        HttpRequest requestSession = HttpRequest.newBuilder()
                .uri(new URI("https://internet.mts.by/session"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", mtsPortalSession)
                //.header("Connection", "keep-alive")
                .header("Cache-Control", "max-age=0")
                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"104\", \"Opera\";v=\"90\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Origin", "https://internet.mts.by")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 OPR/90.0.4480.84")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Fetch-Dest", "document")
                .header("Referer", "https://internet.mts.by/session")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "en-US,en;q=0.9")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "utf8=%E2%9C%93&" +
                           "authenticity_token=" +  URLEncoder.encode(authenticityToken, StandardCharsets.UTF_8) +
                           "&phone_number=" + URLEncoder.encode("+375 (00) 106 87 56", StandardCharsets.UTF_8) +
                           "&password=928851" +
                           "&commit=" + URLEncoder.encode("Войти", StandardCharsets.UTF_8)))
                .build();
        HttpResponse<Stream<String>> responseSession = processRequest(requestSession, httpClient);
        responseSession.body().forEach(System.out::println);

        //Redirect
        List<String> cookiesSession = responseSession.headers().allValues("set-cookie");
        mtsPortalSession = MtsHelper.cutString(cookiesSession.get(0), "_mts_portal_session=", ";");
        HttpRequest requestRedirect = HttpRequest.newBuilder()
                .uri(new URI("https://internet.mts.by/"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", mtsPortalSession)
                //.header("Connection", "keep-alive")
                .header("Cache-Control", "max-age=0")
                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"104\", \"Opera\";v=\"90\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("Upgrade-Insecure_Requests", "1")
                .header("Origin", "https://internet.mts.by")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 OPR/90.0.4480.84")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Fetch-Dest", "document")
                .header("Referer", "https://internet.mts.by/session")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "en-US,en;q=0.9")
                .GET()
//                .POST(HttpRequest.BodyPublishers.ofString(
//                        "utf8=%E2%9C%93&" +
//                                "authenticity_token=" + authenticityToken +
//                                "&phone_number=" + URLEncoder.encode("+375 (00) 106 87 56", StandardCharsets.UTF_8) +
//                                "&password=928851" +
//                                "&commit=" + URLEncoder.encode("Войти", StandardCharsets.UTF_8)))
                .build();
        HttpResponse<Stream<String>> responseRedirect = httpClient.send(requestRedirect, ofLines());
        responseRedirect.body().forEach(System.out::println);
        return null;
    }

    private HttpResponse<Stream<String>> processRequest(@NonNull HttpRequest request, @NonNull HttpClient httpClient) {
        try {
            return httpClient.send(request, ofLines());
        } catch (Exception e) {
            log.log(Level.INFO, "Request failed with error " + e.getMessage());
        }
        return null;
    }

    private void draft() throws Throwable {

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
