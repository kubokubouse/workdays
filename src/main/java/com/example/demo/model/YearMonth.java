package com.example.demo.model;
import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Entity
//@Table(name = "workdays")
public class YearMonth {
    @Id
    @Column
    Integer month;
    @Column
    Integer year;
   
  
}