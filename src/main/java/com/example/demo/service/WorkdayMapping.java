package com.example.demo.service;

import org.apache.poi.ss.usermodel.Row;
import java.io.*;
import java.util.*;

import javax.print.DocFlavor.STRING;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class WorkdayMapping {
    
    public void outputExcel(String inputFilePath, String outputFilePath,
        Map stHourMap, Map stMinMap, Map endHourMap, Map endMinMap,
            Map lunchTimeHourMap, Map lunchTimeMinMap, Map totalHourMap, Map totalMinMap, Map otherMap) {

                
    File inputFileObject = new File(inputFilePath);
    File outputFileObject = new File(outputFilePath);

    if (!inputFileObject.exists()) {
        System.out.println("ファイルが存在しません：" + inputFilePath);
        return;
    }
        try {
        //FileInputStreamクラスのオブジェクトを生成

        FileInputStream inputFile = new FileInputStream(inputFilePath);

        //FileOutputStreamクラスのオブジェクトを生成

        if (!outputFileObject.exists()) {
            outputFileObject.createNewFile();
        }
        
        FileOutputStream outputFile = new FileOutputStream(outputFilePath);

        byte[] buf = new byte[256];

        int len;

        while((len = inputFile.read(buf)) != -1){ 
            outputFile.write(buf); 
        }

        outputFile.flush();

        inputFile.close();
        outputFile.close();
    } catch (FileNotFoundException e){

    } catch (IOException io){

    }
       
    Workbook excel;
    try {
    //エクセルファイルへアクセスするためのオブジェクト
        excel = WorkbookFactory.create(new File(outputFilePath));
   
    // シート名がわかっている場合
        Sheet sheet = excel.getSheet("Sheet1");

    //行
        Row row;
        for (int i=0; i< sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            for (int j=0; j<row.getLastCellNum(); j++) {
            //セル
                Cell cell = row.getCell(i);
                if (cell == null) {
                    continue;
                }
                String value = null;
                CellType cType = cell.getCellType();
                if(cType == CellType.STRING) {
            //文字列の取得
                    value = cell.getStringCellValue();
                } else {
                    continue;
                }
                
            //セルの値がMapと一致すればセルの内容を書き換える
                if (value.contains("sth")) {
                    Object data = stHourMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else{
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("stm")) {
                    Object data = stMinMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("edh")) {
                    Object data = endHourMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("edm")) {
                    Object data = endMinMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("lth")) {
                    Object data = lunchTimeHourMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("ltm")) {
                    Object data = lunchTimeMinMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("th")) {
                    Object data = totalHourMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("tm")) {
                    Object data = totalMinMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                if (value.contains("ot")) {
                    Object data = otherMap.get(value);
                    if (data == null) {
                        value.replace(value, " ");;
                    } else {
                    value.replace(value, data.toString());
                    }
                }
                cell.setCellValue(value);
                
            }
        }
    } catch (IOException io){

    }
    }

    public void outputExcel(String inputFilePath, String outputFilePath,
    Map stHourMap, Map endHourMap, Map lunchTimeHourMap, Map totalHourMap, Map otherMap) {

    File inputFileObject = new File(inputFilePath);
    if (!inputFileObject.exists()) {
        System.out.println("ファイルが存在しません：" + inputFilePath);
    }
    try {
    //FileInputStreamクラスのオブジェクトを生成

    FileInputStream inputFile = new FileInputStream(inputFilePath);

    //FileOutputStreamクラスのオブジェクトを生成

    FileOutputStream outputFile = new FileOutputStream(outputFilePath);

    byte[] buf = new byte[256];

    int len;

    while((len = inputFile.read(buf)) != -1){ 
        outputFile.write(buf); 
    }

    outputFile.flush();

    inputFile.close();
    outputFile.close();
} catch (FileNotFoundException e){

} catch (IOException io){

}
   
Workbook excel;
try {
//エクセルファイルへアクセスするためのオブジェクト
    excel = WorkbookFactory.create(new File(outputFilePath));

// シート名がわかっている場合
    Sheet sheet = excel.getSheet("Sheet1");

//行
    Row row = null;
    for (int i=0; i< row.getLastCellNum(); i++) {
        row = sheet.getRow(i);
        for (int j=0; j<row.getLastCellNum(); j++) {
        //セル
            Cell cell = row.getCell(i);
        //文字列の取得
            String value = cell.getStringCellValue();
            
        //セルの値がMapと一致すればセルの内容を書き換える
            if (value.contains("sth")) {
                Object data = stHourMap.get(value);
                value.replace(value, data.toString());
            }
            if (value.contains("edh")) {
                Object data = endHourMap.get(value);
                value.replace(value, data.toString());
            }
            if (value.contains("lth")) {
                Object data = lunchTimeHourMap.get(value);
                value.replace(value, data.toString());
            }
            if (value.contains("th")) {
                Object data = totalHourMap.get(value);
                value.replace(value, data.toString());
            }
            if (value.contains("ot")) {
                Object data = otherMap.get(value);
                value.replace(value, data.toString());
            }
            cell.setCellValue(value);
            
        }
    }
} catch (IOException io){

}
}
}

