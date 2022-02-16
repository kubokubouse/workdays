package com.example.demo.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpSession;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Holiday;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.service.HolidayService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;
import com.example.demo.service.WorkdayMapping;
import com.google.gson.Gson;



@Controller
public class SudoController {

    @Autowired
    UserService userService;

    //ファイルアップロード画面表示
	@GetMapping("/templateupload")
	public String getTemplateUpload(){

		return "/templateupload";
	}
    
    //ファイルアップロード処理
    @RequestMapping("/uploadFile")
    public String uploadTemplateFile(@RequestParam("file") MultipartFile multipartFile, Model model) {
    
        File uploadFile = null;
        String fileName = multipartFile.getOriginalFilename();
        String inputFilePath = "C:/pleiades/workdays/workdays/src/main/resources/PropertyFiles/" + fileName;


        // ファイルが空の場合は異常終了
        if(multipartFile.isEmpty()){
        // 異常終了時の処理
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "templateupload";   
        }
        try {
        // アップロードファイルを置く
            uploadFile = new File(inputFilePath);
            uploadFile.createNewFile();
            byte[] bytes = multipartFile.getBytes();
            BufferedOutputStream uploadFileStream =
                new BufferedOutputStream(new FileOutputStream(uploadFile));
            uploadFileStream.write(bytes);
            uploadFileStream.close();   
            
            if (!uploadFile.exists()) {
                model.addAttribute("error", "ファイルのアップロードに失敗しました");        
            }
            
        } catch (Exception e) {
            if (uploadFile.exists()){
                uploadFile.delete();
            }
            model.addAttribute("error", "ファイルのアップロードに失敗しました");
            return "templateupload";
        } catch (Throwable t) {
            if (uploadFile.exists()){
                uploadFile.delete();
            }
            model.addAttribute("error", "ファイルのアップロードに失敗しました");
            return "templateupload";
        }
        model.addAttribute("error", "ファイルがアップロードされました");
        return "templateupload";
    }

    //ファイル一覧を表示
    @RequestMapping("/templatelist")
    public String showTemplateFileList(Model model) {

        List<String> fileNameList = new ArrayList<String>();
        String fileFolderPath = "C:/pleiades/workdays/workdays/src/main/resources/PropertyFiles";
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
        
        if (fileList != null) {
            for (File file : fileList){
                fileNameList.add(file.getName());
            }
        } else {
            model.addAttribute("fileName", "ファイルが存在しません");
            return "templatelist";
        }
        model.addAttribute("fileName", fileNameList);
        return "templatelist";
    }

    //ファイル一覧からファイル削除
    @RequestMapping("/filedelete")
    public String showTemplateFileList(@RequestParam("deleteFileName") String deleteFileName, Model model) {
        String folderPath = "C:/pleiades/workdays/workdays/src/main/resources/PropertyFiles/";

        File deleteFile = new File(folderPath + deleteFileName);
        if(!deleteFile.delete()){
            model.addAttribute("error", "ファイルが削除できませんでした");
        }
        model.addAttribute("error", deleteFileName+"が削除されました");
        return "templatelist";
    }
    


}


