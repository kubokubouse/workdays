package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.File;
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
import com.example.demo.repository.UserRepository;



@Controller
public class AdminController {

    private final UserRepository repository;

    @Autowired
    HolidayService holidayService;
    public AdminController(UserRepository repository){
        this.repository = repository;
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
        String id = (String)session.getAttribute("id");
        String pass = (String)session.getAttribute("pass");

        SuperUserLogin superUser = userService.findIdAndPass(id, pass);
        if (superUser == null) {
            return "accessError";
        }
		return "superuser";
	}


    //会員登録ページに移行
	@GetMapping("/register")
	public String register(@ModelAttribute User user){
        String id = (String)session.getAttribute("id");
        String pass = (String)session.getAttribute("pass");

        SuperUserLogin superUser = userService.findIdAndPass(id, pass);
        if (superUser == null) {
            return "accessError";
        }
		return "register";
	}

    //会員情報入力→確認画面へ
	@PostMapping("/")
	public String confirm(@Validated @ModelAttribute User user,BindingResult result){
		//メアドに重複があった場合重複画面に飛ぶ
		User usedEmailuser=userService.findEmail(user.getEmail());
		if(usedEmailuser!=null){
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
    public String regist(@Validated @ModelAttribute User user, BindingResult result, Model model){
		
		model.addAttribute("user", repository.findAll());
        if (result.hasErrors()){
			return "confirm";
		}
		// COMMENTテーブル：コメント登録
		repository.save(user);
		// ルートパス("/") にリダイレクトします
		return "superuser";
    }

    //ユーザー編集画面
	@GetMapping("/userlist")
	public String userlist(@Validated  Model model){

        String id = (String)session.getAttribute("id");
        String pass = (String)session.getAttribute("pass");

        SuperUserLogin superUser = userService.findIdAndPass(id, pass);
        if (superUser == null) {
            return "accessError";
        }

		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		return "/userlist";
	}

	@RequestMapping(value="/userdelete")
	public String deleteuser(@RequestParam String userid, Model model){
		System.out.println(userid);
		userService.delete(Integer.parseInt(userid));
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		return "/userlist";
	}


	@PostMapping("/UserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String userid, Model model){	
		User user=userService.findId(Integer.parseInt(userid));
		switch (name) {
			case "lastname":
			  user.setLastname(inputvalue);
			  break;
			case "firsttname":
			  user.setFirstname(inputvalue);
			  break;
			  case "email":
			  user.setEmail(inputvalue);
			  break;
			case "password":
			  user.setPassword(inputvalue);
			  break;
			case "company1":
			  user.setCompany1(inputvalue);
			  break;
			case "company2":
			  user.setCompany2(inputvalue);
			  break;
			case "company3":
			  user.setCompany2(inputvalue);
			  break;
			} 
		userService.update(user);
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		return "/userlist";
	}

	//ファイルアップロード画面表示
	@GetMapping("/templateupload")
	public String getTemplateUpload(){

        String id = (String)session.getAttribute("id");
        String pass = (String)session.getAttribute("pass");

        SuperUserLogin superUser = userService.findIdAndPass(id, pass);
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
        String inputFilePath = "/久保さん/PropertyFiles" + fileName;


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

        String id = (String)session.getAttribute("id");
        String pass = (String)session.getAttribute("pass");

        SuperUserLogin superUser = userService.findIdAndPass(id, pass);
        if (superUser == null) {
            return "accessError";
        }

        List<String> fileNameList = new ArrayList<String>();
        String fileFolderPath = "/久保さん/PropertyFiles";
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
        String folderPath = "/久保さん/PropertyFiles";

        File deleteFile = new File(folderPath + deleteFileName);
        if(!deleteFile.delete()){
            model.addAttribute("error", "ファイルが削除できませんでした");
        }
        model.addAttribute("error", deleteFileName+"が削除されました");
        return "templatelist";
    }
}