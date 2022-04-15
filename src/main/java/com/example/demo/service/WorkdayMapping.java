package com.example.demo.service;

import org.apache.poi.ss.usermodel.Row;
import java.io.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class WorkdayMapping {
    
    public List<String> outputExcel(String inputFilePath, String outputFilePath,
        Map stHourMap, Map stMinMap, Map endHourMap, Map endMinMap, Map lunchTimeHourMap, 
            Map lunchTimeMinMap, Map totalHourMap, Map totalMinMap, Map other1Map, Map other2Map,
                Map other3Map, String email, int id, String name, int year, int month) {

    List<String> errorList = new ArrayList<String>();
    File inputFileObject = new File(inputFilePath);
    File outputFile = new File(outputFilePath);
    
    if (outputFile.exists()) {
        outputFile.delete();
    }

    if (!inputFileObject.exists()) {
        System.out.println("ファイルが存在しません：" + inputFilePath);
        errorList.add("テンプレートファイルが存在しません" );
        return errorList;
    }
    
    //ファイルのコピーが成功したときは０が戻り値となる
    int copyFiles = copyFile(inputFilePath, outputFilePath);

    if (copyFiles != 0) {
        errorList.add("ファイルの読み込みに失敗しました");
        return errorList;
    }
       
    //シート名を取得
    List<String> sheetName = new ArrayList<String>();
    try {
        sheetName = getSheetNames(inputFileObject);
    } catch (Exception e) {
        e.printStackTrace();
    }


    Workbook excel;
    try {
    //エクセルファイルへアクセスするためのオブジェクト
        FileInputStream in = new FileInputStream(outputFilePath);
        excel = WorkbookFactory.create(in);
   
    // シート名がわかっている場合
        Sheet sheet = excel.getSheet(sheetName.get(0));

    //行
        Row row;
        for (int i = 0; i < sheet.getLastRowNum()+1; i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            for (int j = 0; j < row.getLastCellNum()+1; j++ ) {
            //セル
                Cell cell = row.getCell(j);
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
                String setValue = null;
                int intValue = 1000;
            //始業時刻
            //セルに2つのKeyがある時（例　sth1:stm1）
                if (value.contains("[stm") && value.contains("[sth")) {
            //Indexの値が数字か判定
                    String index = value.substring(4,6);
                    boolean isNumeric =  index.matches("[+-]?\\d*(\\.\\d+)?");
                    if (!isNumeric) {
                        index = value.substring(4,5);
                    }
                    Object sth = stHourMap.get("[sth" + index +"]");
                    Object stm = stMinMap.get("[stm" + index +"]");

            //エクセルに記入されたsthの数がstHourMap内の値の数より多いとNullになる
                    if (sth == null) {
                        cell.setCellValue("");
                        continue;
                    }
                
                    String sthValue = value.replace("[sth" + index + "]", sth.toString());
                    setValue = sthValue.replace("[stm" + index + "]", stm.toString());
                } else {
                    setValue = value;
                }
            //セルの値にKeyが１つのとき（例　sth1　のみ）
                if (value.contains("[sth") && !value.contains("[stm")) {
                    Object data = stHourMap.get(value);
            //データがNullの時
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }
            //セルの値にKeyが１つのとき（例　stm1　のみ）
                if (value.contains("[stm") && !value.contains("[sth")) {
                    Object data = stMinMap.get(value);
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }
                
            //終業時刻
                if (value.contains("[edh") && value.contains("[edm")) {
                    String index = value.substring(4,6);
                    boolean isNumeric =  index.matches("[+-]?\\d*(\\.\\d+)?");
                    if (!isNumeric) {
                        index = value.substring(4,5);
                    }
                    Object edh = endHourMap.get("[edh" + index +"]");
                    Object edm = endMinMap.get("[edm" + index +"]");

                    if (edh == null) {
                        cell.setCellValue("");
                        continue;
                    }

                    String edhValue = value.replace("[edh" + index + "]", edh.toString());
                    setValue = edhValue.replace("[edm" + index + "]", edm.toString());
                }
                if (value.contains("[edh") && !value.contains("[edm")) {
                    Object data = endHourMap.get(value);
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }
                if (value.contains("[edm") && !value.contains("[edh")) {
                    Object data = endMinMap.get(value);
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }

            //休憩時間
                if (value.contains("[ltm") && value.contains("[lth")) {
                    String index = value.substring(4,6);
                    boolean isNumeric =  index.matches("[+-]?\\d*(\\.\\d+)?");
                    if (!isNumeric) {
                        index = value.substring(4,5);
                    }
                    Object lth = lunchTimeHourMap.get("[lth" + index +"]");
                    Object ltm = lunchTimeMinMap.get("[ltm" + index +"]");

                    if (lth == null) {
                        cell.setCellValue("");
                        continue;
                    }

                    String lthValue = value.replace("[lth" + index + "]", lth.toString());
                    setValue = lthValue.replace("[ltm" + index + "]", ltm.toString());
                }
                if (value.contains("[lth") && !value.contains("[ltm")) {
                    Object data = lunchTimeHourMap.get(value);
                    if (data == null) {
                        continue;
                    }else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }
                if (value.contains("[ltm") && !value.contains("[lth")) {
                    Object data = lunchTimeMinMap.get(value);
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }

            //一日の就業時間
                if (value.contains("[ttm") && value.contains("[tth")) {
                    String index = value.substring(4,6);
                    boolean isNumeric =  index.matches("[+-]?\\d*(\\.\\d+)?");
                    if (!isNumeric) {
                        index = value.substring(4,5);
                    }
                    Object tth = totalHourMap.get("[tth" + index +"]");
                    Object ttm = totalMinMap.get("[ttm" + index +"]");

                    if (tth == null) {
                        cell.setCellValue("");
                        continue;
                    }

                    String tthValue = value.replace("[" + "tth" + index + "]", tth.toString());
                    setValue = tthValue.replace("[" +"ttm" + index + "]", ttm.toString());
                }
                if (value.contains("[tth") && !value.contains("[ttm")) {
                    Object data = totalHourMap.get(value);
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }
                if (value.contains("[ttm") && !value.contains("[tth")) {
                    Object data = totalMinMap.get(value);
                    if (data == null) {
                        continue;
                    } else {
                        intValue = Integer.valueOf(value.replace(value, data.toString()));
                    }
                }

            //時間はTime型として保存

            Date timeValue = null;
            if (setValue.contains(":")){              
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            // Date型変換
            try {
                timeValue = sdf.parse(setValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }            
            }
             
            //備考1データ
                if (value.contains("[oa")) {
                    Object data = other1Map.get(value);
                    if (data == null) {
                        cell.setCellValue("");
                        continue;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }
            //備考2データ
                if (value.contains("[ob")) {
                    Object data = other2Map.get(value);
                    if (data == null) {
                        cell.setCellValue("");
                        continue;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }

                //備考3データ
                if (value.contains("[oc")) {
                    Object data = other3Map.get(value);
                    if (data == null) {
                        cell.setCellValue("");
                        continue;
                    } else {
                        setValue = value.replace(value, data.toString());
                    }
                }

                //メールアドレス
                if (value.contains("[MAIL]")) {
                    setValue = value.replace("[MAIL]", email);
                }
                //名前
                if (value.contains("[NAME]")) {
                    setValue = value.replace("[NAME]", name);
                }                
                //管理番号
                if (value.contains("[ID]")) {
                    setValue = value.replace("[ID]", String.valueOf(id));
                }
                //年と月が同じセル内にある場合
                if (value.contains("[YEAR]") && value.contains("[MONTH]")) {
                    String yearValue = value.replace("[YEAR]", String.valueOf(year));
                    setValue = yearValue.replace("[MONTH]", String.valueOf(month));
                }
                //年のみ
                if (!value.contains("[MONTH]") && value.contains("[YEAR]")) {
                    setValue = value.replace("[YEAR]", String.valueOf(year));
                }
                //月のみ
                if (!value.contains("[YEAR]") && value.contains("[MONTH]")) {
                    setValue = value.replace("[MONTH]", String.valueOf(month));
                }
                //sth:stmなどコロンがある時
                if (timeValue != null) {
                    cell.setCellValue(timeValue);
                    continue;
                }
                //時間タグがsthのみなど時と分で分かれているとき
                if (intValue != 1000) {
                    cell.setCellValue(intValue);
                    continue;
                }
                cell.setCellValue(setValue);
               }
            }   
            FileOutputStream out = null;
            out = new FileOutputStream(outputFilePath);
            excel.write(out);
                
        } catch (IOException io){
        io.printStackTrace();
        errorList.add("ファイルの出力に失敗しました");
        return errorList;
        }
        return errorList;
    }

    public Integer copyFile (String inputFilePath, String outputFilePath) {
        
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

    private List<String> getSheetNames(File file) throws Exception {

        try (Workbook book = WorkbookFactory.create(file)) {

            return IntStream.range(0, book.getNumberOfSheets())
                    .mapToObj(book::getSheetAt)
                    .map(Sheet::getSheetName)
                    .collect(Collectors.toList());
        }
    }
}