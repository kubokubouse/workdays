package com.example.demo.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.*;
import org.apache.commons.collections4.CollectionUtils;
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
import com.box.sdk.*;
import com.box.sdk.BoxItem.Info;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.nio.file.Paths;
import javax.swing.JFileChooser;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import com.example.demo.service.ContractService;
import com.example.demo.service.HolidayService;
import com.example.demo.service.IndividualService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.SuperUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;
import com.example.demo.model.User;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.BeanFile;
import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.ContractData;
import com.example.demo.model.IdUser;
import com.example.demo.model.IndividualData;
import com.example.demo.model.SuperUser;
import com.example.demo.model.MasterUser;
import com.example.demo.model.Otherpa;
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
    SuperUserService superUserService;

    @Autowired
    MailSendService mailSendService;

    @Autowired
    IndividualService individualService;

    @Autowired
    ContractService contractService;
    @Autowired
    WorkdaysService workdaysService;
    

	@GetMapping("/logout")
	public String logout(){
		session.removeAttribute("api");
		return "logout";
	}

    //管理メニュー画面
    //管理者画面から勤怠データ操作画面へ
    @GetMapping("/superworkdays")
    public String sworkdays(@ModelAttribute YearMonth yaerMonth,BeanRegularTime Brtime,Model model ){
        SuperUserLogin superUser=(SuperUserLogin)session.getAttribute("superUser");
        User users=userService.findEmail(superUser.getEmail());

        if(users.getBanned()==1){
			return "banned";
		}
		//ログイン時に入力されたメアドを使っている個別ユーザーを特定しリストを作る
		List <IndividualData>iDataList=individualService.findMail(superUser.getEmail());
		model.addAttribute("iDataList", iDataList);
		
		
		//個別ユーザーから会社IDを取得し会社名のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
		
		for(IndividualData iData:iDataList){
			IdUser idUser=individualService.getIdUser(iData);
			
			idUserList.add(idUser);
		}

		model.addAttribute("idUserList", idUserList);
		
		session.setAttribute("Data",users);
		
		YearMonth yearMonth=userService.nowYearMonth();
		session.setAttribute("yearMonth",yearMonth);
		
		//年月を渡し勤怠データの有無の確認→データなしなら新規追加
		workdaysService.checkYearMonth(yearMonth.getYear(),yearMonth.getMonth());
		
		//年月を渡し勤怠データをDBからリスト形式で取得
		WorkingListParam workingListParam=workdaysService.date(yearMonth.getYear(),yearMonth.getMonth());
		//WorkingListParam workingListParam = userService.searchAll(users);//リストに出す用の検索は必要
		workingListParam.setMonth(yearMonth.getMonth());
		workingListParam.setYear(yearMonth.getYear());
		model.addAttribute("workingListParam", workingListParam);
        model.addAttribute("users", users);
		//備考1,2,3に会社名を添付する処理をする
		//会社のテンプレートファイルでoa,ob,coが使われているかジャッジ
        int companyid = (int)session.getAttribute("companyId");
		List<Otherpa>opList=workdaysService.oplist(users, companyid);
		model.addAttribute("opList", opList);
		model.addAttribute("yearMonth",yearMonth);
		model.addAttribute("Brtime",Brtime);
		
        return "list";
    }

    //会員登録ページに移行
	@GetMapping("/superuser")
	public String menue(@ModelAttribute User user, Model model){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        String id = (String)session.getAttribute("email");

        if (superUser == null) {
            return "accessError";
        }

        //管理者がindiviudualDataにも登録していた場合勤怠データにアクセスできるように
		List<IndividualData> iDataList = individualService.findMail(id);
		if(CollectionUtils.isEmpty(iDataList)){
			model.addAttribute("iDuser",0);
			return "superuser";
		}
    
        model.addAttribute("iDuser",1);
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
	@PostMapping("/insertCompanyInfo")
	public String confirm(@Validated @ModelAttribute IndividualData idData,BindingResult result,Model model){
		
        if (result.hasErrors()){
			// エラーがある場合、index.htmlに戻る
            return "register";
        }
        
        //メアドに重複があった場合重複画面に飛ぶ
        IndividualData iData = idRepository.findByMailAndCompanyID(
            idData.getMail(), (Integer)session.getAttribute("companyId"));
		if(iData!=null){
			return "usedemail";
		}
        model.addAttribute("idData", idData);
		return "confirm";
	}

	//会員情報をDBに登録
	@PostMapping("/regist")
    public String regist(@Validated @ModelAttribute IndividualData idData, BindingResult result, Model model){
		model.addAttribute("user", idRepository.findAll());
        if (result.hasErrors()){

			return "confirm";
		}

        int companyID = (int)session.getAttribute("companyId");
        String mail=idData.getMail();
        int individualId=1;
        String name=idData.getName();
        String company1=idData.getCompany1();
        String company2=idData.getCompany2();
        String company3=idData.getCompany3();

        
        int i=individualService.insert(companyID, mail, individualId,name,0,1,company1,company2,company3);
        
        //登録されたメアドにユニバーサルユーザー登録orログインのメールを送る
        User universalUser=userService.findEmail(mail);
        if(universalUser==null){
           //mailSendService.mailsend(mail,WorkdaysProperties.userRegisterTitle,WorkdaysProperties.userRegisterText);
           return "superuser";
        }
        //mailSendService.mailsend(mail,WorkdaysProperties.loginTitle,WorkdaysProperties.loginText);
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

        //ユニバーサルユーザーテーブルに登録済みの場合は済と表示する
        for (IndividualData id : iList) {
            if (id.getRegistered() == 0) {
                id.setRegist("済");
            } else {
                id.setRegist("未");
            }
        }
        System.out.println(iList);
		model.addAttribute("userListParam", iList);

		return "userlist";
	}

    //メール全員に送る
    @GetMapping("/mailallsend")
    public String mailallsend(@Validated  Model model){
        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
        //セッションの管理者から企業idを特定して企業内で登録してないやつをリスト化
       List<IndividualData>IdataList=individualService.findCompanyIdRegistered(superUser.getCompanyID(),1);
        //して1人1人にメールを送る
        for(IndividualData idata:IdataList){
            mailSendService.mailsend(idata.getMail(),WorkdaysProperties.onetimeTitle, WorkdaysProperties.userRegisterText);

        }
        List<IndividualData> iList = userService.findCompanyID(superUser.getCompanyID());

         //ユニバーサルユーザーテーブルに登録済みの場合は済と表示する
         for (IndividualData id : iList) {
            if (id.getRegistered() == 0) {
                id.setRegist("済");
            } else {
                id.setRegist("未");
            }
        }
		model.addAttribute("userListParam", iList);

        model.addAttribute("regist","メール送信が完了しました");
        return "userlist";
    }

    //個別ユーザー削除
    //ここで使用されるユーザーIDはメアドのこと
	@RequestMapping(value="/userdelete")
	public String deleteuser(@RequestParam("id") String userid, Model model){
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
        System.out.println(iData.getCompanyID());
        System.out.println(iData.getMail());
		switch (name) {
			case "number":
			  iData.setNumber(inputvalue);
			  break;
			case "name":
              iData.setName(inputvalue);
			  break;
			  case "mail":
              int i=idRepository.UpdateIndividualEmail(inputvalue,iData.getCompanyID(),iData.getMail());
			  //iData.setMail(inputvalue);
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
            case "banned":
			  iData.setBanned(Integer.parseInt(inputvalue));
			  break;
			} 
		userService.updateIndividualData(iData);
		
		List<IndividualData> iList = userService.findCompanyID(companyId);
		model.addAttribute("userListParam", iList);
		return "userlist";
	}

    @RequestMapping(value="/remail")
	public String superuserremail(@RequestParam("mail") String mail, Model model){
		
		mailSendService.mailsend(mail, WorkdaysProperties.onetimeTitle, WorkdaysProperties.userRegisterText);
		return "remail";
	}

	//ファイルアップロード画面表示
	@GetMapping("/templateupload")
	public String getTemplateUpload(Model model){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }

        int companyId = (Integer)session.getAttribute("companyId");

            //会社IDから契約情報のListを作る
            List<ContractData> contractDataList= contractService.findCompanyID(companyId);
            ContractData newContractData=new ContractData();
            Date today=new Date();
            //本日の日付が契約開始日より先且つ契約終了日より後の契約を特定する
            for(ContractData contractData: contractDataList){
                if(contractData.getStartContract().before(today)&&contractData.getEndContract().after(today)){
                    newContractData=contractData;
                }
                
            }
            long limitmegasize=newContractData.getLimitedCapacity()*1000*1000*1000;//DBに登録されているのはギガなので1000の3乗して規格を合わせる
            
            //ファイルサイズの合計が一定値を上回ったらエラー返す
            String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
        
            File fileFolder = new File(fileFolderPath);
            File[] fileList = fileFolder.listFiles();
            long foldermegasize=0;
            if (fileList != null) {
                for (File file : fileList){
                    foldermegasize=foldermegasize+file.length();
                }
            }else {
                
            }
            System.out.println("all="+foldermegasize);
            if(foldermegasize>limitmegasize){
                model.addAttribute("error", "フォルダの容量が制限を超えています。");
                return "templatelist";
            }


		return "templateupload";
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

            //会社IDから契約情報のListを作る
            List<ContractData> contractDataList= contractService.findCompanyID(companyId);
            ContractData newContractData=new ContractData();
            Date today=new Date();
            //本日の日付が契約開始日より先且つ契約終了日より後の契約を特定する
            for(ContractData contractData: contractDataList){
                if(contractData.getStartContract().before(today)&&contractData.getEndContract().after(today)){
                    newContractData=contractData;
                }
                
            }
            long limitmegasize=newContractData.getLimitedCapacity()*1000*1000*1000;//DBに登録されているのはギガなので1000の3乗して規格を合わせる
            
            //ファイルサイズの合計が一定値を上回ったらエラー返す
            long newfilemeagsize=uploadFile.length();
            System.out.println("new="+newfilemeagsize);
            String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
        
            File fileFolder = new File(fileFolderPath);
            File[] fileList = fileFolder.listFiles();
            long foldermegasize=0;
            if (fileList != null) {
                for (File file : fileList){
                    foldermegasize=foldermegasize+file.length();
                }
            }
            System.out.println("all="+foldermegasize);
            if(foldermegasize+newfilemeagsize>limitmegasize){
                model.addAttribute("error", "フォルダの容量が制限を超えています");
                return "templatelist";
            }
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

    //ファイル一覧操作方法選択画面へ遷移
    @GetMapping("/templatelistTransition")
	public String templateTransition(){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        
        if (superUser == null) {
            return "accessError";
        }
		return "totemplatelist";
	}


    //ファイル一覧を表示
    @RequestMapping("/templatelist")
    public String showTemplateFileList(Model model) {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }

        List<BeanFile> BeanFileList = new ArrayList<BeanFile>();
        int companyId = (Integer)session.getAttribute("companyId");
        String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
        
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
        
        int fileCount = 0;
        if (fileList != null) {
            for (File file : fileList){
                BeanFile beanFile=new BeanFile();
                beanFile.setFileName(file.getName());
                beanFile.setFilePath("https://workdays.jp/download/"+companyId + "_input/"+file.getName());
                BeanFileList.add(beanFile); 
                fileCount++;
            }  
        } 
        if(fileCount == 0) {
            model.addAttribute("error", "ファイルが存在しません");
            return "templatelist";
        }
        model.addAttribute("folder", companyId + "_input");
        model.addAttribute("fileName", BeanFileList);
        return "templatelist";
    }

    //ファイル一覧からファイル削除
    @RequestMapping("/filedelete")
    public String showTemplateFileList(@RequestParam("deleteFileName") String deleteFileName, @RequestParam("box") String box, Model model) {
        
        int id = (Integer)session.getAttribute("companyId");
        File folder = getInputFolder(id);
        String deleteFilePath = folder.toPath() + "/" + deleteFileName;
        File deleteFile = new File(deleteFilePath);
        if(!deleteFile.delete()){
            model.addAttribute("error", "ファイルが削除できませんでした");
        }
        
        String fileFolderPath = getInputFolder(id).getAbsolutePath();
        
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
        List<String> filePathList = new ArrayList<String>();
        List<String> fileNameList = new ArrayList<String>();

        if (fileList != null) {
            for (File file : fileList){
                fileNameList.add(file.getName());
                String filePath = getInputFolder(id).getAbsolutePath() + "/" + file.getName();
                filePathList.add(filePath);
            }
        } else {
            model.addAttribute("error", "ファイルが存在しません");
            return "templatelist";
        }
        model.addAttribute("filePath", filePathList);
        model.addAttribute("fileName", fileNameList);
        
        model.addAttribute("error", deleteFileName+"が削除されました");

        if (box.equals("box")) {
            return "templatedownloadBox"; 
        }
        return "templatelist";
    }

    @GetMapping("/createApi")
    public String createApi(HttpServletRequest request, Model model) {
        String clientId = WorkdaysProperties.boxClientId;
		String clientSecret = WorkdaysProperties.boxClientSecret;

        BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");

        if (api == null) {
            String code = request.getParameter("code");
		    System.out.println("CODE=" + code);
		
		    api = new BoxAPIConnection(
  			clientId,
  			clientSecret,
			code
		);

        session.setAttribute("api", api);
        }

        List<String> filePathList = new ArrayList<String>();
        List<String> fileNameList = new ArrayList<String>();
        int companyId = (Integer)session.getAttribute("companyId");
        String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
        
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
        
        if (fileList != null) {
            for (File file : fileList){
                fileNameList.add(file.getName());
                String filePath = getInputFolder(companyId).getAbsolutePath() + "/" + file.getName();
                filePathList.add(filePath);
            }
        } else {
            model.addAttribute("error", "ファイルが存在しません");
            return "templatedownloadBox";
        }
        model.addAttribute("fileName", fileNameList);
        return "templatedownloadBox";
    }

    //テンプレファイル一覧からboxへファイルダウンロード
    @RequestMapping("/templatefiledownload")
    public String downloadTemplateFilefromList(@RequestParam("fileName") String fileName, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException{
		
        int id = (Integer)session.getAttribute("companyId");
        String clientFilePath = getInputFolder(id).getAbsolutePath() + "//" + fileName;

		BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");

        if (api == null) {
            model.addAttribute("error", "先に右上のボタンを押してboxにログインしてください");
        return "templatelist";
        }

		//すべてのフォルダ(id=0)下に出力用ファイルを作成
		BoxFolder parentFolder = new BoxFolder(api, "0");
		Iterable<Info> childrens = parentFolder.getChildren();
		String childFolderId = null;

		//フォルダ名の重複を確認
		for (Info info : childrens) {
			if (info.getName().equals("WorkDays_template")) {
				childFolderId = info.getID();
				break;
			}
		}
		if (childFolderId == null) {
			BoxFolder.Info childFolderInfo = parentFolder.createFolder("WorkDays_template");
			childFolderId = childFolderInfo.getID();
		}

		BoxFolder uploadFolder = new BoxFolder(api, childFolderId);
		BoxFolder.Info info = uploadFolder.getInfo();
		System.out.println("出力フォルダ名：" + info.getName() + ", 出力フォルダID:" + info.getID());

		//ファイル名の重複を確認
		String fileId = null;
		for (BoxItem.Info itemInfo : uploadFolder) {
			if(itemInfo.getName().equals(fileName)) {
				//ファイルを更新
				fileId = itemInfo.getID();
				BoxFile updatefile = new BoxFile(api, fileId);
				FileInputStream stream = new FileInputStream(clientFilePath);
				updatefile.uploadNewVersion(stream);
			}
		}

		if(fileId == null) {
			FileInputStream input = new FileInputStream(clientFilePath);
			BoxFile.Info newFileInfo = uploadFolder.uploadFile(input, fileName);
		}
  
        List<String> filePathList = new ArrayList<String>();
        List<String> fileNameList = new ArrayList<String>();
        int companyId = (Integer)session.getAttribute("companyId");
        String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
        
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
        
        if (fileList != null) {
            for (File file : fileList){
                fileNameList.add(file.getName());
                String filePath = getInputFolder(companyId).getAbsolutePath() + "/" + file.getName();
                filePathList.add(filePath);
            }
        } else {
            model.addAttribute("error", "ファイルが存在しません");
            return "templatelist";
        }
        model.addAttribute("fileName", fileNameList);
        model.addAttribute("error", "WorkDays_templateフォルダにダウンロードが完了しました");
        return "templatedownloadBox";
    }

    //パスワード変更画面表示
    @GetMapping("/changePass")
	public String changePass(@ModelAttribute User user){

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        
        if (superUser == null) {
            return "accessError";
        }
		return "changePass";
	}

    //パスワード変更処理
    @RequestMapping("/updatePass")
    public String updatePassword(@RequestParam("old") String oldPass, @RequestParam("new") String newPass, Model model) {

        SuperUserLogin superUserLogin = (SuperUserLogin)session.getAttribute("superUser");
        String sessionPass = superUserLogin.getPass();
        String sessionMail = superUserLogin.getEmail();
        int companyID = (Integer)session.getAttribute("companyId");

        if (!sessionPass.equals(oldPass)) {
            model.addAttribute("error", "現在のパスワードが間違っています");
            return "changePass";
        }

        SuperUser superUser = superUserService.findEmailCompanyID(sessionMail, companyID);
        superUser.setPass(newPass);
        superUserService.update(superUser);

        model.addAttribute("error", "パスワードが設定されました");

        return "changePass";
    }

        //ユーザー一括アップロード画面表示
    @GetMapping("/transition")
    public String transition(@RequestParam("local") String localPath, @RequestParam("box") String boxPath, Model model) {

        BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");

        if (api == null) {
            String url = "https://account.box.com/api/oauth2/authorize?client_id=" 
            + WorkdaysProperties.boxClientId + "&response_type=code&redirect_uri=";

            model.addAttribute("boxPath", url + WorkdaysProperties.host + boxPath);
        } else {
            model.addAttribute("boxPath", boxPath);
        }

        model.addAttribute("localPath", localPath);

		return "transition";
	}

    //ユーザー一括アップロード画面表示
    @GetMapping("/touserupload")
    public String toUserUpload() {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
		return "touserupload";
	}
    //ローカルからユーザー一括ファイルアップロードする画面表示
    @GetMapping("/alluserupload")
    public String allUserUpload() {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
		return "alluserupload";
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
        List<String> errorMailList = new ArrayList<String>();
        List<IndividualData> newIdList = new ArrayList<IndividualData>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {  
            //エンコード指定して読み込み      
            FileInputStream input = new FileInputStream(uploadFile);
            InputStreamReader stream = new InputStreamReader(input,"UTF-8");
            br = new BufferedReader(stream);    
            // br = new BufferedReader(new FileReader(uploadFile));
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
                    if (map.containsKey("管理番号")){
                        int number = map.get("管理番号");
                        idData.setNumber(data[number]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：管理番号"); 
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

                        //正しいアドレスか確認
                        String checkMail = mailSendService.checkMailAddress(data[mail]);
                        if(checkMail != null) {
                            errorMailList.add("使用出来ないメールアドレス：" + checkMail);   
                        } else {
                            idData.setMail(data[mail]);
                        }
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：メールアドレス"); 
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

            //ユーザー情報に使用できないメールアドレスを含んでいた場合はエラーを返す
            if (errorMailList.size() != 0 || !errorMailList.isEmpty()) {
                errorMailList.add("使用できないメールアドレスが含まれているためファイルのアップロードに失敗しました");
                model.addAttribute("mail", errorMailList); 
                return "alluserupload";
            }

            for (IndividualData id : newIdList) {

                String mail = id.getMail();

            //ユーザーが既に登録されているか確認
                User universalUser=userService.findEmail(mail);
                if(universalUser==null){
                 //mailSendService.mailsend(mail,WorkdaysProperties.userRegisterTitle, WorkdaysProperties.userRegisterText);
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

    //boxからユーザー一括登録ファイルを読み込み
    @GetMapping("/uploaduserfrombox")
    public String userUploadfrombox(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        
        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }

        String clientId = WorkdaysProperties.boxClientId;
		String clientSecret = WorkdaysProperties.boxClientSecret;
        List<String> csvFileList = new ArrayList<String>();

        BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");

        if (api == null) {
		    String code = request.getParameter("code");
		    System.out.println("CODE=" + code);
		
		    api = new BoxAPIConnection(
  			    clientId,
  			    clientSecret,
			    code
		    );
            session.setAttribute("api", api);
        }

        //すべてのフォルダ(id=0)下の情報を取得
		BoxFolder parentFolder = new BoxFolder(api, "0");
		Iterable<Info> childrens = parentFolder.getChildren();

        String folderId = null;
		//フォルダ名"WorkDays_template"を検索
		for (Info info : childrens) {
			if(info.getName().equals("WorkDays_userUpload")){
                folderId = info.getID();
                break;
            }
		}

        //フォルダが存在しない場合はフォルダを作成して終了
        if(folderId == null) {
            BoxFolder.Info childFolderInfo = parentFolder.createFolder("WorkDays_userUpload");
            model.addAttribute("error", "boxのWorkDays_userUploadフォルダにファイルが存在しません");
            return "alluserupload";
        }
        
        session.setAttribute("boxcsvFolderId", folderId);
        //フォルダが存在する場合はファイル一覧を表示
        BoxFolder targetFolder = new BoxFolder(api, folderId);
        Iterable<Info> targetChildrens = targetFolder.getChildren();

        for (Info info : targetChildrens) {
            if(info.getName().contains(".csv")){
                csvFileList.add(info.getName());
            }
        }

        if (csvFileList == null || csvFileList.size() == 0) {
            model.addAttribute("error", "boxのWorkDays_userUploadフォルダにcsvファイルが存在しません");
            return "boxcsvfilelist";
        }

        session.setAttribute("csvFileList", csvFileList);
        model.addAttribute("fileName", csvFileList);
        return "boxcsvfilelist";
    }

    //boxのcsvからファイル一括アップロード
    @GetMapping("/boxuserresister")
    public String boxUserResister(@RequestParam("uploadfile") String fileName, Model model) throws FileNotFoundException, IOException {
        //セッションからapiとフォルダidと会社idを取り出す
        BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");
        String folderId = (String)session.getAttribute("boxcsvFolderId");
        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        int companyid = superUser.getCompanyID();

          //全ユーザー削除
          List<IndividualData> idList = userService.findCompanyID(companyid);
          for (IndividualData id : idList) {
              userService.deleteIndividualData(id);
          }

        BoxFolder targetFolder = new BoxFolder(api, folderId);
        Iterable<Info> targetChildrens = targetFolder.getChildren();

        String fileId = null;
        for (Info info : targetChildrens) {
            if(info.getName().equals(fileName)) {
                fileId = info.getID();
            }
        }
        //boxからファイルダウンロード処理
        BoxFile file = new BoxFile(api, fileId);
        BoxFile.Info info = file.getInfo();

        String csvFile = WorkdaysProperties.basePath + "//" + info.getName();
        File uploadFile = new File(csvFile);
        uploadFile.createNewFile();
        FileOutputStream stream = new FileOutputStream(uploadFile);
        file.download(stream);
        stream.close();

        //ユーザーアップロード処理
        // アップロードファイルから値を読み込んで保存する
        BufferedReader br = null;
        
        //ファイルが存在しないとき
        if(!uploadFile.exists()) {
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "alluserupload";  
        }

        int i = 0;
        List<String> errorMailList = new ArrayList<String>();
        List<IndividualData> newIdList = new ArrayList<IndividualData>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {
            //エンコード指定して読み込み      
            FileInputStream input = new FileInputStream(uploadFile);
            InputStreamReader st = new InputStreamReader(input,"UTF-8");
            br = new BufferedReader(st);              
            // br = new BufferedReader(new FileReader(uploadFile));
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
                    if (map.containsKey("管理番号")){
                        int number = map.get("管理番号");
                        idData.setNumber(data[number]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：管理番号"); 
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

                        //正しいアドレスか確認
                        String checkMail = mailSendService.checkMailAddress(data[mail]);
                        if(checkMail != null) {
                            errorMailList.add("使用出来ないメールアドレス：" + checkMail);   
                        } else {
                            idData.setMail(data[mail]);
                        }
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：メールアドレス"); 
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
                    idData.setCompanyID(companyid);
                    newIdList.add(idData);
                }
                i++;
            }

            //ユーザー情報に使用できないメールアドレスを含んでいた場合はエラーを返す
            if (errorMailList.size() != 0 || !errorMailList.isEmpty()) {
                ArrayList csvFileList = (ArrayList)session.getAttribute("csvFileList");
                model.addAttribute("fileName", csvFileList);
                errorMailList.add("使用できないメールアドレスが含まれているためファイルのアップロードに失敗しました");
                model.addAttribute("mail", errorMailList); 
                return "boxcsvfilelist";
            }

            for (IndividualData id : newIdList) {

                String mail = id.getMail();

            //ユーザーが既に登録されているか確認
                User universalUser=userService.findEmail(mail);
                if(universalUser==null){
                 //mailSendService.mailsend(mail,WorkdaysProperties.userRegisterTitle, WorkdaysProperties.userRegisterText);
                    id.setRegistered(1);
                } else {
                    id.setRegistered(0);
                }         
            }
                   
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ArrayList csvFileList = (ArrayList)session.getAttribute("csvFileList");
            model.addAttribute("fileName", csvFileList);
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "boxcsvfilelist";
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

        ArrayList csvFileList = (ArrayList)session.getAttribute("csvFileList");
        model.addAttribute("fileName", csvFileList);
        model.addAttribute("error", "ファイルがアップロードされました");
        return "boxcsvfilelist";
    }

    //テンプレファイルアップロード画面遷移
    @GetMapping("/totemplateupload")
    public String toTemplateUpload() {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
		return "totemplateupload";
	}

    //boxからテンプレファイル読み込み
    @GetMapping("/boxtemplateupload")
    public String boxTemplateUpload(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        if (superUser == null) {
            return "accessError";
        }
		
        String clientId = WorkdaysProperties.boxClientId;
		String clientSecret = WorkdaysProperties.boxClientSecret;
        List<String> templatelist = new ArrayList<String>();
		 
        BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");

        if (api == null) {
		    String code = request.getParameter("code");
		    System.out.println("CODE=" + code);
		
		    api = new BoxAPIConnection(
  			    clientId,
  			    clientSecret,
			    code
		    );
            session.setAttribute("api", api);
        }

         //会社IDから契約情報のListを作る
         int companyId = (Integer)session.getAttribute("companyId");
         List<ContractData> contractDataList= contractService.findCompanyID(companyId);
         ContractData newContractData=new ContractData();
         Date today=new Date();
         //本日の日付が契約開始日より先且つ契約終了日より後の契約を特定する
         for(ContractData contractData: contractDataList){
             if(contractData.getStartContract().before(today)&&contractData.getEndContract().after(today)){
                 newContractData=contractData;
             }
             
         }
         long limitmegasize=newContractData.getLimitedCapacity()*1000*1000*1000;//DBに登録されているのはギガなので1000の3乗して規格を合わせる
         
         //ファイルサイズの合計が一定値を上回ったらエラー返す
         String fileFolderPath = getInputFolder(companyId).getAbsolutePath();
     
         File fileFolder = new File(fileFolderPath);
         File[] fileList = fileFolder.listFiles();
         long foldermegasize=0;
         if (fileList != null) {
             for (File file : fileList){
                 foldermegasize=foldermegasize+file.length();
             }
         }
         System.out.println("all="+foldermegasize);
         if(foldermegasize > limitmegasize){
             model.addAttribute("error", "フォルダの容量が制限を超えています");
             return "boxuploadlist";
         }

		//すべてのフォルダ(id=0)下の情報を取得
		BoxFolder parentFolder = new BoxFolder(api, "0");
		Iterable<Info> childrens = parentFolder.getChildren();

        String folderId = null;
		//フォルダ名"WorkDays_template"を検索
		for (Info info : childrens) {
			if(info.getName().equals("WorkDays_template")){
                folderId = info.getID();
                break;
            }
		}

        //フォルダが存在しない場合はフォルダを作成して終了
        if(folderId == null) {
            BoxFolder.Info childFolderInfo = parentFolder.createFolder("WorkDays_template");
            model.addAttribute("error", "boxのWorkDays_templateフォルダにファイルが存在しません");
            return "boxuploadlist";
        }
        
        session.setAttribute("boxFolderId", folderId);
        //フォルダが存在する場合はファイル一覧を表示
        BoxFolder targetFolder = new BoxFolder(api, folderId);
        Iterable<Info> targetChildrens = targetFolder.getChildren();

        for (Info info : targetChildrens) {
            templatelist.add(info.getName());
        }

        session.setAttribute("templatelist", templatelist);
        model.addAttribute("fileName", templatelist);
        return "boxuploadlist";
	}

    @GetMapping("/boxupload")
    public String boxUpload(@RequestParam("uploadfile") String fileName, Model model) throws FileNotFoundException, IOException {
    
        //セッションからapiとフォルダidと会社idを取り出す
        BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");
        String folderId = (String)session.getAttribute("boxFolderId");
        SuperUserLogin superUser = (SuperUserLogin)session.getAttribute("superUser");
        int companyid = superUser.getCompanyID();

        BoxFolder targetFolder = new BoxFolder(api, folderId);
        Iterable<Info> targetChildrens = targetFolder.getChildren();

        String fileId = null;
        for (Info info : targetChildrens) {
            if(info.getName().equals(fileName)) {
                fileId = info.getID();
            }
        }
        //boxからファイルダウンロード処理
        BoxFile file = new BoxFile(api, fileId);
        BoxFile.Info info = file.getInfo();

        String templateFile = getInputFolder(companyid) + "//" + info.getName();
        File templatefile = new File(templateFile);
        System.out.println("boxファイルアップロード先" + templatefile.getAbsolutePath());

        if(!templatefile.createNewFile()) {
            model.addAttribute("error", "ファイルのアップロードに失敗しました");
            List<String> templatelist = (List<String>)session.getAttribute("templatelist");
            model.addAttribute("fileName", templatelist);
            return "boxuploadlist";
        }

        FileOutputStream stream = new FileOutputStream(templatefile);
        file.download(stream);
        stream.close();

        model.addAttribute("error", fileName + "がアップロードされました");
        List<String> templatelist = (List<String>)session.getAttribute("templatelist");
        model.addAttribute("fileName", templatelist);
        return "boxuploadlist";
    }
  
}