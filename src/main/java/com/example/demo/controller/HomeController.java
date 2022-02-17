package com.example.demo.controller;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

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

import com.example.demo.model.Holiday;
import com.example.demo.model.Login;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.SuperUser;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HolidayService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdayMapping;
import com.example.demo.service.WorkdaysService;
import com.google.gson.Gson;
@Controller
public class HomeController{
	private final UserRepository repository;
    @Autowired //← コンストラクタが１つの場合、@Autowiredは省略できます
    HttpSession session;
    @Autowired
    UserService userService;
    @Autowired
    MailSendService mailsendService;
    @Autowired
    WorkdaysService workdaysService;
	@Autowired
    HolidayService holidayService;
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
	//会員登録ページに移行
	@GetMapping("/register")
	public String register(@ModelAttribute User user){
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
		//メアドに重複があった場合重複画面に飛ぶ
		
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
		SuperUserLogin superUser = userService.findIdAndPass(id, pass);
		if (superUser != null && !sudoresult.hasErrors()) {
			return "superuser";
		}
		
		User users=userService.findEmailPassword(login.getEmail(),login.getPassword());
		if (result.hasErrors()||users==null){
			// エラーがある場合、login.htmlに戻る
			return "login";
		}
		//利用禁止ユーザーの場合loginに
		if(users.getBanned()==1){
			return "banned";
		}

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
		System.out.println(company);
		String propertyfileName = company;
		
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
		Map<String, String> otherMap = new HashMap<>();

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
			otherMap.put("ot"+index, workday.getOther1());

			index++;

		}
		
		String inputFilePath = "C:/久保さん/PropertyFiles/" + propertyfileName+".xls";
		String outputFilePath = "C:/久保さん/PropertyFiles/勤怠表_"+lastname+"_"+year+"年"+month+"月.xls";

		WorkdayMapping workdayMapping = new WorkdayMapping();
		workdayMapping.outputExcel(inputFilePath, outputFilePath, 
			stHourMap, stMinMap, endHourMap, endMinMap, lunchTimeHourMap, lunchTimeMinMap,
			 totalHourMap, totalMinMap, otherMap);

		return "done";
	}

	

	/*@GetMapping("/userlist")
	public String userlist(Model model){
		System.out.println("a");
		List<User>userList=userService.findAll();
		model.addAttribute("userList", userList);
		return "/userlist";


	}*/

	
}