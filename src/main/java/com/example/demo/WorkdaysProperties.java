package com.example.demo;

public class WorkdaysProperties {

    public static String inputFolder = "C:/pleiades/workdays/workdays/src/main/resources/PropertyFiles";
    public static String outputFolder = "C:/pleiades/workdays/workdays/src/main/resources/OutputFiles";
    public static String outputFileName = "勤怠表.xls";

    //久保さん
	// String inputFolder = "C:/久保さん/PropertyFiles";
	// String outputFolder = "C:/久保さん/OutputFiles;

    public String getOutputFile() {
        return outputFolder + "/" + outputFileName;
    }
    
}
