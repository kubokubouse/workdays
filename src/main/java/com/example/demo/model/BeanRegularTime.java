package com.example.demo.model;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data


public class BeanRegularTime {
    String start;
    String end;
    String halftime;
    String worktime;
    Integer id;
}
