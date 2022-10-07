package com.example.data_suplier.controller;

import com.example.data_suplier.model.MtsInformation;
import com.example.data_suplier.model.User;
import com.example.data_suplier.service.MtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class MtsController {

    @Autowired
    private MtsService service;

    //TODO: User validation
    @PostMapping("/mts/balance")
    public MtsInformation getCurrentBalance(@Valid @RequestBody User user) {
        return service.getCurrentBalance(user);
    }
}
