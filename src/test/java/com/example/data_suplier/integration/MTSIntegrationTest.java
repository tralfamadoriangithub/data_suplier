package com.example.data_suplier.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MTSIntegrationTest {

    @Autowired
    private MTSIntegration integration;

    @Test
    public void requestTest() throws Throwable{
        integration.request();
    }
}