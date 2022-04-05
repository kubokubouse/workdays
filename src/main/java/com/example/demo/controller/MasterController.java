package com.example.demo.controller;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.box.sdk.BoxItem.Info;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.*;
import com.example.demo.WorkdaysProperties;
import com.example.demo.model.ContractData;
import com.example.demo.model.Holiday;
import com.example.demo.model.Login;
import com.example.demo.model.User;
import com.example.demo.model.UserData;
import com.example.demo.model.UserListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.model.SuperUser;
import com.example.demo.model.MasterUser;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.ContractService;
import com.example.demo.service.HolidayService;
import com.example.demo.service.UserService;
import com.example.demo.service.SuperUserService;
import com.example.demo.model.SuperUserListParam;
import com.example.demo.repository.ContractDataRepository;
import com.example.demo.repository.SuperUserRepository2;



@Controller

public class MasterController {
    @Autowired
    UserService userService;
    @Autowired
    SuperUserRepository2 superUserRepository2;
    @Autowired
    SuperUserService superUserService;
	@Autowired
    CompanyInfoService companyInfoService;
	@Autowired
    ContractDataRepository contractRepository;
	@Autowired
	ContractService contractService;
	@Autowired
	HolidayService holidayService;

	
    @Autowired
    HttpSession session;

    @GetMapping("/master")
	public String master(@ModelAttribute Login login){

		return "masterlogin";
	}

    @RequestMapping("/masterlogin")
	public String masterlogin(@ModelAttribute Login login){
        String id = login.getEmail();
		String password = login.getPassword();
        MasterUser masterUser=userService.findIdPass(id, password);
        if(masterUser==null){
            return "masterloginfault";
        }
		session.setAttribute("masterUser", masterUser);
		return "masteruser";
	}
    //管理者登録ページに移行
	@GetMapping("/registersuperuser")
	public String registerAdmin(@ModelAttribute SuperUser superUser,BindingResult result,Model model){
        
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
        
		return "registersuperuser";
	}

    //データ送信時に確認画面に移行
    @PostMapping("/confirmsuperuser")
    public String confirmsuperuser(@Validated @ModelAttribute SuperUser superUser,BindingResult result,Model model){
		if (result.hasErrors()){
			// エラーがある場合、index.htmlに戻る
            return "registersuperuser";
        }
        SuperUser usedEmailuser=superUserService.findEmailCompanyID(superUser.getEmail(),superUser.getCompanyID());
		if(usedEmailuser!=null){
			return "usedemail";
		}
        
        model.addAttribute("superUser", superUser);
		return "confirmsuperuser";
	}

    //管理者情報をDBに登録
	@PostMapping("/registsuperuser")
    public String registsuperuser(@Validated @ModelAttribute SuperUser superUser, BindingResult result, Model model){
		
		System.out.println(superUser.getPass());
		superUserRepository2.save(superUser);
		// ルートパス("/") にリダイレクトします
		return "masteruser";
    }

	//契約情報登録ページに遷移
	@GetMapping("/contract")
	public String contract(@ModelAttribute ContractData contractData){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		return "contract";
	}
 
	//契約情報送信時に確認画面に遷移
	@PostMapping("/confirmcontract")
	public String confirmcontract(@Validated @ModelAttribute ContractData contractData,BindingResult result, Model model){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		if(contractData.getRegister()==null){
			model.addAttribute("error1", "日付が未入力です");
			
		}
		if(contractData.getStartContract()==null){
			model.addAttribute("error2", "日付が未入力です");
			
		}
		if(contractData.getEndContract()==null){
			model.addAttribute("error3", "日付が未入力です");
		}

		if (result.hasErrors()){
			// エラーがある場合、登録画面に戻る
			return "contract";
		}
 		
		model.addAttribute("contractData", contractData);
		return "confirmcontract";
	}
 
	//契約情報をDBに登録
	@PostMapping("/registercontract")
	public String registercontract(@Validated @ModelAttribute  ContractData contractData, BindingResult result, Model model){
		List<ContractData> contractDataList=contractService.findCompanyID(contractData.getCompanyID());
		if(CollectionUtils.isEmpty(contractDataList)){
			int contractID=1;
			contractData.setContractID(contractID);
		}

		else{
			int contractID=0;
			for(ContractData contractdata:contractDataList){
				if (contractID<contractdata.getContractID()){
					contractID=contractdata.getContractID();
				}
				
			}
			contractID=contractID+1;
			contractData.setContractID(contractID);

		}
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime());
        long timeInMilliSeconds = date.getTime();
        java.sql.Date date1 = new java.sql.Date(timeInMilliSeconds);
		String timeStamp=date1.toString();

		contractData.setTopupContract(timeStamp);
		contractData.setLimitedCapacity(1);

		System.out.println(contractData);
		
		contractRepository.save(contractData);
		
		return "masteruser";
	}

    //ユーザー編集画面
	@GetMapping("/superuserlist1")
	public String superuserlist(@Validated  Model model, @ModelAttribute SuperUser superuser){

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
        
        SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "superuserlist";
	}

    @RequestMapping(value="/superuserdelete")
	public String superuserdelete(@RequestParam("id") String id, Model model){
		
		superUserService.delete(id);
		SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "superUserlist";
	}

    @PostMapping("/SuperUserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String superuserid, Model model){	
	
        SuperUser superUser=superUserService.findId(superuserid);

		switch (name) {
			case "pass":
			 superUser.setPass(inputvalue);
			break;
			
			case "email":
			 superUser.setEmail(inputvalue);
			break;
			
			case "companyID":
			  superUser.setCompanyID(Integer.parseInt(inputvalue));
			break;
			
		} 
		superUserService.update(superUser);
		SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "superuserlist";
	}

	//ユニバーサルユーザー一覧表示
	@GetMapping("/alluserlist")
	public String alluserlsit(Model model){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		List<UserData> userDataList=userListParam.getUserDataList();
		model.addAttribute("userDataList",userDataList);
		
		return "universaluserlist";
	}
	//Ajaxで値を取得しDBに登録
	@PostMapping("/UniversalUserAjaxServlet")
	public String UniversalUserAjaxServlet(@RequestParam String inputvalue, String userid, String name, Model model){	
		User user = userService.findId(Integer.parseInt(userid));
        System.out.println(name);
        System.out.println(inputvalue);

		switch (name) {
			case "banned":
			  user.setBanned(Integer.parseInt(inputvalue));
			break;
			
		} 
		userService.update(user);
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		List<UserData> userDataList=userListParam.getUserDataList();
		model.addAttribute("userDataList",userDataList);
		return "universaluserlist";
	}


	//祝日編集画面に遷移
	@GetMapping("/holiday")
	public String holidaylist(@Validated  Model model, @ModelAttribute Holiday holiday){

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		YearMonth yearMonth=userService.nowYearMonth();
		session.setAttribute("yearMonth",yearMonth);
		model.addAttribute("yearMonth", yearMonth);

		//タイムスタンプの二年以上前のデータは全部削除
		List<Holiday>allHolidayList=holidayService.serchAllHoliday();
		for(Holiday holidays:allHolidayList){
			if(yearMonth.getYear()-holidays.getYear()>1){
				holidayService.delete(holidays.getId());
			}
		}
		//2年前のデータを削除する
		int twoyearago=yearMonth.getYear()-2;
		List<Holiday>tyholidayList=holidayService.getholidayList(twoyearago);
		for(Holiday holidays:tyholidayList){
			holidayService.delete((holidays.getId()));
		}
		//その年の昇順ソートされたリストを入手
        List<Holiday>holidayList=holidayService.getholidayList(yearMonth.getYear());
        model.addAttribute("holidayList", holidayList);

		return "holidaylist";
	}

	//祝日削除
	@RequestMapping(value="/holidaydelete")
	public String holidaydelete(@RequestParam("id") String id, Model model){
		holidayService.delete(Integer.parseInt(id));
		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		//その年の昇順ソートされたリストを入手
        List<Holiday>holidayList=holidayService.getholidayList(yearMonth.getYear());
        model.addAttribute("holidayList", holidayList);
		model.addAttribute("yearMonth", yearMonth);
		return "holidaylist";
	}

	//祝日の変更ページに飛ぶ
	@GetMapping("/holidaychange")
	public String holidaychange(@RequestParam("id") String id, Model model,@ModelAttribute Holiday holiday,BindingResult result){
		Holiday oneholiday=holidayService.findId(Integer.parseInt(id));
		model.addAttribute("oneholiday", oneholiday);
		return "holidaychange";
	}

	//祝日の変更
	@PostMapping("/holidayupdate")
	public String holidayupdate(Model model,@ModelAttribute Holiday holiday, BindingResult result){
		holidayService.update(holiday);
		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		//その年の昇順ソートされたリストを入手
        List<Holiday>holidayList=holidayService.getholidayList(yearMonth.getYear());
        model.addAttribute("holidayList", holidayList);
		return "holidaylist";
	}

	//祝日の追加ページに飛ぶ
	@RequestMapping(value="/holidayplus")
	public String holidayplus(@RequestParam("year") String year, Model model,@ModelAttribute Holiday holiday,BindingResult result){
		Holiday oneholiday=new Holiday();
		oneholiday.setYear(Integer.parseInt(year));
		model.addAttribute("oneholiday", oneholiday);
		return "holidayregister";
	}

	//祝日の追加
	@PostMapping("/holidayregister")
	public String holidayregister(Model model,@ModelAttribute Holiday oneholiday, BindingResult result){
		//oneholiday.setYear();
		System.out.println(oneholiday);
		holidayService.insert(oneholiday);
		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		//その年の昇順ソートされたリストを入手
        List<Holiday>holidayList=holidayService.getholidayList(yearMonth.getYear());
        model.addAttribute("holidayList", holidayList);
		model.addAttribute("yearMonth", yearMonth);
		return "holidaylist";
	}

	//1年の祝日全削除
	@RequestMapping(value="/holidayalldelete")
	public String holidayalldelete(@RequestParam("year") String year, Model model,@ModelAttribute Holiday holiday){
		List<Holiday>holidayList=holidayService.findyear(Integer.parseInt(year));
		for(Holiday holidays:holidayList){
			holidayService.delete((holidays.getId()));
		}

		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		//その年の昇順ソートされたリストを入手
        List<Holiday>holidayList2=holidayService.getholidayList(yearMonth.getYear());
        model.addAttribute("holidayList", holidayList2);
		model.addAttribute("yearMonth", yearMonth);
		return "holidaylist";
	}

	//年度の選択
	@RequestMapping(value="/selectyear")
	public String selectyear(@RequestParam("nowyear") String nowyear, Model model){
		int year=userService.nowYearMonth().getYear()+Integer.parseInt(nowyear)-1;
		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		yearMonth.setYear(year);
		session.setAttribute("yearMonth", yearMonth);
		//その年の昇順ソートされたリストを入手
        List<Holiday>holidayList=holidayService.getholidayList(yearMonth.getYear());
        model.addAttribute("holidayList", holidayList);
		model.addAttribute("yearMonth", yearMonth);
		return "holidaylist";
	}

	//祝日アップロード画面へ遷移
	@GetMapping("/toholidayupload")
    public String holidayUpload() {

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		return "toholidayupload";
	}
	@GetMapping("/holidayupload")
    public String holidayupload() {

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		return "holidayupload";
	}
	//祝日一括アップロード処理
    @RequestMapping("/uploadholiday")
    public String uploadalluser(@RequestParam("file") MultipartFile multipartFile,
    Model model) throws IOException {

		//祝日を全削除する年度を指定
        YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");

        //全ユーザー削除
        List<Holiday>holidayList=holidayService.findyear(yearMonth.getYear());
		for(Holiday holidays:holidayList){
			holidayService.delete((holidays.getId()));
		}

        // ファイルが空の場合は異常終了
        if(multipartFile.isEmpty()){
        // 異常終了時の処理
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "holidayupload";   
        }
        
        //格納先のフルパス
        String filePath = WorkdaysProperties.basePath + "/uploadholiday.csv";

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
            return "holidayupload";  
        }

        int i = 0;
        List<String> errorMailList = new ArrayList<String>();
        List<Holiday> hdList = new ArrayList<Holiday>();
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
                    String year = header[0];
                    //マッピングする
                    map.put(year, 0);

                    String month = header[1];
                    map.put(month, 1);

                    String day = header[2];
                    map.put(day, 2);

                    String name = header[3];
                    map.put(name, 3);
                    
                } else {

                    Holiday holiday = new Holiday();

                // lineをカンマで分割し、配列dataに設定
                    data = line.split(",");

                    //mapから何番目にデータが格納されているか取得
                    if (map.containsKey("年")){
                        int year = map.get("年");
                        holiday.setYear(Integer.parseInt(data[year]));
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：年"); 
                        return "holidayupload";  
                    }
                    if (map.containsKey("月")) {
                        int month = map.get("月");
                        holiday.setMonth(Integer.parseInt(data[month]));
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：月"); 
                        return "holidayupload";  
                    }

                    if (map.containsKey("日")){
                        int day = map.get("日");
						holiday.setDay(Integer.parseInt(data[day]));
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：日"); 
                        return "holidayupload";  
                    }

                    if (map.containsKey("祝日名")){
                        int name = map.get("祝日名");
                        holiday.setName(data[name]);
                    } else {
                        model.addAttribute("error", "ヘッダーの名前を確認してください：祝日名"); 
                        return "holidayupload";  
                    }

                    //idData.setCompanyID(companyID);
                    hdList.add(holiday);
                }
                i++;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
            return "holidayupload";
        } finally {
            br.close();
            uploadFile.delete();
        }

        // int companyID,String mail,int individual_id,String name,int banned,int registered, String company1,String company2,String company3,String number
        for (Holiday holiday : hdList) {
            holidayService.insert(holiday);
        }
          
        model.addAttribute("error", "ファイルがアップロードされました");
        return "holidayupload";
    }

		//boxファイル経由で祝日一括アップロード処理
		@RequestMapping("/boxuploadholiday")
		public String getHolidayFile(HttpServletRequest request, HttpServletResponse response, Model model) {
			String clientId = WorkdaysProperties.boxClientId;
			String clientSecret = WorkdaysProperties.boxClientSecret;
			List<String> csvFileList = new ArrayList<String>();
	
			String code = request.getParameter("code");
			System.out.println("CODE=" + code);
			
			BoxAPIConnection api = new BoxAPIConnection(
				  clientId,
				  clientSecret,
				code
			);
			session.setAttribute("api", api);
	
			//すべてのフォルダ(id=0)下の情報を取得
			BoxFolder parentFolder = new BoxFolder(api, "0");
			Iterable<Info> childrens = parentFolder.getChildren();
	
			String folderId = null;
			//フォルダ名"WorkDays_template"を検索
			for (Info info : childrens) {
				if(info.getName().equals("WorkDays_holidayUpload")){
					folderId = info.getID();
					break;
				}
			}
	
			//フォルダが存在しない場合はフォルダを作成して終了
			if(folderId == null) {
				BoxFolder.Info childFolderInfo = parentFolder.createFolder("WorkDays_holidayUpload");
				model.addAttribute("error", "boxのWorkDays_holidayUploadフォルダにファイルが存在しません");
				return "boxholidayfiles";
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
				model.addAttribute("error", "boxのWorkDays_holidayUploadフォルダにcsvファイルが存在しません");
				return "boxholidayfiles";
			}
	
			session.setAttribute("csvFileList", csvFileList);
			model.addAttribute("fileName", csvFileList);
			return "boxholidayfiles";
		
		}

		//boxファイルから祝日登録csvダウンロード
		@RequestMapping("/boxholidayresister")
		public String uploadholiday(@RequestParam("file") String fileName, Model model) throws IOException {
	
			BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");
			String folderId = (String)session.getAttribute("boxcsvFolderId");
			ArrayList csvFileList = (ArrayList)session.getAttribute("csvFileList");

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
	
			String filePath = WorkdaysProperties.basePath + "//" + info.getName();
			File uploadFile = new File(filePath);
			uploadFile.createNewFile();
			FileOutputStream stream = new FileOutputStream(uploadFile);
			file.download(stream);
			stream.close();

			//祝日を全削除する年度を指定
			YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
	
			//全ユーザー削除
			List<Holiday>holidayList=holidayService.findyear(yearMonth.getYear());
			for(Holiday holidays:holidayList){
				holidayService.delete((holidays.getId()));
			}
			
			BufferedReader br = null;
	
			if(!uploadFile.exists()) {
			// 異常終了時の処理
				model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
				return "boxholidayfiles";  
			}
	
			int i = 0;
			List<String> errorMailList = new ArrayList<String>();
			List<Holiday> hdList = new ArrayList<Holiday>();
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
						String year = header[0];
						//マッピングする
						map.put(year, 0);
	
						String month = header[1];
						map.put(month, 1);
	
						String day = header[2];
						map.put(day, 2);
	
						String name = header[3];
						map.put(name, 3);
						
					} else {
	
						Holiday holiday = new Holiday();
	
					// lineをカンマで分割し、配列dataに設定
						data = line.split(",");
	
						//mapから何番目にデータが格納されているか取得
						if (map.containsKey("年")){
							int year = map.get("年");
							holiday.setYear(Integer.parseInt(data[year]));
						} else {
							model.addAttribute("fileName", csvFileList);
							model.addAttribute("error", "ヘッダーの名前を確認してください：年"); 
							return "boxholidayfiles";  
						}
						if (map.containsKey("月")) {
							int month = map.get("月");
							holiday.setMonth(Integer.parseInt(data[month]));
						} else {
							model.addAttribute("error", "ヘッダーの名前を確認してください：月"); 
							return "boxholidayfiles";  
						}
	
						if (map.containsKey("日")){
							int day = map.get("日");
							holiday.setDay(Integer.parseInt(data[day]));
						} else {
							model.addAttribute("error", "ヘッダーの名前を確認してください：日"); 
							return "boxholidayfiles";  
						}
	
						if (map.containsKey("祝日名")){
							int name = map.get("祝日名");
							holiday.setName(data[name]);
						} else {
							model.addAttribute("error", "ヘッダーの名前を確認してください：祝日名"); 
							return "boxholidayfiles";  
						}
	
						//idData.setCompanyID(companyID);
						hdList.add(holiday);
					}
					i++;
				}
	
			} catch (Exception e) {
				System.out.println(e.getMessage());
				model.addAttribute("fileName", csvFileList);
				model.addAttribute("error", "ファイルのアップロードに失敗しました"); 
				return "boxholidayfiles";
			} finally {
				br.close();
				uploadFile.delete();
			}
	
			// int companyID,String mail,int individual_id,String name,int banned,int registered, String company1,String company2,String company3,String number
			for (Holiday holiday : hdList) {
				holidayService.insert(holiday);
			}
			model.addAttribute("error", "ファイルがアップロードされました");
			return "boxholidayfiles";
		}

}

