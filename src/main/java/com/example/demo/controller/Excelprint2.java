package com.example.demo.controller;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excelprint2 {
  public static void main(String[] args) throws EncryptedDocumentException, IOException{
    // Excelファイルを作成
    Workbook outputWorkbook = new XSSFWorkbook();

    // シートを作成
    Sheet outputSheet = outputWorkbook.createSheet("user_data");

    // 行を作成
    Row outputRow = outputSheet.createRow(0);

//順番的には行を作る→セルを1つ作る→セルの中に記入する×8（ここでもループ）→行を作る　のループ
String[]s= {"日付","曜日","開始時刻","終了時刻","休憩時間","作業時間","備考","指示者"};
 for(int i=0;i<s.length;i++) {
	 Cell outputCell_name = outputRow.createCell(i);
    outputCell_name.setCellValue(s[i]);
 }
    // 出力用のストリームを用意
    FileOutputStream out = new FileOutputStream("/apache/htdocs/image/User2.xlsx");

    // ファイルへ出力
    outputWorkbook.write(out);
    System.out.println("sucsess");
  }
}