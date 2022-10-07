package com.example.data_suplier.service;

import com.example.data_suplier.integration.mts.MTSIntegration;
import com.example.data_suplier.model.MtsInformation;
import com.example.data_suplier.model.User;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Service
@Log
public class MtsService {

    @Autowired
    private MTSIntegration mtsIntegration;

    public MtsInformation getCurrentBalance(User user) {
        try {
            return mtsIntegration.getCurrentBalance(user);
        } catch (Exception e) {
            log.log(Level.INFO, "Exception " + e.getMessage());
        }
        return null;
    }
}
