package com.example.demo.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;

import lombok.Data;
/**
 * ユーザー情報一覧画面用 データクラス
 */
@Data
public class SuperUserListParam implements Serializable {

  @Valid
  private List<SuperUserData> superUserDataList;
}