package com.example.demo.model;
import java.io.Serializable;
import lombok.Data;

@Data
public class ContractDataKey implements Serializable {
    private int companyID;
    private Integer contractID;
}
