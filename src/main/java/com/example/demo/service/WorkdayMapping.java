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
                String index = value.substring(3,4);
                String setValue = null;
            //始業時刻
                if (value.contains("sth")) {
                    Object data = stHourMap.get(value);
            //データがNullの時
                    if (data == null) {
                        setValue = value.replace(value, " ");;
            //セルに2つのKeyがある時（例　sth1:stm1）
                    } else if (value.contains("stm")) {
                        Object sth = stHourMap.get("sth" + index);
                        Object stm = stMinMap.get("stm" + index);
                        
                        String sthValue = value.replace("sth" + index, sth.toString());
                        setValue = sthValue.replace("stm" + index, stm.toString());
                    } else {
            //セルの値にKeyが１つのとき（例　sth1　のみ）
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("stm")) {
                    Object data = stMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } 
            //セルの値にKeyが１つのとき（例　stm1　のみ）
                    if (!value.contains("sth")) {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //終業時刻
                if (value.contains("edh")) {
                    Object data = endHourMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    } else if (value.contains("edm")) {
                        Object edh = endHourMap.get("edh" + index);
                        Object edm = endMinMap.get("edm" + index);

                        String edhValue = value.replace("edh" + index, edh.toString());
                        setValue = edhValue.replace("edm" + index, edm.toString());
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("edm")) {
                    Object data = endMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    }
                    if (!value.contains("edh")) {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //休憩時間
                if (value.contains("lth")) {
                    Object data = lunchTimeHourMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    }else if (value.contains("ltm")) {
                        Object lth = lunchTimeHourMap.get("lth" + index);
                        Object ltm = lunchTimeMinMap.get("ltm" + index);

                        String lthValue = value.replace("lth" + index, lth.toString());
                        setValue = lthValue.replace("ltm" + index, ltm.toString());
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("ltm")) {
                    Object data = lunchTimeMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    }
                    if (!value.contains("lth")) {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //就業時間合計
                if (value.contains("tth")) {
                    Object data = totalHourMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");
                    } else if (value.contains("ttm")) {
                        Object tth = totalHourMap.get("tth" + index);
                        Object ttm = totalMinMap.get("ttm" + index);

                        String tthValue = value.replace("tth" + index, tth.toString());
                        setValue = tthValue.replace("ttm" + index, ttm.toString());
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
                if (value.contains("ttm")) {
                    Object data = totalMinMap.get(value);
                    if (data == null) {
                        setValue = value.replace(value, " ");;
                    }
                    if (!value.contains("tth")) {
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
                FileOutputStream out = null;
                out = new FileOutputStream(outputFilePath);
                excel.write(out);
                }
            }
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