package com.example.demo.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PhoneNumber {
    @Id
    @GeneratedValue
    private Long id;

    private String number;
    private String countryCode;
    private String cityCode;

    @ManyToOne
    private User user;
}
