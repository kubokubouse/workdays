package com.example.demo.model;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;

import lombok.Data;
@Data
public class SuperUserListParam implements Serializable {

  @Valid
  private List<SuperUserData> superUserDataList;
}