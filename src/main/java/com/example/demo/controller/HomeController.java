package com.example.demo.controller;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Login;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
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
		if (result.hasErrors()){
			// エラーがある場合、index.htmlに戻る
            return "register";
        }
		return "confirm";
	}
	//会員情報をDBに登録
	@PostMapping("/regist")
    public String regist(@Validated @ModelAttribute User user, BindingResult result, Model model){
		System.out.println(1);
		model.addAttribute("user", repository.findAll());
        if (result.hasErrors()){
			return "confirm";
		}
		// COMMENTテーブル：コメント登録
		repository.save(user);
		// ルートパス("/") にリダイレクトします
		return "done";
    }
	
	//ログイン時の処理
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
			//もしDBにその月の勤怠データがナカッタラ→勤怠データをDBに登録
			//id year month はこの時点で回収済み　残りは日付と曜日
			//年と月からforで回転させる回数を出してどうにかこうにか？
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
	//return "workdays";
	//}


	//ログイン時の処理
	@RequestMapping("/login2")
	//@ResponseBody
	@CrossOrigin(origins = "*")
	public String sucsess2(@Validated  @ModelAttribute Login login,Model model,BindingResult result){
		User users=userService.findEmailPassword(login.getEmail(),login.getPassword());
		if (result.hasErrors()||users==null){
			// エラーがある場合、login.htmlに戻る
			return "login";
		}

		session.setAttribute("Data",users);
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int calendermonth=cal.get(Calendar.MONTH);//calendermonth=10
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
		}
		
		//WorkingListParam workingListParam = userService.searchAll();→本来のやつ
		//スタートやエンドの値を一括で変形する→workingListParm全体の値を別の箱に入れる→箱をjsonで変換する
		WorkingListParam workingListParam = userService.searchAll(users);//リストに出す用の検索は必要
		model.addAttribute("workingListParam", workingListParam);
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

}