package com.example.demo.model;

import javax.validation.constraints.Pattern;
import lombok.Data;

@Data


public class BeanRegularTime {
    
    @Pattern(regexp = "^([0-1][0-9]|[2][0-3]):[0-5][0-9]$", message = "00:00の形式で入力してください")
    String start;
    @Pattern(regexp = "^([0-1][0-9]|[2][0-3]):[0-5][0-9]$", message = "00:00の形式で入力してください")
    String end;
    @Pattern(regexp = "^([0-1][0-9]|[2][0-3]):[0-5][0-9]$", message = "00:00の形式で入力してください")
    String halftime;
    @Pattern(regexp = "^([0-1][0-9]|[2][0-3]):[0-5][0-9]$", message = "00:00の形式で入力してください")
    String worktime;
    Integer id;
}
