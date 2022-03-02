package com.example.demo.controller;

import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.*;
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
import com.example.demo.service.IndividualService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.UserListParam;
import com.example.demo.model.IndividualData;
import com.example.demo.model.SuperUser;
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

    @Autowired
    MailSendService mailSendService;

    @Autowired
    IndividualService individualService;
    

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
	public String register(@ModelAttribute IndividualData idData, Model model, BindingResult result){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
		return "register";
	}

    //会員情報入力→確認画面へ
	@PostMapping("/")
	public String confirm( @ModelAttribute IndividualData idData,Model model,BindingResult result){
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
        model.addAttribute("idData", idData);
		return "confirm";
	}

	//会員情報をDBに登録
	@PostMapping("/regist")
    public String regist(@Validated @ModelAttribute IndividualData idData, BindingResult result, Model model){
		/*model.addAttribute("user", idRepository.findAll());
        if (result.hasErrors()){

			return "confirm";
		}*/

        int companyID = (int)session.getAttribute("companyId");
        String mail=idData.getMail();
        int individualId=1;
        String name=idData.getName();
        String company1=idData.getCompany1();
        String company2=idData.getCompany2();
        String company3=idData.getCompany3();
        String number=idData.getNumber();

        
        int i=individualService.insert(companyID, mail, individualId,name,0,1,company1,company2,company3);
        
        User universalUser=userService.findEmail(mail);
        if(universalUser==null){
           mailSendService.mailsend(mail,WorkdaysProperties.userRegisterText);
        }
        mailSendService.mailsend(mail,WorkdaysProperties.loginText);
		return "superuser";
    }

    //ユーザー編集画面
	@GetMapping("/userlist")
	public String userlist(@Validated  Model model){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
        int companyID=(int)session.getAttribute("companyId");
		List<IndividualData> iList = userService.findCompanyID(companyID);
		model.addAttribute("userListParam", iList);
		return "userlist";
	}

    //個別ユーザー削除
    //ここで使用されるユーザーIDはメアドのこと
	@RequestMapping(value="/userdelete")
	public String deleteuser(@RequestParam String userid, Model model){
		System.out.println(userid);
		userService.deleteIndividualData(userid);
        int companyId = (Integer)session.getAttribute("companyId");
		List<IndividualData> iList = userService.findCompanyID(companyId);
		model.addAttribute("userListParam", iList);
		return "userlist";
	}

    //個別ユーザー更新機能
	@PostMapping("/UserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String userid, Model model){	

        int companyId = (Integer)session.getAttribute("companyId");
		IndividualData iData = idRepository.findByMailAndCompanyID(
            userid, companyId);
        System.out.println(name);
        System.out.println(inputvalue);

		switch (name) {
			case "number":
			  iData.setNumber(inputvalue);
			  break;
			case "name":
              iData.setName(inputvalue);
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
		
		List<IndividualData> iList = userService.findCompanyID(companyId);
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
            uploadFile = new File(getInputFolder(companyId).getPath() +"//"+ fileName);
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

    //ユーザー一括アップロード画面表示
    @GetMapping("/alluserupload")
    public String allUserUpload() {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser != null) {
            return "alluserupload";
        }
		return "accessError";
	}

    //ユーザー一括アップロード処理
    @RequestMapping("/uploadalluser")
    public String uploadalluser(@RequestParam("file") MultipartFile multipartFile,
    Model model) throws IOException {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        int companyID = superUser.getCompanyID();

        //全ユーザー削除
        List<IndividualData> idList = userService.findCompanyID(companyID);
        for (IndividualData id : idList) {
            userService.deleteIndividualData(id);
        }

        // ファイルが空の場合は異常終了
        if(multipartFile.isEmpty()){
        // 異常終了時の処理
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "alluserupload";   
        }

        //格納先のフルパス
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

        if(!uploadFile.exists()) {
        // 異常終了時の処理
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "alluserupload";  
        }

        int i = 0;
        List<IndividualData> newIdList = new ArrayList<IndividualData>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {            
            br = new BufferedReader(new FileReader(uploadFile));
            // readLineで一行ずつ読み込む
            String line; // 読み込み行
            String[] data; // 分割後のデータを保持する配列
            while ((line = br.readLine()) != null) {

                if (i == 0) {
                    //カンマで分割した内容を配列に格納する
                    String[] header = line.split(",");

                    // Listの中でヘッダー項目が何か
                    String number = header[0];
                    //マッピングする
                    map.put(number, 0);

                    String name = header[1];
                    map.put(name, 1);

                    String mail = header[2];
                    map.put(mail, 2);

                    String company1 = header[3];
                    map.put(company1, 3);
                    
                    String company2 = header[4];
                    map.put(company2, 4);

                    String company3 = header[5];
                    map.put(company3, 5);

                    String banned = header[6];
                    map.put(banned, 6);

                } else {

                    IndividualData idData = new IndividualData();

                // lineをカンマで分割し、配列dataに設定
                    data = line.split(",");

                    //mapから何番目にデータが格納されているか取得
                    if (map.containsKey("社員番号")){
                        int number = map.get("社員番号");
                        idData.setNumber(data[number]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：社員番号"); 
                        return "alluserupload";  
                    }
                    if (map.containsKey("氏名")) {
                        int name = map.get("氏名");
                        idData.setName(data[name]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：氏名"); 
                        return "alluserupload";  
                    }

                    if (map.containsKey("メールアドレス")){
                        int mail = map.get("メールアドレス");
                        idData.setMail(data[mail]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：氏名"); 
                        return "alluserupload";  
                    }

                    if (map.containsKey("会社１")){
                        int company1 = map.get("会社１");
                        idData.setCompany1(data[company1]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：会社１(全角)"); 
                        return "alluserupload";  
                    }

                    if (map.containsKey("会社２")){
                        int company2 = map.get("会社２");
                        idData.setCompany2(data[company2]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：会社２(全角)"); 
                        return "alluserupload";  
                    }
                    if (map.containsKey("会社３")){
                        int company3 = map.get("会社３");
                        idData.setCompany3(data[company3]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：会社３(全角)"); 
                        return "alluserupload";  
                    }
                    String banned = null;
                    if (map.containsKey("利用可否")){
                        int bannedIndex = map.get("利用可否");
                        banned = data[bannedIndex];
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：利用可否"); 
                        return "alluserupload";  
                    }
                    if (banned.equals("可")){
                        idData.setBanned(0);
                    } else {
                        idData.setBanned(1);
                    }  
                    idData.setCompanyID(companyID);
                    newIdList.add(idData);
                }
                i++;
            }
            for (IndividualData id : newIdList) {

                String mail = id.getMail();

            //ユーザーが既に登録されているか確認
                User universalUser=userService.findEmail(mail);
                if(universalUser==null){
                 mailSendService.mailsend(mail,WorkdaysProperties.userRegisterText);
                    id.setRegistered(1);
                } else {
                    id.setRegistered(0);
                }         
            }
                   
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "alluserupload";
        } finally {
            br.close();
            uploadFile.delete();
        }

        // int companyID,String mail,int individual_id,String name,int banned,int registered, String company1,String company2,String company3,String number
        for (IndividualData id : newIdList) {
            int insert = individualService.insert(
                id.getCompanyID(), id.getMail(), Integer.valueOf(id.getNumber()), id.getName(),id.getBanned(),
                 id.getRegistered(), id.getCompany1(), id.getCompany2(), id.getCompany3()
            );
        }
          
        model.addAttribute("error", "ファイルがアップロードされました");
        return "alluserupload";
    }
  
}