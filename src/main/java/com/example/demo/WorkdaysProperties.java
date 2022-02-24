package com.example.demo;

public class WorkdaysProperties {

    // public static String folderPath = "C:/久保さん/";
    public static String folderPath = "C:/pleiades/workdays/workdays/src/main/resources/";
    public static String outputFileName = "勤怠表.xls";
    public static String cccExcelFile = "/apache/htdocs/image/test.xlsx";

    public String getInputFolderPath(String companyName) {
        return folderPath + companyName + "/InputFiles";
    }
    
    
    public String getOutputFolderPath(String companyName) {
        return folderPath + companyName + "/OutputFiles";
    }
}
