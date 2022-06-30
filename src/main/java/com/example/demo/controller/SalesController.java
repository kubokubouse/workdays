package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.Idregulartime;
import com.example.demo.model.Login;
import com.example.demo.model.User;
import com.example.demo.model.RegularTime;
import com.example.demo.model.Workdays;
import com.example.demo.model.YearMonth;
import com.example.demo.service.HolidayService;
import com.example.demo.service.IdRegularTimeService;
import com.example.demo.service.IndividualService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.OnetimeService;
import com.example.demo.service.RegularTimeService;
import com.example.demo.service.SuperUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.WorkdaysService;
import com.example.demo.WorkdaysProperties;

@Controller
public class SalesController extends WorkdaysProperties{
	
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
    
    @Autowired
    IdRegularTimeService iRService;

    @Autowired
    RegularTimeService rtService;

	
    
    @GetMapping("/salesforce")
	public String Salesforce (@ModelAttribute Login login) {
        System.out.println("ai");
        return "salesforce";
    }

    @GetMapping("/ajaxtest")
	public String ajaxtest () {
       
        return "ajaxtest";
    }
    
    @GetMapping("/testiframe")
	public String testiframe () {
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, localdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");
	
		WebDriver driver = new ChromeDriver();//local
		System.out.println("①");

        try {
            //ログイン画面にアクセス
            driver.get("http://workdays.jp/iframe");
			System.out.println("②＝"+driver.getCurrentUrl());

            WebElement iframe = driver.findElement(By.id("iframe"));
			driver.switchTo().frame(iframe) ;
	
            driver.findElement(By.id("email")).sendKeys("ryowhite@icloud.com");

            
            driver.findElement(By.id("pass")).sendKeys("0000") ;
            

           

            driver.findElement(By.id("login")).click();
            

            System.out.println("③＝"+driver.getCurrentUrl());
			
        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }
		
        return "testiframe";
    }

    @GetMapping("/testawsiframe")
	public String testawsiframe () {
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, awsdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");
	
		WebDriver driver = new ChromeDriver();//local
		System.out.println("①");

        try {
            //ログイン画面にアクセス
            driver.get("http://workdays.jp/iframe");
			System.out.println("②＝"+driver.getCurrentUrl());

            WebElement iframe = driver.findElement(By.id("iframe"));
			driver.switchTo().frame(iframe) ;
	
            driver.findElement(By.id("email")).sendKeys("ryowhite@icloud.com");

            
            driver.findElement(By.id("pass")).sendKeys("0000") ;
            

           

            driver.findElement(By.id("login")).click();

            System.out.println("③＝"+driver.getCurrentUrl());
			
        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }
		
        return "testiframe";
    }

    @GetMapping("/testawsajax")
	public String testA(Model model){
		
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, awsdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		
		
		//Chromeドライバーのインスタンス
        WebDriver driver = new ChromeDriver(options); //本番環境
		//WebDriver driver = new ChromeDriver();//local
		System.out.println("①");
        //暗黙的な待機の設定（ブラウザ操作時の要素を見つけるまで最大5秒待つ）
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        try {
            //ログイン画面にアクセス
            driver.get("https://mki.lightning.force.com/lightning/page/home");
			System.out.println("②＝"+driver.getCurrentUrl());

			
        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }
		
		return "ajaxtest";
	}


    @GetMapping("/testlocal")
	public String test4(Model model){
		
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, localdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		
		
		//Chromeドライバーのインスタンス
        //WebDriver driver = new ChromeDriver(options); //本番環境
		WebDriver driver = new ChromeDriver();//local
		System.out.println("①");
        //暗黙的な待機の設定（ブラウザ操作時の要素を見つけるまで最大5秒待つ）
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        try {
            //ログイン画面にアクセス
            driver.get("https://mki.my.salesforce.com/");
			System.out.println("②＝"+driver.getCurrentUrl());

			WebElement element=driver.findElement(By.xpath("//*[@id='idp_section_buttons']/button"));
	
			String s=element.getAttribute("onclick");

            Pattern pattern2 = Pattern.compile("'");
            String[] splitStr = pattern2.split(s);
            
            String cgc=splitStr[1];
            String id=splitStr[3];
            
            System.out.println(splitStr[1]);
            System.out.println(splitStr[3]);

            //idは拾ってこれたのでidを埋め込む作業が始まる

            driver.get("http://localhost:8080/html/copytestaws.html");

            driver.findElement(By.id("first")).sendKeys(cgc);
            driver.findElement(By.id("second")).sendKeys(id);
            
            driver.findElement(By.id("sign")).click();
           
            System.out.println("❹＝"+driver.getCurrentUrl());
        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }
		
		return "test2";
	}

    @GetMapping("/testaws")
	public String testaws(Model model){
		
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, awsdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		
		
		//Chromeドライバーのインスタンス
        WebDriver driver = new ChromeDriver(options); //本番環境
		//WebDriver driver = new ChromeDriver();//local
		System.out.println("①");
        //暗黙的な待機の設定（ブラウザ操作時の要素を見つけるまで最大5秒待つ）
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        try {
            //ログイン画面にアクセス
            driver.get("https://mki.my.salesforce.com/");
			System.out.println("②＝"+driver.getCurrentUrl());

			WebElement element=driver.findElement(By.xpath("//*[@id='idp_section_buttons']/button"));
	
			String s=element.getAttribute("onclick");

            Pattern pattern2 = Pattern.compile("'");
            String[] splitStr = pattern2.split(s);
            
            String cgc=splitStr[1];
            String id=splitStr[3];
            
            System.out.println(splitStr[1]);
            System.out.println(splitStr[3]);

            //idは拾ってこれたのでidを埋め込む作業が始まる
            
            driver.get("https://workdays.jp/html/copytestaws.html");

            driver.findElement(By.id("first")).sendKeys(cgc);
            driver.findElement(By.id("second")).sendKeys(id);
            
            driver.findElement(By.id("sign")).click();

            //サインオン成功
            System.out.println("サインオン後のURL＝"+driver.getCurrentUrl());

            //定時退勤ボタンを押下
			//driver.findElement(By.id("btnTetInput")).click();
			//System.out.println("定時退勤押下後＝"+driver.getCurrentUrl());

 
        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }
		
		return "testaws";
	}


    @GetMapping("/go_work")
	public String go () {
		
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        User users=(User)session.getAttribute("Data");
        
        Workdays workday=workdaysService.findUseridYearMonthDay(users.getId(), year, month, day);
        
       
        
        
        String shour=String.valueOf(hour);
        if(hour<10){
            shour="0"+hour;
        }
        
        String inputvalue=shour+":"+minute;
        workday.setStart(userService.toTime(inputvalue));
        workdaysService.update(workday);
        System.out.println(inputvalue);
        return "done";
    }

    @GetMapping("/leave_work")
	public String leave () {
		
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        User users=(User)session.getAttribute("Data");
        
        Workdays workday=workdaysService.findUseridYearMonthDay(users.getId(), year, month, day);
        
       
        String shour=String.valueOf(hour);
        if(hour<10){
            shour="0"+hour;
        }
        
        String inputvalue=shour+":"+minute;
        workday.setEnd(userService.toTime(inputvalue));
        

        //労働時間の算出→モデルにset
        int sminutes=userService.allminutes(workday.getStart().toString());
		int hminutes=userService.allminutes(workday.getHalftime().toString());
		int eminutes=userService.allminutes(inputvalue);
		int wminutes=eminutes-hminutes-sminutes;
		//分になった労働時間を00:00形式に変換
		String worktime=userService.wminutes(wminutes);
		//Stringからtime形式に変換しDBに登録
		workday.setWorktime(userService.toTime(worktime));	
        workdaysService.update(workday);
        return "done";
    }

    //定時出勤コマンド
    @GetMapping("/de_go_work")
	public String dego () {
		
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        User users=(User)session.getAttribute("Data");
        
        Workdays workday=workdaysService.findUseridYearMonthDay(users.getId(), year, month, day);
    
        Idregulartime irt=iRService.findId(users.getId());
       
        BeanRegularTime br=new BeanRegularTime();
        if(irt==null){
            RegularTime rt=rtService.findId(1);
			//定時情報をbeanRegularTimeに移植
			br=rtService.brtime(rt);
        }
        else{
            br=iRService.brtime(irt);
        }

        
        String inputvalue=br.getStart();
        workday.setStart(userService.toTime(inputvalue));
        workdaysService.update(workday);
        System.out.println(inputvalue);
        return "done";
    }

    //定時退勤コマンド
    @GetMapping("/de_leave_work")
	public String deleave () {
		
        //年月日を取得し更新する勤怠データを特定
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        User users=(User)session.getAttribute("Data");
        
        Workdays workday=workdaysService.findUseridYearMonthDay(users.getId(), year, month, day);
    
        

        Idregulartime irt=iRService.findId(users.getId());
       
        BeanRegularTime br=new BeanRegularTime();
        if(irt==null){
            RegularTime rt=rtService.findId(1);
			//定時情報をbeanRegularTimeに移植
			br=rtService.brtime(rt);
        }
        else{
            br=iRService.brtime(irt);
        }

        String inputvalue=br.getEnd();
        System.out.println(inputvalue);

        workday.setEnd(userService.toTime(inputvalue));

        //労働時間を計算しworkdayにset    
        int sminutes=userService.allminutes(workday.getStart().toString());
		int hminutes=userService.allminutes(workday.getHalftime().toString());
		int eminutes=userService.allminutes(inputvalue);
		int wminutes=eminutes-hminutes-sminutes;
		//分になった労働時間を00:00形式に変換
		String worktime=userService.wminutes(wminutes);
		//Stringからtime形式に変換しDBに登録
		workday.setWorktime(userService.toTime(worktime));	
        workdaysService.update(workday);

        
        
        return "done";
    }


    //定時出勤ボタンを押した時の反応
    @PostMapping("/salcestart")
	public String rgtime (BeanRegularTime br) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        User users=(User)session.getAttribute("Data");
        
        Workdays workday=workdaysService.findUseridYearMonthDay(users.getId(), year, month, day);
    
        Idregulartime irt=iRService.findId(users.getId());

        //個別定時テーブルが空だった場合　全体定時テーブルから定時情報を取得
        if(irt==null){
			RegularTime rt=rtService.findId(1);
			//全体定時テーブルの定時情報をbeanRegularTimeに移植
			BeanRegularTime br2=rtService.brtime(rt);

			//個別が空かつ入力された定時が全体定時と同じ場合→何もなし
			if(br.getStart().equals(br2.getStart())&&br.getEnd().equals(br2.getEnd())&&br.getHalftime().equals(br2.getHalftime())){

			}
			//個別が空かつ入力された定時が全体定時と違う場合→入力値を個別定時テーブルに新規作成する
			else{
				Idregulartime idtime2=new Idregulartime();
				idtime2.setStart(br.getStart());
				idtime2.setEnd(br.getEnd());
				idtime2.setHalftime(br.getHalftime());
				idtime2.setUserid(users.getId());
				iRService.insert(idtime2);
			}
			
		}
		//個別定時テーブルにデータがあった場合 個別定時テーブルから定時情報を取得
		else{
			//idregulrをbeanRegularに変換
			BeanRegularTime br2=iRService.brtime(irt);

			//個別が空でないかつ入力された定時が個別定時と同じ場合→何もなし
			if(br.getStart().equals(br2.getStart())&&br.getEnd().equals(br2.getEnd())&&br.getHalftime().equals(br2.getHalftime())){

			}
			//個別が空でないかつ入力された定時が個別定時と違う場合→入力値を個別定時テーブルで更新する
			else{
				irt.setStart(br.getStart());
				irt.setEnd(br.getEnd());
				irt.setHalftime(br.getHalftime());
				irt.setUserid(users.getId());
				iRService.update(irt);
			}

			

		}


        
        String inputvalue=br.getStart();
        System.out.println(inputvalue);
        workday.setStart(userService.toTime(inputvalue));
        workdaysService.update(workday);
        
        return "done";
        
    }

    //勤怠記録ページに遷移
    @GetMapping("/record")
	public String record (Model model) {
			
		User users=(User)session.getAttribute("Data");
        //個別定時テーブルにデータがあるか確認
		Idregulartime idtime=iRService.findId(users.getId());
		BeanRegularTime br=new BeanRegularTime();
		//個別定時テーブルが空だった場合　全体定時テーブルから定時情報を取得
		if(idtime==null){
			RegularTime rt=rtService.findId(1);
			//定時情報をbeanRegularTimeに移植
			br=rtService.brtime(rt);
			model.addAttribute("br", br);
		
			
		}
		else{
			br=iRService.brtime(idtime);
			model.addAttribute("br", br);
			
		}
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        //ap…午前だったら0が、午後だったら1が返却値になる

        System.out.println("そうなの？hour="+hour);


        //ここまででタイムスタンプの値が分と時間で算出されたことになる

        //個別ないし全体の定時を取得
        String destart=br.getStart();
        System.out.println(destart);
        //前の1文字(09:00の0)を切り出す
        String first=destart.substring(0,1);
        System.out.println("first="+first);
        //もし0なら09から0を省く
        int ihour;
        if(Integer.parseInt(first)==0){
            ihour=Integer.parseInt(destart.substring(1,2));
        }
        else{
            ihour=Integer.parseInt(destart.substring(0,2));
        }

        //前の1文字(09:08の後ろの0)を切り出す
        String second=destart.substring(3,4);
        System.out.println("second="+second);
        //もし0なら08から0を省く
        int iminutes;
        if(Integer.parseInt(second)==0){
            iminutes=Integer.parseInt(destart.substring(4,5));
        }
        else{
            iminutes=Integer.parseInt(destart.substring(3,5));
        }


        //個別ないし全体の退勤定時を取得
        String deend=br.getEnd();
        System.out.println("deend="+deend);
        //前の1文字(09:00の0)を切り出す
        String efirst=deend.substring(0,1);
        //もし0なら09から0を省く
        int ehour;
        if(Integer.parseInt(efirst)==0){
            ehour=Integer.parseInt(deend.substring(1,2));
        }
        else{
            ehour=Integer.parseInt(deend.substring(0,2));
        }

        //前の1文字(09:00の後ろの0)を切り出す
        String esecond=deend.substring(3,4);
        System.out.println("esecond="+esecond);
        //もし0なら09から0を省く
        int eminutes;
        if(Integer.parseInt(esecond)==0){
            eminutes=Integer.parseInt(deend.substring(4,5));
        }
        else{
            eminutes=Integer.parseInt(deend.substring(3,5));
        }


        System.out.println("hour="+hour);
        System.out.println("minutes="+minute);
        System.out.println("ihour="+ihour);
        System.out.println("iminutes="+iminutes);
        System.out.println("ehour="+ehour);
        System.out.println("eminutes="+eminutes);

        LocalDateTime dateTs=LocalDateTime.of(2018, 5, 1, hour, minute, 20);
        LocalDateTime dateSt=LocalDateTime.of(2018, 5, 1, ihour, iminutes, 20);
        LocalDateTime dateEt=LocalDateTime.of(2018, 5, 1, ehour, eminutes, 20);

        
        //出勤退勤ボタンを押したかどうか確認する日付を特定する
        //Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        Workdays workday=workdaysService.findUseridYearMonthDay(users.getId(), year, month, day);

        //その日にボタンを押したかどうかチェック
        //int buttone=workday.getbutton

        //buttone==0　ボタンは押されてない　1 出勤か定時出勤が押されてる　2　退勤か定時退勤が押されてる

        //定時よりも前の時間だった場合定時ボタンを表示
        if(dateTs.isBefore(dateSt)){
            model.addAttribute("gw", 1);
        }
        //タイムスタンプが定時退勤時間より後の場合定時退勤ボタンを表示
        else if(dateTs.isAfter(dateEt)){
            model.addAttribute("lw", 1);
        }


       

        return "record_work";
    }
    @PostMapping("/SaleceServlet")
    public String SaleceServlet(@RequestParam String inputvalue, String name, Model model,@ModelAttribute YearMonth yaerMonth,BeanRegularTime Brtime){	
		User users=(User)session.getAttribute("Data");
		
		int id=users.getId();
		System.out.println("before="+inputvalue);
		
		//0900のような:なしの入力がされた場合に間に:を挿入する
		if(inputvalue.length()==4){
			StringBuilder sb = new StringBuilder();
            sb.append(inputvalue);
			sb.insert(2, ":");
			inputvalue=sb.toString();  

		}
		System.out.println("after="+inputvalue);

        //個別定時の有無を確認
		Idregulartime idtime=iRService.findId(users.getId());

		
		switch (name) {
            
			case "start":
            //個別定時テーブルが空だった場合　全体定時テーブルから定時情報を取得
		    if(idtime==null){
                RegularTime rt=rtService.findId(1);
			    //定時情報をbeanRegularTimeに移植
			    BeanRegularTime br=rtService.brtime(rt);

                Idregulartime idtime2=new Idregulartime();
			    idtime2.setStart(inputvalue);
			    idtime2.setEnd(br.getEnd());
			    idtime2.setHalftime(br.getHalftime());
			    idtime2.setUserid(id);
			    iRService.insert(idtime2);
		    }
		    //個別定時テーブルにデータがあった場合 個別定時テーブルから定時情報を取得し入力値を入れる
		    else{
			    idtime.setStart(inputvalue);
			    iRService.update(idtime);
		    }
            
				
			break;
			
			case "end":
                //個別定時テーブルが空だった場合　全体定時テーブルから定時情報を取得
		        if(idtime==null){
                    RegularTime rt=rtService.findId(1);
			        //定時情報をbeanRegularTimeに移植
			        BeanRegularTime br=rtService.brtime(rt);

                    Idregulartime idtime2=new Idregulartime();
			        idtime2.setStart(br.getStart());
			        idtime2.setEnd(inputvalue);
			        idtime2.setHalftime(br.getHalftime());
			        idtime2.setUserid(id);
			        iRService.insert(idtime2);
		        }
		        //個別定時テーブルにデータがあった場合 個別定時テーブルから定時情報を取得し入力値を入れる
		        else{
			        idtime.setEnd(inputvalue);
			        iRService.update(idtime);
		        }
            
			
			break;

			case "halftime":
		
			
			break;
		} 

		
        
		return "record_work";
		
	}

    //セールスフォースログイン用 定時退勤 ローカル
	@GetMapping("/localin")
	public String sale(){
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, localdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		
		
		//Chromeドライバーのインスタンス
       
		WebDriver driver = new ChromeDriver();//local
		System.out.println("①");
        //暗黙的な待機の設定（ブラウザ操作時の要素を見つけるまで最大5秒待つ）
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        try {
            //ログイン画面にアクセス
            driver.get("https://mki.my.salesforce.com/");
			System.out.println("②＝"+driver.getCurrentUrl());
			//driver.get("http://localhost:8080");
            //テキストボックス（出発）に「東京」と入力
            //driver.findElement(By.id("username")).sendKeys(login.getEmail());//ryowhite-yn9b@force.com

            //テキストボックス（出発）に「東京」と入力
            //driver.findElement(By.id("password")).sendKeys(login.getPassword()) ;//ryo13160

            //検索ボタンを押下
            driver.findElement(By.xpath("//*[@id='idp_section_buttons']/button")).click();
			//*[@id='idp_section_buttons']/button
			//String year=driver.findElement(By.id("vp")).getAttribute("name");
			//ログインが成功して勤怠データに遷移しているのならここでyearがログに出るはず
			//System.out.println("vp="+year);

			/*driver.findElement(By.id("s")).sendKeys("00:00") ;
			driver.findElement(By.id("e")).sendKeys("17:00") ;
			driver.findElement(By.id("h")).sendKeys("01:00") ;
			driver.findElement(By.id("ontime")).click();*/

			//driver.findElement(By.className("button mb24 secondary wide")).click();
			System.out.println("③＝"+driver.getCurrentUrl());

			//定時出勤ボタンを押下
			//driver.findElement(By.id("btnTstInput")).click();
			

			//定時退勤ボタンを押下
			driver.findElement(By.id("btnTetInput")).click();
			System.out.println("④＝"+driver.getCurrentUrl());

        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }



		return "done2";
	}

    //セールスフォースログイン用 定時出勤 ローカル
    @GetMapping("/localout")
	public String sale2(){
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, localdriver);

		System.setProperty("webdriver.chrome.whitelistedIps", "");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		
		
		//Chromeドライバーのインスタンス
		WebDriver driver = new ChromeDriver();//local
		System.out.println("①");
        //暗黙的な待機の設定（ブラウザ操作時の要素を見つけるまで最大5秒待つ）
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        try {
            //ログイン画面にアクセス
            driver.get("https://mki.my.salesforce.com/");
			System.out.println("②＝"+driver.getCurrentUrl());
			
            //検索ボタンを押下
            driver.findElement(By.xpath("//*[@id='idp_section_buttons']/button")).click();
			
			//driver.findElement(By.className("button mb24 secondary wide")).click();
			System.out.println("③＝"+driver.getCurrentUrl());

			//定時出勤ボタンを押下
			driver.findElement(By.id("btnTstInput")).click();
			

        } catch(Exception e) {
            System.out.println(e.getMessage());

        } finally {
			driver.quit();
        }



		return "done2";
	}



    

}
    