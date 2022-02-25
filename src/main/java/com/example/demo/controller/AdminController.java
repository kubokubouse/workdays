package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.*;
import java.nio.*;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

import com.example.demo.model.User;

import com.example.demo.service.HolidayService;
import com.example.demo.service.UserService;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.UserListParam;
import com.example.demo.model.IndividualData;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.IndividualDataRepository;
import com.example.demo.WorkdaysProperties;


@Controller
public class AdminController extends WorkdaysProperties{

    private final UserRepository repository;
    private final IndividualDataRepository idRepository;

    @Autowired
    HolidayService holidayService;
    public AdminController(UserRepository repository, IndividualDataRepository idRepo){
        this.repository = repository;
        this.idRepository = idRepo;
    }

	@Autowired
    HttpSession session;
	
	@Autowired
    UserService userService;

    

	@GetMapping("/logout")
	public String logout(){
		
		return "/logout";
	}

    //管理メニュー画面
    //会員登録ページに移行
	@GetMapping("/superuser")
	public String menue(@ModelAttribute User user){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        
        if (superUser == null) {
            return "accessError";
        }
		return "superuser";
	}


    //会員登録ページに移行
	@GetMapping("/register")
	public String register(@ModelAttribute IndividualData idData, BindingResult result, Model model){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
		return "register";
	}

    //会員情報入力→確認画面へ
	@PostMapping("/")
	public String confirm(@Validated @ModelAttribute IndividualData idData,BindingResult result){
		//メアドに重複があった場合重複画面に飛ぶ
        IndividualData iData = idRepository.findByMailAndCompanyID(
            idData.getMail(), (Integer)session.getAttribute("companyId"));
		if(iData!=null){
			return "usedemail";
		}
        
        if (result.hasErrors()){
			// エラーがある場合、index.htmlに戻る
            return "register";
        }
		return "confirm";
	}

	//会員情報をDBに登録
	@PostMapping("/regist")
    public String regist(@Validated @ModelAttribute IndividualData idData, BindingResult result, Model model){
		
		model.addAttribute("user", idRepository.findAll());
        if (result.hasErrors()){
			return "confirm";
		}

        //個別ユーザーテーブルに登録
        int companyID = Integer.parseInt((String)session.getAttribute("companyId"));
        idData.setCompanyID(companyID);
        idData.setRegistered(1);
        idData.setBanned(0);

        idRepository.save(idData);

		return "superuser";
    }

    //ユーザー編集画面
	@GetMapping("/userlist")
	public String userlist(@Validated  Model model){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }

		List<IndividualData> iList = userService.searchAllIndividualData();
		model.addAttribute("userListParam", iList);
		return "userlist";
	}

	@RequestMapping(value="/userdelete")
	public String deleteuser(@RequestParam String userid, Model model){
		System.out.println(userid);
		userService.deleteIndividualData(userid);
		List<IndividualData> iList = userService.searchAllIndividualData();
		model.addAttribute("userListParam", iList);
		return "userlist";
	}


	@PostMapping("/UserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String userid, Model model){	

        int companyId = (Integer)session.getAttribute("companyId");
		IndividualData iData = idRepository.findByMailAndCompanyID(
            name, companyId);
        System.out.println(name);
        System.out.println(inputvalue);

		switch (name) {
			case "number":
			  iData.setNumber(inputvalue);
			  break;
			case "name":
              iData.setName(name);
			  break;
			  case "mail":
			  iData.setMail(inputvalue);
			  break;
			case "company1":
			  iData.setCompany1(inputvalue);
			  break;
            case "company2":
			  iData.setCompany2(inputvalue);
			  break;
            case "company3":
			  iData.setCompany3(inputvalue);
			  break;
			} 
		userService.updateIndividualData(iData);
		
		List<IndividualData> iList = userService.searchAllIndividualData();
		model.addAttribute("userListParam", iList);
		return "userlist";
	}

	//ファイルアップロード画面表示
	@GetMapping("/templateupload")
	public String getTemplateUpload(){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser != null) {
            return "templateupload";
        }
		return "accessError";
	}
    
    //ファイルアップロード処理
    @RequestMapping("/uploadFile")
    public String uploadTemplateFile(@RequestParam("file") MultipartFile multipartFile, Model model) {
    
        File uploadFile = null;
        String fileName = multipartFile.getOriginalFilename();

        // ファイルが空の場合は異常終了
        if(multipartFile.isEmpty()){
        // 異常終了時の処理
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "templateupload";   
        }
        try {
        // アップロードファイルを置く
            int companyId = (Integer)session.getAttribute("companyId");
            uploadFile = new File(getInputFolder(companyId).getPath() + fileName);
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

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }

        List<String> fileNameList = new ArrayList<String>();
        int companyId = (Integer)session.getAttribute("companyId");
        String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
        
        if (fileList != null) {
            for (File file : fileList){
                fileNameList.add(file.getName());
            }
        } else {
            model.addAttribute("error", "ファイルが存在しません");
            return "templatelist";
        }
        model.addAttribute("fileName", fileNameList);
        return "templatelist";
    }

    //ファイル一覧からファイル削除
    @RequestMapping("/filedelete")
    public String showTemplateFileList(@RequestParam("deleteFileName") String deleteFileName, Model model) {
        
        int id = (Integer)session.getAttribute("companyId");
        File folder = getInputFolder(id);
        String deleteFilePath = folder.toPath() + "/" + deleteFileName;
        File deleteFile = new File(deleteFilePath);
        if(!deleteFile.delete()){
            model.addAttribute("error", "ファイルが削除できませんでした");
        }
        model.addAttribute("error", deleteFileName+"が削除されました");
        return "templatelist";
    }

    //一括アップロード
    @GetMapping("/alluserupload")
    public String allUserUpload() {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser != null) {
            return "alluserupload";
        }
		return "accessError";
	}

    //ファイルアップロード処理
    @RequestMapping("/uploadalluser")
    public String uploadalluser(@RequestParam("file") MultipartFile multipartFile,
    @ModelAttribute IndividualData idData, Model model) {

        // ファイルが空の場合は異常終了
        if(multipartFile.isEmpty()){
        // 異常終了時の処理
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "templateupload";   
        }

        //格納先のフルパス ※事前に格納先フォルダ「UploadTest」をCドライブ直下に作成しておく
        String filePath = WorkdaysProperties.basePath + "/upload.csv";

        try {
            byte[] bytes = multipartFile.getBytes();
            BufferedOutputStream uploadFileStream =
                new BufferedOutputStream(new FileOutputStream(filePath));
            uploadFileStream.write(bytes);
            uploadFileStream.close();  
        } catch (IOException e) {
            e.printStackTrace();
        }

        // アップロードファイルから値を読み込んで保存する
        BufferedReader br = null;
        File uploadFile = new File(filePath);
        try {
            
            br = new BufferedReader(new FileReader(uploadFile));
            // readLineで一行ずつ読み込む
            String line; // 読み込み行
            String[] data; // 分割後のデータを保持する配列
            while ((line = br.readLine()) != null) {
                // lineをカンマで分割し、配列dataに設定
                    data = line.split(",");
                     
                for (String value : data) {
                    idData.setNumber(value);
                    idData.setName(value);
                    idData.setMail(value);
                    idData.setCompany1(value);
                    idData.setCompany2(value);
                    idData.setCompany3(value);
                    idData.setBanned(Integer.valueOf(value));                                        
                }
                idData.setRegistered(0);

                idRepository.save(idData);
                
                br.close();
                uploadFile.delete();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "uploadalluser";
        } 
        model.addAttribute("error", "ファイルがアップロードされました");
        return "uploadalluser";
    }
  

}