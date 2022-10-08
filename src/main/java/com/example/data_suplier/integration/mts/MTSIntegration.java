package com.example.data_suplier.integration.mts;

import com.example.data_suplier.model.MtsInformation;
import com.example.data_suplier.model.User;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.stream.Stream;

import static com.example.data_suplier.integration.mts.MTSHelper.*;
import static java.net.URLEncoder.encode;
import static java.net.http.HttpResponse.BodyHandlers.ofLines;

@Log
@Component
public class MTSIntegration {

    private static final String MTS_URL = "https://internet.mts.by/";

    public MtsInformation getCurrentBalance(User user) throws Exception {

        log.log(Level.INFO, "Request log");
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest mainPageRequest = createGetRequest(MTS_URL);
        HttpResponse<Stream<String>> mainPageResponse = processRequest(mainPageRequest, httpClient);

        var authenticityToken = getAuthenticityToken(mainPageResponse);
        var mtsPortalSession = getMtsPortalSessionCookie(mainPageResponse);

        Thread.sleep(5000);

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(new URI(MTS_URL + "session"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", mtsPortalSession)
                .POST(HttpRequest.BodyPublishers.ofString(
                        "utf8=%E2%9C%93&" +
                             "authenticity_token=" + encode(authenticityToken, StandardCharsets.UTF_8) +
                             "&phone_number=" + encode(user.getLogin(), StandardCharsets.UTF_8) +
                             "&password=" + encode(user.getPassword(), StandardCharsets.UTF_8) +
                             "&commit=" + encode("Войти", StandardCharsets.UTF_8)))
                .build();
        HttpResponse<Stream<String>> loginResponse = processRequest(loginRequest, httpClient);
        mtsPortalSession = getMtsPortalSessionCookie(loginResponse);

        HttpRequest accountInfoRequest = HttpRequest.newBuilder()
                .uri(new URI(MTS_URL))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", mtsPortalSession)
                .GET()
                .build();
        HttpResponse<Stream<String>> accountInfoResponse = httpClient.send(accountInfoRequest, ofLines());

        return MtsInformation.builder()
                .balance(extractBalance(accountInfoResponse))
                .build();
    }

    private HttpResponse<Stream<String>> processRequest(@NonNull HttpRequest request, @NonNull HttpClient httpClient) {
        try {
            return httpClient.send(request, ofLines());
        } catch (Exception e) {
            log.log(Level.INFO, "Request failed with error " + e.getMessage());
        }
        return null;
    }

//    public MtsInformation getCurrentBalanceFullHeaders(User user) throws Exception {
//
//        log.log(Level.INFO, "Request log");
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpRequest requestMainPage = MtsHelper.createGetRequest("https://internet.mts.by/");
//        HttpResponse<Stream<String>> responseMainPage = processRequest(requestMainPage, httpClient);
//
//        String authenticityToken = MtsHelper.getAuthenticityToken(responseMainPage);
//
//        List<String> cookiesMainPage = responseMainPage.headers().allValues("set-cookie");
//
//
//        var mtsPortalSession = MtsHelper.cutString(cookiesMainPage.get(0), "_mts_portal_session=", ";");
//
//        Thread.sleep(5000);
//
//        HttpRequest requestSession = HttpRequest.newBuilder()
//                .uri(new URI("https://internet.mts.by/session"))
//                .headers("Content-Type", "application/x-www-form-urlencoded")
//                .header("Cookie", mtsPortalSession)
//                .header("Cache-Control", "max-age=0")
//                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"104\", \"Opera\";v=\"90\"")
//                .header("sec-ch-ua-mobile", "?0")
//                .header("sec-ch-ua-platform", "\"Windows\"")
//                .header("Upgrade-Insecure-Requests", "1")
//                .header("Origin", "https://internet.mts.by")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 OPR/90.0.4480.84")
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .header("Sec-Fetch-Site", "same-origin")
//                .header("Sec-Fetch-Mode", "navigate")
//                .header("Sec-Fetch-User", "?1")
//                .header("Sec-Fetch-Dest", "document")
//                .header("Referer", "https://internet.mts.by/session")
//                .header("Accept-Encoding", "gzip, deflate, br")
//                .header("Accept-Language", "en-US,en;q=0.9")
//                .POST(HttpRequest.BodyPublishers.ofString(
//                        "utf8=%E2%9C%93&" +
//                                "authenticity_token=" +  URLEncoder.encode(authenticityToken, StandardCharsets.UTF_8) +
//                                "&phone_number=" + URLEncoder.encode(user.getLogin(), StandardCharsets.UTF_8) +
//                                "&password=" + URLEncoder.encode(user.getPassword(), StandardCharsets.UTF_8) +
//                                "&commit=" + URLEncoder.encode("Войти", StandardCharsets.UTF_8)))
//                .build();
//        HttpResponse<Stream<String>> responseSession = processRequest(requestSession, httpClient);
//        responseSession.body().forEach(System.out::println);
//
//        //Redirect
//        List<String> cookiesSession = responseSession.headers().allValues("set-cookie");
//        mtsPortalSession = MtsHelper.cutString(cookiesSession.get(0), "_mts_portal_session=", ";");
//        HttpRequest requestRedirect = HttpRequest.newBuilder()
//                .uri(new URI("https://internet.mts.by/"))
//                .headers("Content-Type", "application/x-www-form-urlencoded")
//                .header("Cookie", mtsPortalSession)
//                .header("Cache-Control", "max-age=0")
//                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"104\", \"Opera\";v=\"90\"")
//                .header("sec-ch-ua-mobile", "?0")
//                .header("sec-ch-ua-platform", "\"Windows\"")
//                .header("Upgrade-Insecure_Requests", "1")
//                .header("Origin", "https://internet.mts.by")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 OPR/90.0.4480.84")
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .header("Sec-Fetch-Site", "same-origin")
//                .header("Sec-Fetch-Mode", "navigate")
//                .header("Sec-Fetch-User", "?1")
//                .header("Sec-Fetch-Dest", "document")
//                .header("Referer", "https://internet.mts.by/session")
//                .header("Accept-Encoding", "gzip, deflate, br")
//                .header("Accept-Language", "en-US,en;q=0.9")
//                .GET()
//                .build();
//        HttpResponse<Stream<String>> responseRedirect = httpClient.send(requestRedirect, ofLines());
//
//        String collect = responseRedirect.body().collect(Collectors.joining());
//        Document document = Jsoup.parse(collect);
//        Element element = document.select("div.payment").first();
//        String value = element.text();
//
//        return MtsInformation.builder().rawBalance(value).build();
//    }
}
