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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.Login;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;

@Controller
public class HomeController
{
	private final UserRepository repository;
    @Autowired //← コンストラクタが１つの場合、@Autowiredは省略できます
    HttpSession session;
    @Autowired
    UserService userService;
    @Autowired
    MailSendService mailsendService;
    @Autowired
    WorkdaysService workdaysService;
    public HomeController(UserRepository repository)
    {
        this.repository = repository;

    }

    //トップページがログイン画面になる
    @GetMapping("/")
	  public String login(@ModelAttribute Login login)
	{
		return "login";
	}



	@GetMapping("/register")
    public String register(@ModelAttribute User user)
	{
        return "register";
    }


//メール送信処理
	@GetMapping(value = "/sendmail")
	 public String mailsend()throws MessagingException
		{
		mailsendService.send();
			return "done";
		}




	@PostMapping("/")

    public String confirm(@Validated @ModelAttribute User user,
            BindingResult result)
	{
		if (result.hasErrors())
		{
            // エラーがある場合、index.htmlに戻る
            return "register";
        }
	return "confirm";
}


	//ログイン時の処理
	@PostMapping("/login")
	 public String sucsess(@Validated  @ModelAttribute Login login,Model model,
	            BindingResult result)
	{
		User users=userService.findEmailPassword(login.getEmail(),login.getPassword());

			if (result.hasErrors()||users==null)
			{
	            // エラーがある場合、login.htmlに戻る
	            return "login";
	        }

		session.setAttribute("Data",users);
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int calendermonth=cal.get(Calendar.MONTH);//calendermonth=10
		int month=calendermonth+1;//month=11 カレンダーメソッドで拾ってくる値は実際の月-1だから1足した上で検索しないといけない
		String year2 = String.valueOf(year);
		String month2 = String.valueOf(month);//検索するのは実際の月＝month
		int userid=users.getId();

		Workdays workdays =workdaysService.findUseridYearMonthDay(userid,year2, month2,"1");

		if (workdays==null)
		{

		//もしDBにその月の勤怠データがナカッタラ　
		//id year month はこの時点で回収済み　残りは日付と曜日
		//年と月からforで回転させる回数を出してどうにかこうにか？


			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, calendermonth);//カレンダーメソッドなので使うのは実際の月-1の方
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
			for(int i=0;i<lastDayOfMonth;i++)
			{
			String yobi[] = {"日","月","火","水","木","金","土"};
			cal.set(year, calendermonth, i+1);
			String weekday=yobi[cal.get(Calendar.DAY_OF_WEEK)-1];
			int d=workdaysService.insertdata(userid, year, month, i+1, weekday);
			}
		}



		    WorkingListParam workingListParam = userService.searchAll();

		    workingListParam.setYear(2021);
		    workingListParam.setMonth(11);

		    model.addAttribute("workingListParam", workingListParam);
		    return "list";
		  }

		//return "workdays";
	//}





	 @RequestMapping(value = "/listUpdate", method = RequestMethod.POST)
	  public String listUpdate(@Validated @ModelAttribute WorkingListParam workingListParam, BindingResult result, Model model) {
		 /*System.out.println(workingListParam.getYear());
		 System.out.println(workingListParam.getMonth());
		 List<WorkingData> tests =workingListParam.getWorkingDataList();
		 for(WorkingData data:tests) {
			 System.out.println(data.getDay());
			 System.out.println(data.getStart());
		 }*/
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
	 @RequestMapping(value = "/beforelistUpdate", method = RequestMethod.POST)
	  public String beforelistUpdate(@Validated @ModelAttribute WorkingListParam workingListParam, BindingResult result, Model model) {
	    if (result.hasErrors()) {
	      List<String> errorList = new ArrayList<String>();
	      for (ObjectError error : result.getAllErrors()) {
	        if (!errorList.contains(error.getDefaultMessage())) {
	          errorList.add(error.getDefaultMessage());
	        }
	      }
	      model.addAttribute("validationError", errorList);
	      return "beforeworkdays";
	    }
	    // ユーザー情報の更新
	    userService.updatebeforeAll(workingListParam);

	    return "beforeworkdays";
	  }

	 @RequestMapping(value = "/nextlistUpdate", method = RequestMethod.POST)
	  public String nextlistUpdate(@Validated @ModelAttribute WorkingListParam workingListParam, BindingResult result, Model model) {
	    if (result.hasErrors()) {
	      List<String> errorList = new ArrayList<String>();
	      for (ObjectError error : result.getAllErrors()) {
	        if (!errorList.contains(error.getDefaultMessage())) {
	          errorList.add(error.getDefaultMessage());
	        }
	      }
	      model.addAttribute("validationError", errorList);
	      return "nextworkdays";
	    }
	    // ユーザー情報の更新
	    userService.updatenextAll(workingListParam);

	    return "nextworkdays";
	  }

	@PostMapping("/revisedone")
	public String revisedone(@Validated @ModelAttribute User user,
			BindingResult result)
	{
			/*if (result.hasErrors()) {
	            // エラーがある場合、login.htmlに戻る
	            return "login";
	        }*/
		return "sucsess";
	}




	@PostMapping("/regist")
    public String regist(@Validated @ModelAttribute User user, BindingResult result, Model model)
	{System.out.println(1);
		model.addAttribute("user", repository.findAll());
        if (result.hasErrors())
        {
            return "confirm";
        }
        // COMMENTテーブル：コメント登録
        repository.save(user);

        // ルートパス("/") にリダイレクトします
        return "done";
    }
}