package com.example.demo;

public class WorkdaysProperties {

    public static String inputFolder = "C:/久保さん/PropertyFiles";
    public static String outputFolder = "C:/久保さん/OutputFiles";
    public static String outputFileName = "勤怠表.xls";
    public static String cccExcelFile = "/apache/htdocs/image/test.xlsx";

    //久保さん
	// String inputFolder = "C:/久保さん/PropertyFiles";
	// String outputFolder = "C:/久保さん/OutputFiles;

    public String getOutputFile() {
        return outputFolder + "/" + outputFileName;
    }
    
}
