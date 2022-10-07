package com.example.data_suplier.integration.mts;


import lombok.NonNull;
import lombok.extern.java.Log;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;
import java.util.logging.Level;

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


    public static String cutString (String string, String from, String to) {
        String substring = string.substring(string.indexOf(from));
        return substring.substring(0, substring.indexOf(to) + to.length());
    }

    private static String cutStringBetween (String string, String from, String to) {
        String substring = string.substring(string.indexOf(from) + from.length());
        return substring.substring(0, substring.indexOf(to));
    }


}
