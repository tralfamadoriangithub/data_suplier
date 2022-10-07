package com.example.data_suplier.integration.mts;

import com.example.data_suplier.model.MtsInformation;
import com.example.data_suplier.model.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MTSIntegrationTest {

    @Autowired
    private MTSIntegration integration;

    @Test
    public void getCurrentBalance() throws Throwable {
        var user = User.builder()
                .login("+375 (00) 106 87 56")
                .password("928851")
                .build();
        MtsInformation currentBalance = integration.getCurrentBalance(user);
        Assert.assertNotNull(currentBalance);
    }

    @Test
    public void amountParseTest() throws Throwable {
        URL resource = MTSIntegrationTest.class.getResource("/mts_account.html");
        Document document = Jsoup.parse(Paths.get(resource.toURI()).toFile());
        Element element = document.select("div.payment").first();
        String value = element.text();
        System.out.println(value);
    }


}
