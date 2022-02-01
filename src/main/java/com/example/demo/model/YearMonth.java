package com.example.demo.model;
import java.io.Serializable;

import lombok.Data;

@Data
public class YearMonth implements Serializable{
    int month;
    int year;
    int lastmonth;
  
}