package com.example.data_suplier.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class User {

    @NotBlank
    String login;

    @NotBlank
    String password;
}
