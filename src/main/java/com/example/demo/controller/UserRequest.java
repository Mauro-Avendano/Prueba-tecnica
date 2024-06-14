package com.example.demo.controller;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    public UserRequest(String name, String email, String password, List<PhoneRequest> phones) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phones = phones;
    }

    private String name;
    private String email;
    private String password;
    private List<PhoneRequest> phones;
}

@Getter
@Setter
class PhoneRequest {
    public PhoneRequest(String number, String citycode, String countrycode) {
        this.number = number;
        this.citycode = citycode;
        this.countrycode = countrycode;
    }
    private String number;
    private String citycode;
    private String countrycode;
}
