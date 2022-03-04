package com.example.demo.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Onetime {
    @Id
    @Column
    private Integer id;
    @Column
    private String email;
    @Column
    private String lastname;
    @Column
    private String firstname;
    @Column
    private String password;
}
