package com.example.demo.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class IndividualDataKey implements Serializable {
    private int companyID;
    private String mail;
}