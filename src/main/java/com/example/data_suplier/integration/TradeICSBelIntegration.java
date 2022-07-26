package com.example.data_suplier.integration;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

import static java.net.http.HttpResponse.BodyHandlers.ofLines;

@Log
@Component
public class TradeICSBelIntegration {

    private String AUTH_FORM, TYPE, backurl, USER_LOGIN, USER_PASSWORD, Login;

    public String request() throws Throwable {
        //https://b2b.tradeicsbel.by/?login=yes
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://b2b.tradeicsbel.by/?login=yes"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "AUTH_FORM=Y&TYPE=AUTH&backurl=/&USER_LOGIN=camvision&USER_PASSWORD=5088889&Login=Authorize"))
                .build();
        HttpResponse<Stream<String>> response = httpClient.send(request, ofLines());
        response.body().forEach(System.out::println);
        return null;
    }
}
