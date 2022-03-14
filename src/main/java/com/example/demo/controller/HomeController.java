package com.example.demo.controller;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.net.HttpURLConnection;

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
import com.example.demo.model.Holiday;
import com.example.demo.model.IdUser;
import com.example.demo.model.IndividualData;
import com.example.demo.model.Login;
import com.example.demo.model.Mail;
import com.example.demo.model.Onetime;
import com.example.demo.model.CompanyInfo;
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
	 session.removeAttribute("Data");
		
	
		
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
		int lastmonth=yearMonth.getLastmonth();
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
	 Model model, @ModelAttribute SuperUserLogin suserlogin, BindingResult sudoresult){
		
		String id = login.getEmail();
		String pass = login.getPassword();

			//管理者の場合は管理者ページに移行
		SuperUserLogin superUser = userService.findEmailAndPass(id, pass);
		
		if (superUser != null && !sudoresult.hasErrors()) {
			session.setAttribute("superUser", superUser);
			int companyId=superUser.getCompanyID();
			session.setAttribute("companyId", companyId);
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
		//メアドから個別ユーザーのリストを作成する
		List <IndividualData>iDataList=individualService.findMail(login.getEmail());
		model.addAttribute("iDataList", iDataList);
		
		
		//個別ユーザーから会社IDを取得し会社のリストを作る
		List<IdUser>idUserList=new ArrayList<IdUser>();
		
		for(IndividualData iData:iDataList){
			IdUser idUser=new IdUser();
			CompanyInfo companyInfo= companyInfoService.findByCompanyID(iData.getCompanyID());
			idUser.setCompany1(iData.getCompany1());// companyInfo.getCompanyName();
			idUser.setCompany2(iData.getCompany2());
			idUser.setCompany3(iData.getCompany3());
			idUser.setBanned(iData.getBanned());
			idUser.setCompanyID(iData.getCompanyID());
			idUser.setCompanyName(companyInfo.getCompanyName());
			idUserList.add(idUser);

		}

		model.addAttribute("idUserList", idUserList);
		System.out.println(idUserList);
		
		session.setAttribute("Data",users);
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int calendermonth=cal.get(Calendar.MONTH);//当月11月の時calendermonth=10
		int month=calendermonth+1;//month=11 カレンダーメソッドで拾ってくる値は実際の月-1だから1足した上で検索しないといけない
		int userid=users.getId();
		Workdays workdays =workdaysService.findUseridYearMonthDay(userid,year, month,1);
		
		if (workdays==null){
			//もしDBにその月の勤怠データがナカッタラ→勤怠データをDBに登録
			//id year month はこの時点で回収済み　残りは日付と曜日
			//年と月からforで回転させる回数を出してどうにかこうにか？
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, calendermonth);//カレンダーメソッドなので使うのは実際の月-1の方
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
			for(int i=0;i<lastDayOfMonth;i++){
				String yobi[] = {"日","月","火","水","木","金","土"};
				cal.set(year, calendermonth, i+1);
				String weekday=yobi[cal.get(Calendar.DAY_OF_WEEK)-1];
				workdaysService.insertdata(userid, year, month, i+1, weekday);//int d=が左辺にあったから動かなくなったら足すこと
			}
			List<Holiday> holidays=holidayService.findyearmonth(year, month);
			for(Holiday holiday:holidays) {
				int holiday2=holiday.getDay();
				workdays =workdaysService.findUseridYearMonthDay(userid,year, month,holiday2);
				workdays.setHoliday(1);
				workdaysService.update(workdays);
			}

		}
		
		//スタートやエンドの値を一括で変形する→workingListParm全体の値を別の箱に入れる→箱をjsonで変換する
		WorkingListParam workingListParam = userService.searchAll(users);//リストに出す用の検索は必要
		workingListParam.setMonth(month);
		workingListParam.setYear(year);
		model.addAttribute("workingListParam", workingListParam);
		model.addAttribute("users", users);

		//備考1,2,3に会社名を添付する処理をする
		//会社のテンプレートファイルでoa,ob,coが使われているかジャッジ
		CellvalueGet cellgetvalue=new CellvalueGet();
		
		
		List<Otherpa>opList=new ArrayList<Otherpa>();
		Judgeused judgeused1=cellgetvalue.GetCellvalue(users.getCompany1());
		Judgeused judgeused2=cellgetvalue.GetCellvalue(users.getCompany2());
		Judgeused judgeused3=cellgetvalue.GetCellvalue(users.getCompany3());
		
		Otherpa opa_oa=new Otherpa();
		opa_oa.setCompany1(judgeused1.getOa());
		opa_oa.setCompany2(judgeused2.getOa());
		opa_oa.setCompany3(judgeused3.getOa());

		opList.add(opa_oa);
		
		Otherpa opa_ob=new Otherpa();
		opa_ob.setCompany1(judgeused1.getOb());
		opa_ob.setCompany2(judgeused2.getOb());
		opa_ob.setCompany3(judgeused3.getOb());
		opList.add(opa_ob);

		Otherpa opa_oc=new Otherpa();
		opa_oc.setCompany1(judgeused1.getOc());
		opa_oc.setCompany2(judgeused2.getOc());
		opa_oc.setCompany3(judgeused3.getOc());
		opList.add(opa_oc);

		model.addAttribute("opList", opList);
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
		CellvalueGet cellgetvalue=new CellvalueGet();
		Judgeused judgeused=cellgetvalue.GetCellvalue(users.getCompany2());
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

	//会社選択時の処理
	@RequestMapping(value="/companyselect")
	public String CompanySelect (@RequestParam String company, Model model) {
			
		User users=(User)session.getAttribute("Data");
		model.addAttribute("user",users);
		//苗字と名前合わせてname
		String lastname=users.getLastname();

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

			stHourMap.put("sth"+index, workday.getStart().toString().substring(0,2));
			stMinMap.put("stm"+index, workday.getStart().toString().substring(3,5));
			endHourMap.put("edh"+index, workday.getEnd().toString().substring(0,2));
			endMinMap.put("edm"+index, workday.getEnd().toString().substring(3,5));
			lunchTimeHourMap.put("lth"+index, workday.getHalftime().toString().substring(0,2));
			lunchTimeMinMap.put("ltm"+index, workday.getHalftime().toString().substring(3,5));
			totalHourMap.put("tth"+index, workday.getWorktime().toString().substring(0,2));
			totalMinMap.put("ttm"+index, workday.getWorktime().toString().substring(3,5));
			other1Map.put("oa"+index, workday.getOther1());
			other2Map.put("ob"+index, workday.getOther2());
			other3Map.put("oc"+index, workday.getOther3());

			index++;

		}
				
		//個別ユーザーTLから個人IDと会社IDを拾ってくる
		System.out.println(company);
		String[] values = company.split(":");
		int companyID = Integer.parseInt(values[0]);
		String companyName=values[1];
		
		String mail = users.getEmail();
		int individualID = Integer.valueOf(userService.findIndividualID(mail, companyID));
		
		//会社ID_input/会社名.xls
		String inputFilePath = getInputFolder(companyID).getAbsolutePath() + "\\" + companyName + ".xls";
		//会社ID_output/個別ID_出力会社.xls
		String outputFilePath = getOutputFolder(companyID).getAbsolutePath() 
			+ "\\" + individualID + "_" + companyName + ".xls";

		System.out.println("input: " + inputFilePath);
		System.out.println("output: " + outputFilePath);

		session.setAttribute("output", outputFilePath);
		
		WorkdayMapping workdayMapping = new WorkdayMapping();
		List<String> errors = workdayMapping.outputExcel(inputFilePath, outputFilePath, 
			stHourMap, stMinMap, endHourMap, endMinMap, lunchTimeHourMap, lunchTimeMinMap,
			totalHourMap, totalMinMap, other1Map,other2Map,other3Map
		);

		if (errors.size() != 0 || !errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "outputerror";
		}

		model.addAttribute("filePath", outputFilePath);

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
			return "/repass";
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
		return "/newpassword";
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
	public String universalregister(@ModelAttribute User user, Model model,@RequestParam String email){

		user.setEmail(email);
		session.setAttribute("user",user);
		model.addAttribute("user",user);
		return "universalregister";
	}

    //会員情報入力→確認画面へ
	@PostMapping("/universalregister")
	public String universalconfirm(@Validated @ModelAttribute User user,BindingResult result){
		
        if (result.hasErrors()){
			// エラーがある場合登録画面に戻る
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
	
	
	
	Onetime onetime=new Onetime();
	onetime.setEmail(user.getEmail());
	onetime.setLastname(user.getLastname());
	onetime.setFirstname(user.getFirstname());
	onetime.setPassword(user.getPassword());
	onetime.setId(1);
	Date date = new Date();
	Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
	onetime.setDatetime(date);
	
	
	//Date型の持つ日時を表示
    /*System.out.println(date);
	long longer=date.getTime();

	System.out.println(longer);
	long longer2=longer+300;
	
    System.out.println("result="+longer2+longer);*/
	
	

	onetimeService.insert(onetime);
	mailsendService.mailsend(user.getEmail(),onetimeText);

	


	//エラーがなければ仮登録完了ページに遷移
	return "/onetime";
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
		
		System.out.println("now="+date);
		System.out.println("before="+onetime.getDatetime());
		System.out.println();
		System.out.println(nowlong-ontimelong);
		if((nowlong-ontimelong)/1000<180){
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
	public String AjaxServlet(@RequestParam String inputvalue, String name,String day, Model model){	
		User users=(User)session.getAttribute("Data");
		YearMonth yearMonth=userService.nowYearMonth();
		int id=users.getId();
		int intday=Integer.parseInt(day);
		Workdays workdays= workdaysService.findUseridYearMonthDay(id,yearMonth.getYear(),yearMonth.getMonth(),intday);

		switch (name) {
			case "start":
			 workdays.setStart(userService.toTime(inputvalue));//StringをlocalTimeに変換した後Timeに変換するメソッド
			break;
			
			case "end":
			 workdays.setEnd(userService.toTime(inputvalue));
			break;

			case "halftime":
			 workdays.setEnd(userService.toTime(inputvalue));
			break;

			case "worktime":
			 workdays.setEnd(userService.toTime(inputvalue));
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

		
		WorkingListParam workingListParam = userService.searchAll(users);
		model.addAttribute("workingListParam", workingListParam);
		model.addAttribute("users", users);

		CellvalueGet cellgetvalue=new CellvalueGet();
		
		
		List<Otherpa>opList=new ArrayList<Otherpa>();
		Judgeused judgeused1=cellgetvalue.GetCellvalue(users.getCompany1());
		Judgeused judgeused2=cellgetvalue.GetCellvalue(users.getCompany2());
		Judgeused judgeused3=cellgetvalue.GetCellvalue(users.getCompany3());
		
		Otherpa opa_oa=new Otherpa();
		opa_oa.setCompany1(judgeused1.getOa());
		opa_oa.setCompany2(judgeused2.getOa());
		opa_oa.setCompany3(judgeused3.getOa());

		opList.add(opa_oa);
		
		Otherpa opa_ob=new Otherpa();
		opa_ob.setCompany1(judgeused1.getOb());
		opa_ob.setCompany2(judgeused2.getOb());
		opa_ob.setCompany3(judgeused3.getOb());
		opList.add(opa_ob);

		Otherpa opa_oc=new Otherpa();
		opa_oc.setCompany1(judgeused1.getOc());
		opa_oc.setCompany2(judgeused2.getOc());
		opa_oc.setCompany3(judgeused3.getOc());
		opList.add(opa_oc);

		model.addAttribute("opList", opList);
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
	public void downloadBox(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ServletException {
		
		String clientId = WorkdaysProperties.boxClientId;
		String clientSecret = WorkdaysProperties.boxClientSecret;
		String boxSaveFolderName = WorkdaysProperties.boxSaveFolderName;
		String clientFilePath = (String)session.getAttribute("output");
		File clientFile = new File(clientFilePath);
		String clientFileName = clientFile.getName();
		
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
		System.out.println("フォルダ：" + info.getName() + ", ID:" + info.getID());

		//ファイル名の重複を確認
		String fileId = null;
		for (BoxItem.Info itemInfo : uploadFolder) {
			if(itemInfo.getName().equals(clientFileName)) {
				//ファイルを更新
				fileId = itemInfo.getID();
				BoxFile updatefile = new BoxFile(api, fileId);
				FileInputStream stream = new FileInputStream(clientFilePath);
				updatefile.uploadNewVersion(stream);
			}

		}

		if(fileId == null) {
			FileInputStream input = new FileInputStream(clientFilePath);
			BoxFile.Info newFileInfo = uploadFolder.uploadFile(input, clientFileName);
		}
		System.out.println("出力先：https://app.box.com/file/" + uploadFolder.getID());

		String url = WorkdaysProperties.host + "/success";
		System.out.println(url);
		response.sendRedirect(url);
	}
}