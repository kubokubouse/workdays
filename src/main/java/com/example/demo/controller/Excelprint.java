package com.example.demo.controller;
import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Excelprint {
  public static void main(String[] args) throws EncryptedDocumentException, IOException{
    // Excelファイルへアクセス
    Workbook excel = WorkbookFactory.create(new File("/apache/htdocs/image/Book1.xlsx"));

    // シート名を取得
    Sheet sheet = excel.getSheet("Sheet1");

    // 0行目を取得
    Row row = sheet.getRow(0);

    // 0番目のセルの値を取得
    Cell cell_name = row.getCell(0);
    // 1番目のセルの値を取得
    Cell cell_sex = row.getCell(1);
    // 2番目のセルの値を取得
    Cell cell_age = row.getCell(2);

    // セルの値を文字列として取得
    String value_name = cell_name.getStringCellValue();
    String value_sex = cell_sex.getStringCellValue();
    String value_age = cell_age.getStringCellValue();

    // 文字列を結果として表示
    System.out.println("value_name ： " + value_name);
    System.out.println("value_sex ： " + value_sex);
    System.out.println("value_age ： " + value_age);
  }
}