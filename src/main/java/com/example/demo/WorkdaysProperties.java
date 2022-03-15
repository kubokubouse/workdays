package com.example.demo;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;

public class WorkdaysProperties {

    public static String host = "http://localhost:8080";
    public static String basePath = "C:/pleiades/workdays/workdays/src/main/resources";
    // public static String basePath = "C:/久保さん/cloneproject/workdays/src/main/resources";
    public static String outputFileName = "勤怠表.xls";
    public static String cccExcelFile = "/apache/htdocs/image/test.xlsx";
    public static String fromMailAdress="r-kubo@connectcrew.co.jp";
    public static String userRegisterText="/mail/userregister.txt";
    public static String loginText="/mail/login.txt";
    public static String onetimeText="/mail/onetime.txt";
    //※boxClientIdを変更した場合はdone.htmlページのリンクも編集する事
    public static String boxClientId = "4u7hb7ffjwrpl9k1ojfsf09g58eyqwt6";
	public static String boxClientSecret = "wE7OWwmsH0V1Z1wYscLQUaOgTpiUqNvJ";
    public static String boxSaveFolderName = "勤怠管理システム";

    
    public File getInputFolder(int companyID) {
        File inputFile = new File(basePath + "//" + companyID + "_input");
        return inputFile;
    }
    public File getOutputFolder(int companyID) {
        File outputFile = new File(basePath + "//" + companyID + "_output");
        return outputFile;
    }
    public String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime());
        long timeInMilliSeconds = date.getTime();
        java.sql.Date date1 = new java.sql.Date(timeInMilliSeconds);
        return date1.toString();
    }
    
}
