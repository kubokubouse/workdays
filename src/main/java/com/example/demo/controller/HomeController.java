package com.example.demo.controller;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import javax.servlet.ServletException;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.box.sdk.*;
import com.box.sdk.BoxItem.Info;

import javax.servlet.http.*;

import com.example.demo.model.Otherpa;
import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.IdUser;
import com.example.demo.model.IndividualData;
import com.example.demo.model.Login;
import com.example.demo.model.Mail;
import com.example.demo.model.Onetime;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.SuperUser;
import com.example.demo.model.RePassword;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HolidayService;
import com.example.demo.service.IndividualService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.OnetimeService;
import com.example.demo.service.SuperUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.WorkdayMapping;
import com.example.demo.service.WorkdaysService;
import com.example.demo.service.CellvalueGet;
import com.example.demo.WorkdaysProperties;
import com.google.gson.Gson;
import com.example.demo.model.Judgeused;

@Controller
public class HomeController extends WorkdaysProperties{
	private final UserRepository repository;
    @Autowired //← コンストラクタが１つの場合、@Autowiredは省略できます
    HttpSession session;
    @Autowired
    UserService userService;
	@Autowired
    CompanyInfoService ciService;
    @Autowired
    MailSendService mailsendService;
    @Autowired
    WorkdaysService workdaysService;
	@Autowired
    HolidayService holidayService;
	@Autowired
    IndividualService individualService;
	@Autowired
    CompanyInfoService companyInfoService;
	@Autowired
	SuperUserService superUserService;
	@Autowired
    OnetimeService onetimeService;

    public HomeController(UserRepository repository){
        this.repository = repository;
    }

    //トップページがログイン画面になる
	@GetMapping("/")
	public String login(@ModelAttribute Login login){
		session.removeAttribute("api");
		session.removeAttribute("Data");
		Calendar before = Calendar.getInstance();
		before.set(Calendar.YEAR, 2022);
		before.set(Calendar.MONTH, 11);
		Calendar after = Calendar.getInstance();
		after.set(Calendar.YEAR, 2021);
		after.set(Calendar.MONTH, 1);

		int count = 0;

		while (after.before(before)) {
	
			after.add(Calendar.MONTH, 1);
	
			count++;
	
		}
		System.out.println(count);
		//return "login";
		return "login2";
	}

    //メール送信処理
	@GetMapping(value = "/sendmail")
	public String mailsend()throws MessagingException{
		User users=(User)session.getAttribute("Data");
		String lastname=users.getLastname();
		String mail=users.getEmail();
		mailsendService.send(lastname,mail);
		return "done";
	}

	//react用メール処理
	@RequestMapping(value = "/reactmail")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public String reactmail(String password, String email)throws MessagingException{
		User users=userService.findEmailPassword(email,password);
		String lastname=users.getLastname();
		String mail=users.getEmail();
		mailsendService.send(lastname,mail);
		return "done";
	}

	
	//ログイン時の処理 	reactで必要な分　返却値はjsonの文
	@RequestMapping("/login")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public String sucsess(@Validated  @ModelAttribute Login login,Model model,BindingResult result){
		User users=userService.findEmailPassword(login.getEmail(),login.getPassword());
		if (result.hasErrors()||users==null){
			// エラーがある場合、login.htmlに戻る
	        return "login";
		}
		
		session.setAttribute("Data",users);
		YearMonth yearMonth=userService.nowYearMonth();//その時の年月取得
		int userid=users.getId();
		int year=yearMonth.getYear();
		int month=yearMonth.getMonth();
		int lastmonth=month-1;
		Workdays workdays =workdaysService.findUseridYearMonthDay(userid,year, month,1);
		if (workdays==null){
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, lastmonth);//カレンダーメソッドなので使うのは実際の月-1の方
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
			for(int i=0;i<lastDayOfMonth;i++){
				String yobi[] = {"日","月","火","水","木","金","土"};
				cal.set(year, lastmonth, i+1);
				String weekday=yobi[cal.get(Calendar.DAY_OF_WEEK)-1];
				workdaysService.insertdata(userid, year, month, i+1, weekday);//int d=が左辺にあったから動かなくなったら足すこと
			}
		}
		
		//WorkingListParam workingListParam = userService.searchAll();→本来のやつ
		//スタートやエンドの値を一括で変形する→workingListParm全体の値を別の箱に入れる→箱をjsonで変換する
		StringListParam stringListParam = userService.reactsearchAll(users);
		Gson gson = new Gson();
		// String jstr = gson.toJson(workingListParam.getWorkingDataList(),  List.class);→本来のやつ
		String jstr = gson.toJson(stringListParam.getStringList2(),  List.class);
		WorkingListParam workingListParam = userService.searchAll(users);//リストに出す用の検索は必要
		model.addAttribute("workingListParam", workingListParam);
		return jstr;
	}
	//ログイン時の処理　spring用
	@RequestMapping("/login2")
	@CrossOrigin(origins = "*")
	public String sucsess2(@Validated  @ModelAttribute Login login, BindingResult result,
	 Model model, @ModelAttribute SuperUserLogin suserlogin, BindingResult sudoresult, @ModelAttribute YearMonth yaerMonth,BeanRegularTime Brtime){
		
		String id = login.getEmail();
		String pass = login.getPassword();
		session.setAttribute("email", id);
		
		//管理者の場合は管理者ページに移行
		SuperUserLogin superUser = userService.findEmailAndPass(id, pass);
		
		if (superUser != null && !sudoresult.hasErrors()) {
			session.setAttribute("superUser", superUser);
			int companyId=superUser.getCompanyID();
			session.setAttribute("companyId", companyId);
			//管理者がindiviudualDataにも登録していた場合勤怠データにアクセスできるように
			List<IndividualData>iDataList=individualService.findMail(id);
			User user=userService.findEmail(id);
			if(CollectionUtils.isEmpty(iDataList)){
				model.addAttribute("iDuser",0);
				return "superuser";
			}
			if(user==null){
				model.addAttribute("iDuser",0);
				return "superuser";
			}
			model.addAttribute("iDuser",1);
			return "superuser";
		}
		
		User users=userService.findEmailPassword(login.getEmail(),login.getPassword());
		if (result.hasErrors()||users==null){
			// 
			return "loginfalse";
		}
		//利用禁止ユーザーの場合loginに
		if(users.getBanned()==1){
			return "banned";
		}
		//ログイン時に入力されたメアドを使っている個別ユーザーを特定しリストを作る
		List <IndividualData>iDataList=individualService.findMail(login.getEmail());
		model.addAttribute("iDataList", iDataList);
		
		
		//個別ユーザーから会社IDを取得し会社名のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
		
		for(IndividualData iData:iDataList){
			IdUser idUser=individualService.getIdUser(iData);
			int companyid = idUser.getCompanyID();
			session.setAttribute("companyID", companyid);
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

		//備考1,2,3に会社名を添付する処理をする
		//会社のテンプレートファイルでoa,ob,coが使われているかジャッジ
		List<Otherpa>opList=workdaysService.oplist(users);
		model.addAttribute("opList", opList);
		model.addAttribute("yearMonth",yearMonth);
		model.addAttribute("Brtime",Brtime);
		return "list";
	}

	//年月が指定された場合のログイン
	@PostMapping("/yearmonth")
	public String yearMonth(@Validated @ModelAttribute YearMonth yearMonth,BeanRegularTime Brtime,  Model model){
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=1+cal.get(Calendar.MONTH);
		
		Calendar big = Calendar.getInstance();//現在の年月
		big.set(Calendar.YEAR, year);
		big.set(Calendar.MONTH, month);

		Calendar small = Calendar.getInstance();//入力された年月
		small.set(Calendar.YEAR, yearMonth.getYear());
		small.set(Calendar.MONTH, yearMonth.getMonth());
		
		int count = 0;
		//入力された年月が現在より未来だった場合bigとsmallを入れ替えて引き算が成立するようにする
		if(big.before(small)){
			Calendar middle=big;
			big=small;
			small=middle;
			while (small.before(big)) {
	
				small.add(Calendar.MONTH, 1);
		
				count++;
			}
			//2か月先以上の年月が入力されていた場合は戻る
			if(count>1){
				yearMonth.setYear(year);
				yearMonth.setMonth(month);
				model.addAttribute("error2","2か月以上先のデータは閲覧できません");
			}
		}
		
		while (small.before(big)) {
			small.add(Calendar.MONTH, 1);
			count++;
		}
		//1年以上前の年月が入力されていた場合戻る
		if(count>12){
			yearMonth.setYear(year);
			yearMonth.setMonth(month);
			model.addAttribute("error2","1年以上前のデータは閲覧できません");
		}

		//メアドから個別ユーザーのリストを作成する
		User user=(User)session.getAttribute("Data");
		List <IndividualData>iDataList=individualService.findMail(user.getEmail());
		model.addAttribute("iDataList", iDataList);
		
		//個別ユーザーから会社IDを取得し会社のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
		
		for(IndividualData iData:iDataList){
			IdUser idUser=individualService.getIdUser(iData);
			idUserList.add(idUser);
		}

		model.addAttribute("idUserList", idUserList);


		//指定された月のデータをリスト化
		WorkingListParam workingListParam=workdaysService.date(yearMonth.getYear(),yearMonth.getMonth());
		workingListParam.setMonth(yearMonth.getMonth());
		workingListParam.setYear(yearMonth.getYear());
		model.addAttribute("workingListParam", workingListParam);
		model.addAttribute("users", user);

		//備考仕様の有無を確認
		List<Otherpa>opList=workdaysService.oplist(user);
		model.addAttribute("opList", opList);
		session.setAttribute("yearMonth",yearMonth);
		model.addAttribute("Brtime",Brtime);
	
		return "list";
	}

	//定時ボタンを押されたときの処理
	@PostMapping("/ontime")
	public String Ontime(@Validated @ModelAttribute YearMonth yearMonth,BeanRegularTime Brtime, Model model){
		//セッション取得
		User user=(User)session.getAttribute("Data");
		yearMonth=(YearMonth)session.getAttribute("yearMonth");
		
		//メアドから個別ユーザーのリストを作成する
		List <IndividualData>iDataList=individualService.findMail(user.getEmail());
		model.addAttribute("iDataList", iDataList);
		
		//個別ユーザーから会社IDを取得し会社のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
		
		for(IndividualData iData:iDataList){
			IdUser idUser=individualService.getIdUser(iData);
			idUserList.add(idUser);
		}

		model.addAttribute("idUserList", idUserList);
		System.out.println(Brtime);
		//セッションに登録されている年月の平日に指定された定時データを埋め込む
		workdaysService.insertOntime(Brtime);
		
		//指定された月のデータをリスト化
		WorkingListParam workingListParam=workdaysService.date(yearMonth.getYear(),yearMonth.getMonth());
		model.addAttribute("workingListParam", workingListParam);
		model.addAttribute("users", user);

		//備考仕様の有無を確認
		List<Otherpa>opList=workdaysService.oplist(user);
		model.addAttribute("opList", opList);
		session.setAttribute("yearMonth",yearMonth);
		model.addAttribute("yearMonth",yearMonth);
		model.addAttribute("Brtime",Brtime);
	
		return "list";
	}

	@RequestMapping(value = "/listUpdate", method = RequestMethod.POST)
	  public String listUpdate(@Validated @ModelAttribute WorkingListParam workingListParam, BindingResult result, Model model) {
		 
	    if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				if (!errorList.contains(error.getDefaultMessage())) {
					errorList.add(error.getDefaultMessage());
				}
			}
			model.addAttribute("validationError", errorList);
	      return "list";
	    }
	    // ユーザー情報の更新
	    userService.updateAll(workingListParam);
		User users=(User)session.getAttribute("Data");
		model.addAttribute("users", users);
		

		//会社のテンプレートファイルでoa,ob,coが使われているかジャッジ
		int companyid = (int)session.getAttribute("companyID");
		CellvalueGet cellgetvalue=new CellvalueGet();
		Judgeused judgeused=cellgetvalue.GetCellvalue(companyid, users.getCompany2());
		model.addAttribute("judgeused", judgeused);
		return "list";
	}

	@PostMapping("/revisedone")
	public String revisedone(@Validated @ModelAttribute User user,BindingResult result){
			/*if (result.hasErrors()) {
	            // エラーがある場合、login.htmlに戻る
	            return "login";
	        }*/
		return "sucsess";
	}

	//ローカルに勤怠表ダウンロード時の会社選択画面
	@GetMapping("/choosetemplatelocal")
	public String chooseTemplateLocal(Model model){

		//会社選択
		String email = (String)session.getAttribute("email");
		List <IndividualData>iDataList=individualService.findMail(email);
		//個別ユーザーから会社IDを取得し会社名のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
				
		for(IndividualData iData:iDataList){
			IdUser idUser=individualService.getIdUser(iData);
					
			idUserList.add(idUser);
		}
		
		model.addAttribute("idUserList", idUserList);
		return "choosetemplatelocal";
	}

	//会社選択後のboxダウンロード処理
	@RequestMapping(value="/companyselect")
	public String CompanySelect (@RequestParam String company, Model model) {
			
		User users=(User)session.getAttribute("Data");
		model.addAttribute("user",users);
		//苗字と名前合わせてname
		String lastname=users.getLastname();
		String firstname=users.getFirstname();
		String name = lastname + "　" + firstname;

		//当日の年月取得
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		month=month+1; //カレンダーメソッドで取得した月は実際の月-1される（12月だったら11になる的な）ので+1して戻す
	
		List<Workdays> workdays =workdaysService.findYearMonth(users.getId(),year,month);

		Map<String, String> stHourMap = new HashMap<>();
		Map<String, String> stMinMap = new HashMap<>(); 
		Map<String, String> endHourMap = new HashMap<>(); 
		Map<String, String> endMinMap = new HashMap<>();
		Map<String, String> lunchTimeHourMap = new HashMap<>(); 
		Map<String, String> lunchTimeMinMap = new HashMap<>();
		Map<String, String> totalHourMap = new HashMap<>();
		Map<String, String> totalMinMap = new HashMap<>(); 
		Map<String, String> other1Map = new HashMap<>();
		Map<String, String> other2Map = new HashMap<>();
		Map<String, String> other3Map = new HashMap<>();

		int index = 1;
		for(Workdays workday:workdays){

			stHourMap.put("[sth"+index+"]", workday.getStart().toString().substring(0,2));
			stMinMap.put("[stm"+index+"]", workday.getStart().toString().substring(3,5));
			endHourMap.put("[edh"+index+"]", workday.getEnd().toString().substring(0,2));
			endMinMap.put("[edm"+index+"]", workday.getEnd().toString().substring(3,5));
			lunchTimeHourMap.put("[lth"+index+"]", workday.getHalftime().toString().substring(0,2));
			lunchTimeMinMap.put("[ltm"+index+"]", workday.getHalftime().toString().substring(3,5));
			totalHourMap.put("[tth"+index+"]", workday.getWorktime().toString().substring(0,2));
			totalMinMap.put("[ttm"+index+"]", workday.getWorktime().toString().substring(3,5));
			other1Map.put("[oa"+index+"]", workday.getOther1());
			other2Map.put("[ob"+index+"]", workday.getOther2());
			other3Map.put("[oc"+index+"]", workday.getOther3());

			index++;

		}
				
		//個別ユーザーTLから個人IDと会社IDを拾ってくる
		String[] values = company.split(":");
		int companyID = Integer.parseInt(values[0]);
		String companyName=values[1];
		
		String mail = users.getEmail();
		int individualID = Integer.valueOf(userService.findIndividualID(mail, companyID));
		
		//会社ID_input/会社名.xls
		String inputFilePath = getInputFolder(companyID).getAbsolutePath() + "/" + companyName + ".xls";//AWSでない場合は/を\\に修正
		//会社ID_output/個別ID_出力会社.xls
		String outputFilePath = getOutputFolder(companyID).getAbsolutePath() 
			+ "/" + individualID + "_" + companyName + ".xls";//AWSでない場合は/を\\に修正

		System.out.println("input: " + inputFilePath);
		System.out.println("output: " + outputFilePath);

		session.setAttribute("output", outputFilePath);
		
		WorkdayMapping workdayMapping = new WorkdayMapping();
		List<String> errors = workdayMapping.outputExcel(inputFilePath, outputFilePath, 
			stHourMap, stMinMap, endHourMap, endMinMap, lunchTimeHourMap, lunchTimeMinMap,
			totalHourMap, totalMinMap, other1Map,other2Map,other3Map, mail, individualID, name
		);

		if (errors.size() != 0 || !errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "outputerror";
		}

		File file = new File(outputFilePath);
		model.addAttribute("outputFileName", "ファイル名：" + file.getName());
		
		model.addAttribute("filePath", WorkdaysProperties.downloadPath + companyID +"_output/"+individualID + "_" + companyName + ".xls");

		return "done";
	}	
	
	//パスワード忘れた人用の処理 メール送信画面へ飛ぶ
	@GetMapping("/forgetpassword")
	public String forgetpassword (@ModelAttribute Mail mail){
		
		return "repass";
	}

	//パスワード忘れた人が送信してきたメアドに対してリンクを送る
	@PostMapping("/repassmail")
	public String repassmail (@Validated @ModelAttribute Mail mail, BindingResult result){
		User users=userService.findEmail(mail.getEmail());
		if(users==null){
			return "repass";
		}
		mailsendService.send(mail.getEmail());
		return "maildone";
	}
	//リンク踏んだ奴に対する処理
	@RequestMapping("/renewpass")
	public String renewpass (@ModelAttribute User users, RePassword rePassword, Model model, @RequestParam String email){
		User user=userService.findEmail(email);
		session.setAttribute("passworduser" ,user);
		if (user == null) {
			SuperUser superuser = superUserService.findbyEmail(email);
			session.setAttribute("superUser", superuser);
		}
		int flag=0;
		model.addAttribute("flag", flag);
		return "newpassword";
	}
	
	//新規パスワードが送られてきた場合の処理
	@PostMapping("/inputpassword")
	public String inputpassword (@ModelAttribute User user, RePassword rePassword, Model model){
		User passworduser=(User)session.getAttribute("passworduser");
		String pass = rePassword.getPassword();
		String confirm=rePassword.getConfirmpassword();
		
		if(pass.equals(confirm)){
			if(passworduser == null) {
				SuperUser sUser = (SuperUser)session.getAttribute("superUser");
				sUser.setPass(pass);
				superUserService.update(sUser);
			} else {
				passworduser.setPassword(pass);
				userService.update(passworduser);	
			}
			return "renewdone";
		}
		int flag=1;
		model.addAttribute("flag", flag);
		return "newpassword";
	}

	//ユニバユーザー登録用リンク踏んだ奴用の処理　会員登録ページに移行
	@GetMapping("/universalregister")
	public String universalregister(@ModelAttribute User user, Model model, @RequestParam String email){
		
		user.setEmail(email);
		session.setAttribute("user",user);
		model.addAttribute("user",user);
		return "universalregister";
	}

    //会員情報入力→確認画面へ
	@PostMapping("/universalregister")
	public String universalconfirm(@Validated @ModelAttribute User user,BindingResult result,
		@RequestParam String password, @RequestParam String confirm, Model model){
		
        if (result.hasErrors()){
			// エラーがある場合登録画面に戻る
            return "universalregister";
        }
		if (!password.equals(confirm)) {
			model.addAttribute("error", "パスワードが一致しません");
			user = (User)session.getAttribute("user");
			model.addAttribute("user",user);
			return "universalregister";
		}
		return "universalconfirm";
	}

//ユニバーサルユーザー情報をワンタイムテーブルに登録→メールを送信
@PostMapping("/universalregist")
public String univeresalregist(@Validated @ModelAttribute User user, BindingResult result, Model model,Login login){
	
	model.addAttribute("user", repository.findAll());
	if (result.hasErrors()){
		return "universalconfirm";
	}
	
	List <Onetime> onetimeList=onetimeService.searchAll();
	if (CollectionUtils.isEmpty(onetimeList)) {
		
	}
	else{
		for(Onetime oneTime:onetimeList){
			onetimeService.delete(oneTime);
		}
	}
	//入力されたuserの情報をonetimeに入れる
	Onetime onetime=userService.inOnetime(user);
	//ワンタイムテーブルに情報を登録しメールを送る
	onetimeService.insert(onetime);
	mailsendService.mailsend(user.getEmail(), WorkdaysProperties.onetimeTitle, onetimeText);

	//エラーがなければ仮登録完了ページに遷移
	return "onetime";
	}

	//登録完了メール踏んだ時の処理
	@GetMapping("/registerdone")
	public String registerdone(@Validated @ModelAttribute User user, BindingResult result, Model model,Login login) {
		Onetime onetime=onetimeService.findId(1);
		long ontimelong=onetime.getDatetime().getTime();
		
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
		onetime.setDatetime(date);
		long nowlong=onetime.getDatetime().getTime();
		
		if((nowlong-ontimelong)/1000<WorkdaysProperties.limitsecond){
			User users=new User();
			users.setEmail(onetime.getEmail());
			users.setFirstname(onetime.getFirstname());
			users.setLastname(onetime.getLastname());
			users.setPassword(onetime.getPassword());
			users.setBanned(0);

			System.out.println(onetime.getDatetime());

			List<IndividualData> individualData=individualService.findMail(onetime.getEmail());
			for(IndividualData iData:individualData){
				users.setCompany1(iData.getCompany1());
				users.setCompany2(iData.getCompany2());
				users.setCompany3(iData.getCompany3());
			}
			repository.save(users);

		}
		else{
			List <Onetime> onetimeList=onetimeService.searchAll();
			for(Onetime oneTime:onetimeList){
				onetimeService.delete(oneTime);
			}
		
			return "registerdelay";

		}

		//ワンタイムテーブルの全データ消去
		List <Onetime> onetimeList=onetimeService.searchAll();
		for(Onetime oneTime:onetimeList){
			onetimeService.delete(oneTime);
		}
		
		return "registerdone";
	}


	@PostMapping("/AjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String day, Model model,@ModelAttribute YearMonth yaerMonth,BeanRegularTime Brtime){	
		User users=(User)session.getAttribute("Data");
		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		int id=users.getId();
		int intday=Integer.parseInt(day);
		Workdays workdays= workdaysService.findUseridYearMonthDay(id,yearMonth.getYear(),yearMonth.getMonth(),intday);
		System.out.println("before="+inputvalue);
		
		//0900のような:なしの入力がされた場合に間に:を挿入する
		if(inputvalue.length()==4){
			StringBuilder sb = new StringBuilder();
            sb.append(inputvalue);
			sb.insert(2, ":");
			inputvalue=sb.toString();  

		}
		System.out.println("after="+inputvalue);
		switch (name) {
			case "start":
				
				//開始終了休憩時間をそれぞれ分単位にして引き算する（終了-開始-休憩＝労働時間）
				int sminutes=userService.allminutes(inputvalue);
				int hminutes=userService.allminutes(workdays.getHalftime().toString());
				int eminutes=userService.allminutes(workdays.getEnd().toString());
				int wminutes=eminutes-hminutes-sminutes;
			    //分になった労働時間を00:00形式に変換
				String worktime=userService.wminutes(wminutes);
				//Stringからtime形式に変換しDBに登録
				workdays.setWorktime(userService.toTime(worktime));
				workdays.setStart(userService.toTime(inputvalue));//StringをlocalTimeに変換した後Timeに変換するメソッド
			break;
			
			case "end":
			sminutes=userService.allminutes(workdays.getStart().toString());
			hminutes=userService.allminutes(workdays.getHalftime().toString());
			eminutes=userService.allminutes(inputvalue);
			wminutes=eminutes-hminutes-sminutes;
			//分になった労働時間を00:00形式に変換
			worktime=userService.wminutes(wminutes);
			//Stringからtime形式に変換しDBに登録
			workdays.setWorktime(userService.toTime(worktime));
	
			workdays.setEnd(userService.toTime(inputvalue));
			break;

			case "halftime":
			sminutes=userService.allminutes(workdays.getStart().toString());
			hminutes=userService.allminutes(inputvalue);
			eminutes=userService.allminutes(workdays.getEnd().toString());
			wminutes=eminutes-hminutes-sminutes;
			//分になった労働時間を00:00形式に変換
			worktime=userService.wminutes(wminutes);
			//Stringからtime形式に変換しDBに登録
			workdays.setWorktime(userService.toTime(worktime));
			workdays.setHalftime(userService.toTime(inputvalue));
			
			break;

			case "worktime":
			 workdays.setWorktime(userService.toTime(inputvalue));
			break;
			
			case "other1":
			  workdays.setOther1(inputvalue);
			  break;
			  case "other2":
			  workdays.setOther2(inputvalue);
			  break;
			  case "other3":
			  workdays.setOther3(inputvalue);
			  break;
			} 
		workdaysService.update(workdays);

		WorkingListParam workingListParam=workdaysService.date(yearMonth.getYear(),yearMonth.getMonth());
		workingListParam.setMonth(yearMonth.getMonth());
		workingListParam.setYear(yearMonth.getYear());
		model.addAttribute("workingListParam", workingListParam);
		model.addAttribute("users", users);

		//備考仕様の有無を確認
		List<Otherpa>opList=workdaysService.oplist(users);
		model.addAttribute("opList", opList);
		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("Brtime",Brtime);
		return "list";
	}

	@GetMapping("/boxDEV")
	public String box(Model model){
		return "boxDEV";
	}

	@GetMapping("/success")
	public String success(Model model){
		return "success";
	}

	@GetMapping("/boxDownload")
	public String downloadBox(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ServletException {
		
		String clientId = WorkdaysProperties.boxClientId;
		String clientSecret = WorkdaysProperties.boxClientSecret;
		
		// String authorizationUrl = "https://account.box.com/api/oauth2/authorize?client_id="
		// 	+ clientId + "&response_type=code";
		
		// response.sendRedirect(authorizationUrl);

		String code = request.getParameter("code");
		System.out.println("CODE=" + code);
		
		BoxAPIConnection api = new BoxAPIConnection(
  			clientId,
  			clientSecret,
			code
		);

		session.setAttribute("api", api);

		//会社選択
		String email = (String)session.getAttribute("email");
		List <IndividualData>iDataList=individualService.findMail(email);
		//個別ユーザーから会社IDを取得し会社名のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
				
		for(IndividualData iData:iDataList){
			IdUser idUser=individualService.getIdUser(iData);
					
			idUserList.add(idUser);
		}
		
		model.addAttribute("idUserList", idUserList);
		return "choosetemplate";
	}

	@RequestMapping(value="/boxfiledownload")
	public String boxfiledownload (@RequestParam String company, Model model) throws FileNotFoundException {
			
		User users=(User)session.getAttribute("Data");
		model.addAttribute("user",users);
		//苗字と名前合わせてname
		String lastname = users.getLastname();
		String firstname = users.getFirstname();
		String name = lastname + "　" + firstname;

		//当日の年月取得
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		month=month+1; //カレンダーメソッドで取得した月は実際の月-1される（12月だったら11になる的な）ので+1して戻す
	
		List<Workdays> workdays =workdaysService.findYearMonth(users.getId(),year,month);

		Map<String, String> stHourMap = new HashMap<>();
		Map<String, String> stMinMap = new HashMap<>(); 
		Map<String, String> endHourMap = new HashMap<>(); 
		Map<String, String> endMinMap = new HashMap<>();
		Map<String, String> lunchTimeHourMap = new HashMap<>(); 
		Map<String, String> lunchTimeMinMap = new HashMap<>();
		Map<String, String> totalHourMap = new HashMap<>();
		Map<String, String> totalMinMap = new HashMap<>(); 
		Map<String, String> other1Map = new HashMap<>();
		Map<String, String> other2Map = new HashMap<>();
		Map<String, String> other3Map = new HashMap<>();

		int index = 1;
		for(Workdays workday:workdays){

			stHourMap.put("[sth"+index+"]", workday.getStart().toString().substring(0,2));
			stMinMap.put("[stm"+index+"]", workday.getStart().toString().substring(3,5));
			endHourMap.put("[edh"+index+"]", workday.getEnd().toString().substring(0,2));
			endMinMap.put("[edm"+index+"]", workday.getEnd().toString().substring(3,5));
			lunchTimeHourMap.put("[lth"+index+"]", workday.getHalftime().toString().substring(0,2));
			lunchTimeMinMap.put("[ltm"+index+"]", workday.getHalftime().toString().substring(3,5));
			totalHourMap.put("[tth"+index+"]", workday.getWorktime().toString().substring(0,2));
			totalMinMap.put("[ttm"+index+"]", workday.getWorktime().toString().substring(3,5));
			other1Map.put("[oa"+index+"]", workday.getOther1());
			other2Map.put("[ob"+index+"]", workday.getOther2());
			other3Map.put("[oc"+index+"]", workday.getOther3());

			index++;

		}
				
		//個別ユーザーTLから個人IDと会社IDを拾ってくる
		String[] values = company.split(":");
		int companyID = Integer.parseInt(values[0]);
		String companyName=values[1];
		
		String mail = users.getEmail();
		int individualID = Integer.valueOf(userService.findIndividualID(mail, companyID));
		
		//会社ID_input/会社名.xls
		String inputFilePath = getInputFolder(companyID).getAbsolutePath() + "/" + companyName + ".xls";
		//会社ID_output/個別ID_出力会社.xls
		String outputFilePath = getOutputFolder(companyID).getAbsolutePath() 
			+ "/" + individualID + "_" + companyName + ".xls";//AWSでない場合は/を\\に修正

		System.out.println("input: " + inputFilePath);
		System.out.println("output: " + outputFilePath);
		
		WorkdayMapping workdayMapping = new WorkdayMapping();
		List<String> errors = workdayMapping.outputExcel(inputFilePath, outputFilePath, 
			stHourMap, stMinMap, endHourMap, endMinMap, lunchTimeHourMap, lunchTimeMinMap,
			totalHourMap, totalMinMap, other1Map,other2Map,other3Map, mail, individualID, name
		);

		if (errors.size() != 0 || !errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "outputerror";
		}

		BoxAPIConnection api = (BoxAPIConnection)session.getAttribute("api");
		
		//すべてのフォルダ(id=0)下に出力用ファイルを作成
		BoxFolder parentFolder = new BoxFolder(api, "0");
		Iterable<Info> childrens = parentFolder.getChildren();
		String childFolderId = null;

		//フォルダ名の重複を確認
		for (Info info : childrens) {
			if (info.getName().equals(boxSaveFolderName)) {
				childFolderId = info.getID();
				break;
			}
		}
		if (childFolderId == null) {
			BoxFolder.Info childFolderInfo = parentFolder.createFolder(boxSaveFolderName);
			childFolderId = childFolderInfo.getID();
		}

		
		BoxFolder uploadFolder = new BoxFolder(api, childFolderId);
		BoxFolder.Info info = uploadFolder.getInfo();
		System.out.println("出力フォルダ名：" + info.getName() + ", 出力フォルダID:" + info.getID());

		File clientFile = new File(outputFilePath);
		String clientFileName = clientFile.getName();

		//ファイル名の重複を確認
		String fileId = null;
		for (BoxItem.Info itemInfo : uploadFolder) {
			if(itemInfo.getName().equals(clientFileName)) {
				//ファイルを更新
				fileId = itemInfo.getID();
				BoxFile updatefile = new BoxFile(api, fileId);
				FileInputStream stream = new FileInputStream(outputFilePath);
				updatefile.uploadNewVersion(stream);
			}
		}

		if(fileId == null) {
			FileInputStream input = new FileInputStream(outputFilePath);
			BoxFile.Info newFileInfo = uploadFolder.uploadFile(input, clientFileName);
		}

		// String url = WorkdaysProperties.host + "/success";
		// System.out.println("リダイレクト先：" + url);
		// response.sendRedirect(url);

		return "success";
	}
}