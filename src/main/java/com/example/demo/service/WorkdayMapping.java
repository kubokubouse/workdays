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
    

    if (!inputFileObject.exists()) {
        System.out.println("ファイルが存在しません：" + inputFilePath);
        return;
    }
    
    //ファイルのコピーが成功したときは０が戻り値となる
    int copyFiles = copyFile(inputFilePath, outputFilePath);

    if (copyFiles != 0) {
        return;
    }
       
    Workbook excel;
    try {
    //エクセルファイルへアクセスするためのオブジェクト
        FileInputStream in = new FileInputStream(outputFilePath);
        excel = WorkbookFactory.create(in);
   
    // シート名がわかっている場合
        Sheet sheet = excel.getSheet("Sheet1");

    //行
        Row row;
        for (int i = 0; i < sheet.getLastRowNum()+1; i++) {
            row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum()+1; j++ ) {
            //セル
                Cell cell = row.getCell(j);
                if (cell == null) {
                    System.out.println("CellがNullです");
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
                String setValue = null;
            //始業時刻
            //セルに2つのKeyがある時（例　sth1:stm1）
                if (value.contains("stm")) {
                    String index = value.substring(3,4);
                    Object sth = stHourMap.get("sth" + index);
                    Object stm = stMinMap.get("stm" + index);
                
                    String sthValue = value.replace("sth" + index, sth.toString());
                    setValue = sthValue.replace("stm" + index, stm.toString());
                }
            //セルの値にKeyが１つのとき（例　sth1　のみ）
                if (value.contains("sth") && !value.contains("stm")) {
                    Object data = stHourMap.get(value);
            //データがNullの時
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //セルの値にKeyが１つのとき（例　stm1　のみ）
                if (value.contains("stm") && !value.contains("sth")) {
                    Object data = stMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //終業時刻
                if (value.contains("edh") && value.contains("edm")) {
                    String index = value.substring(3,4);
                    Object edh = endHourMap.get("edh" + index);
                    Object edm = endMinMap.get("edm" + index);

                    String edhValue = value.replace("edh" + index, edh.toString());
                    setValue = edhValue.replace("edm" + index, edm.toString());
                }
                if (value.contains("edh") && !value.contains("edm")) {
                    Object data = endHourMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("edm") && !value.contains("edh")) {
                    Object data = endMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //休憩時間
                if (value.contains("ltm")) {
                    String index = value.substring(3,4);
                    Object lth = lunchTimeHourMap.get("lth" + index);
                     Object ltm = lunchTimeMinMap.get("ltm" + index);

                    String lthValue = value.replace("lth" + index, lth.toString());
                    setValue = lthValue.replace("ltm" + index, ltm.toString());
                }
                if (value.contains("lth") && !value.contains("ltm")) {
                    Object data = lunchTimeHourMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    }else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("ltm") && !value.contains("lth")) {
                    Object data = lunchTimeMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //就業時間合計
                if (value.contains("ttm")) {
                    String index = value.substring(3,4);
                    Object tth = totalHourMap.get("tth" + index);
                    Object ttm = totalMinMap.get("ttm" + index);

                    String tthValue = value.replace("tth" + index, tth.toString());
                    setValue = tthValue.replace("ttm" + index, ttm.toString());
                }
                if (value.contains("tth") && !value.contains("ttm")) {
                    Object data = totalHourMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("ttm") && !value.contains("tth")) {
                    Object data = totalMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //備考データ
                if (value.contains("ot")) {
                    Object data = otherMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                cell.setCellValue(setValue);
               }
            }   
            FileOutputStream out = null;
            out = new FileOutputStream(outputFilePath);
            excel.write(out);
                
        } catch (IOException io){
        io.printStackTrace();
        return;
        }
    }

    private Integer copyFile (String inputFilePath, String outputFilePath) {
        
        File outputFileObject = new File(outputFilePath);
        
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
            e.printStackTrace();
            return 1;
        } catch (IOException io){
            io.printStackTrace();
            return 1;    
        }
        return 0;
    }
}