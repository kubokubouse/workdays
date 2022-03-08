package com.example.demo.service;

import org.apache.poi.ss.usermodel.Row;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.example.demo.WorkdaysProperties;
import com.example.demo.model.Judgeused;
public class CellvalueGet {
    WorkdaysProperties wp;
    
    public Judgeused GetCellvalue(String companyname){
        
        Judgeused judgeused=new Judgeused();
        judgeused.setOa(0);
        judgeused.setOb(0);
        judgeused.setOc(0);
        //使用するテンプレートファイルのパスを定義する
        String inputFilePath = WorkdaysProperties.basePath + "/" + companyname + ".xls";
        //ファイルのコピー
        File inputFileObject = new File(inputFilePath);

        //シート名取得
        List<String> sheetName = new ArrayList<String>();
        try {
            sheetName = getSheetNames(inputFileObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Workbook excel;
        try {
        //エクセルファイルへアクセスするためのオブジェクト
        FileInputStream in = new FileInputStream(inputFilePath);
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
           
                
            //備考1データ
                if (value.contains("oa")) {
                   judgeused.setOa(1);
                }
            //備考2データ
                if (value.contains("ob")) {
                    judgeused.setOb(1);
                }

                //備考3データ
                if (value.contains("oc")) {
                    judgeused.setOc(1);
                }
               
               }
            }   
           
                
        } catch (IOException io){
        io.printStackTrace();
       
        }
        return judgeused;
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
