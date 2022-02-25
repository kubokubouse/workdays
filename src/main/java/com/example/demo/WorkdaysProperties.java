package com.example.demo;

public class WorkdaysProperties {

    public static String inputFolder = "C:/久保さん/PropertyFiles";
    public static String outputFolder = "C:/久保さん/OutputFiles";
    public static String outputFileName = "勤怠表.xls";
    public static String cccExcelFile = "/apache/htdocs/image/test.xlsx";
    public static String fromMailAdress = "r-kubo@connectcrew.co.jp";
    public static String userRegisterText = "/mail/userregistre.text";
    public static String loginText = "/mail/login.text";
    //久保さん
	// String inputFolder = "C:/久保さん/PropertyFiles";
	// String outputFolder = "C:/久保さん/OutputFiles;
    
    //竹林さん
    //public static String inputFolder = "C:/pleiades/workdays/workdays/src/main/resources/PropertyFiles";
    //public static String outputFolder = "C:/pleiades/workdays/workdays/src/main/resources/OutputFiles";

    public String getOutputFile() {
        return outputFolder + "/" + outputFileName;
    }
    
}
