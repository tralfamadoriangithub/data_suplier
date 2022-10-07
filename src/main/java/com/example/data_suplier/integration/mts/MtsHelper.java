package com.example.data_suplier.integration.mts;


import com.example.data_suplier.model.Balance;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class MtsHelper {

    private static final String AUTH_TOKEN_INPUT_NAME = "<input type=\"hidden\" name=\"authenticity_token\"";

    public static String getAuthenticityToken(@NonNull HttpResponse<Stream<String>> response) {
        String string = response.body().filter(s -> s.contains(AUTH_TOKEN_INPUT_NAME))
                .findFirst()
                .orElse(null);
        String form = cutString(string, "<form", "</form>");
        String input = cutString(form, AUTH_TOKEN_INPUT_NAME, "/>");
        return cutStringBetween(input, "value=\"", "\" />");
    }

    public static String getMtsPortalSessionCookie(HttpResponse<Stream<String>> response) {
        List<String> cookiesMainPage = response.headers().allValues("set-cookie");
        return MtsHelper.cutString(cookiesMainPage.get(0), "_mts_portal_session=", ";");
    }

    public static Balance extractBalance(HttpResponse<Stream<String>> accountInfoResponse) {
        String collect = accountInfoResponse.body().collect(Collectors.joining());
        Document document = Jsoup.parse(collect);
        Element element = document.select("div.payment").first();
        String value = element.text();
        return Balance.builder().rawBalance(value).build();
    }

    public static HttpRequest createGetRequest(String url) {

        try {
            URI uri = new URI(url);
            return HttpRequest.newBuilder()
                    .uri(uri)
                    .headers("Content-Type", "text/plain;charset=UTF-8")
                    .GET()
                    .build();
        } catch (Exception e) {
            log.log(Level.INFO, e.getMessage());
        }

        return null;
    }


    public static String cutString(String string, String from, String to) {
        String substring = string.substring(string.indexOf(from));
        return substring.substring(0, substring.indexOf(to) + to.length());
    }

    private static String cutStringBetween(String string, String from, String to) {
        String substring = string.substring(string.indexOf(from) + from.length());
        return substring.substring(0, substring.indexOf(to));
    }


}
