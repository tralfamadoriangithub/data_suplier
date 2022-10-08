package com.example.data_suplier.integration.mts;

import com.example.data_suplier.model.MtsInformation;
import com.example.data_suplier.model.User;
import lombok.extern.java.Log;
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
import java.util.logging.Level;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
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
        log.log(Level.INFO, "Current balance " + currentBalance.getBalance().getRawBalance());
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
