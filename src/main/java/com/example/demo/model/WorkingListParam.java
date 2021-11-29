package com.example.demo.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import lombok.Data;
/**
 * ユーザー情報一覧画面用 データクラス
 */
@Data
public class WorkingListParam implements Serializable {
  private int month;
  private int year;
  @Valid
  private List<WorkingData> workingDataList;
}