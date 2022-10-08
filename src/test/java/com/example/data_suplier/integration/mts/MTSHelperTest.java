package com.example.data_suplier.integration.mts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Paths;

class MTSHelperTest {

    private MTSHelper helper = new MTSHelper();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAuthenticityToken() throws Throwable {
        URL resource = MTSIntegrationTest.class.getResource("/mts_main.html");
        Document document = Jsoup.parse(Paths.get(resource.toURI()).toFile());
        Element element = document.select("input[name=\"authenticity_token\"]").first();
        String value = element != null ? element.attr("value") : "";
        System.out.println(value);
    }
}