package com.example.demo.model;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RePassword {
    private String password;
    private String confirmpassword;
}
