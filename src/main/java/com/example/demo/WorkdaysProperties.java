package com.example.demo;

import java.io.File;

public class WorkdaysProperties {

    public static String basePath = "C:/pleiades/workdays/workdays/src/main/resources/";
    // public static String basePath = "C:/久保さん/cloneproject/workdays/src/main/resources
    public static String outputFileName = "勤怠表.xls";
    public static String cccExcelFile = "/apache/htdocs/image/test.xlsx";
    public static String fromMailAdress="r-kubo@connectcrew.co.jp";
    public static String userRegisterText="/mail/userregister.txt";
    public static String loginText="/mail/login.txt";

    
    public File getInputFolder(int companyID) {
        File inputFile = new File(basePath + companyID + "_input");
        return inputFile;
    }
    public File getOutputFolder(int companyID) {
        File outputFile = new File(basePath + companyID + "_output");
        return outputFile;
    }
    
}
