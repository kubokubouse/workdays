package com.example.demo.controller;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalTime;
import javax.servlet.http.HttpSession;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Holiday;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.service.HolidayService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;
import com.example.demo.service.WorkdayMapping;
import com.google.gson.Gson;
@Controller
public class WorkController
{
	@Autowired
    HttpSession session;
    @Autowired
    UserService userService;
    @Autowired
    WorkdaysService workdaysService;
    @Autowired
    MailSendService mailsendService;
    @Autowired
    HolidayService holidayService;

    @Value("${worklist.path}")
    private String path;

	@PostMapping("/AjaxServlet")
	public String AjaxServlet(@RequestParam String invalue, String name,String day, Model model){	
		User users=(User)session.getAttribute("Data");
		YearMonth yearMonth=userService.nowYearMonth();
		int id=users.getId();
		int intday=Integer.parseInt(day);
		Workdays workdays= workdaysService.findUseridYearMonthDay(id,yearMonth.getYear(),yearMonth.getMonth(),intday);
		workdays.setOther(invalue);
		workdaysService.update(workdays);

		System.out.println(invalue);
		System.out.println(name);
		System.out.println(day);
		WorkingListParam workingListParam = userService.searchAll(users);
		model.addAttribute("workingListParam", workingListParam);
		return "list";
	}
	
	@PostMapping("/reactAjax")
	@CrossOrigin(origins = "*")
	@ResponseBody
	public String reactAjax(@RequestParam String email, String password,String other, String day,Model model){	
		User users=userService.findEmailPassword(email,password);
		YearMonth yearMonth=userService.nowYearMonth();//その時の年月取得
		int userid=users.getId();
		int year=yearMonth.getYear();
		int month=yearMonth.getMonth();
		int intday=Integer.parseInt(day);
		Workdays workdays= workdaysService.findUseridYearMonthDay(userid,year,month,intday);//id拾えば5は問題ない
		workdays.setOther(other);
		//仮置き 入力値に:00を足せば多分いける
		String timeother=other+":00";
		Time time=Time.valueOf(timeother);
		workdays.setStart(time);
		workdaysService.update(workdays);

		System.out.println(other);
		//searchAllと多分searchAll2もセッションのデータが必要だけどreactでAjaxするときはセッションデータが存在しないのでおかしくなってる説
		//とはいえ途中までは入力受け付けてね？
		//データのアップデートまではidなしでいいので起動はするのでうまく行く
		//↓のメソッドが事故ると値が真っ当に帰ってこない説
		
		WorkingListParam workingListParam = userService.searchAll(users);
		model.addAttribute("workingListParam", workingListParam);
		StringListParam stringListParam = userService.reactsearchAll(users);
		//StringListParam stringListParam = userService.searchAll3();
		Gson gson = new Gson();
	    String jstr = gson.toJson(stringListParam.getStringList2(),  List.class);
	    model.addAttribute("workingListParam", workingListParam);
	    return jstr;
	}


	@GetMapping("/workdays")
	//userIdが一致かつログインした時の年と月が一致するデータのみを取り出す
	public String workdays(Model model)
	{   User users=(User)session.getAttribute("Data");
		WorkingListParam workingListParam = userService.searchAll(users);
		workingListParam.setYear(2021);
		workingListParam.setMonth(11);

		model.addAttribute("workingListParam", workingListParam);
		return "list";
	}


	@RequestMapping(value = "/reactExcel")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public String reactmail(String password, String email)throws EncryptedDocumentException, IOException{
		User users=userService.findEmailPassword(email,password);
		String lastname=users.getLastname();
		String firstname=users.getFirstname();
		int id=users.getId();
		userService.toExcel(lastname,firstname,id);
		return "done";
	}

	@RequestMapping(value="/selectFile")
	public String select(){
		return "selectFile";
	}

	@RequestMapping(value="/property")
	public String ExcelwithProperty (@RequestParam String text, Model model) {

		String propertyfileName = text;
		
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
			totalHourMap.put("th"+index, workday.getWorktime().toString().substring(0,2));
			totalMinMap.put("tm"+index, workday.getWorktime().toString().substring(3,5));
			otherMap.put("ot"+index, workday.getOther());

			index++;

		}

		String inputFilePath = "C:/pleiades/workdays/workdays/src/main/resources/PropertyFiles/" + propertyfileName;
		String outputFilePath = "C:/pleiades/workdays/workdays/src/main/resources/OutputFiles/勤怠表_"+lastname+"_"+year+"年"+month+"月.xlsx";

		WorkdayMapping workdayMapping = new WorkdayMapping();
		workdayMapping.outputExcel(inputFilePath, outputFilePath, 
			stHourMap, stMinMap, endHourMap, endMinMap, lunchTimeHourMap, lunchTimeMinMap,
			 totalHourMap, totalMinMap, otherMap);


			return "done";

	}



	//Excelに書き込む用の処理
	//
	@GetMapping("/Excel")
	public String Excel( Model model)throws EncryptedDocumentException, IOException{

		User users=(User)session.getAttribute("Data");
		model.addAttribute("user",users);
		//苗字と名前合わせてname
		String lastname=users.getLastname();
		String firstname=users.getFirstname();
		String name=lastname+firstname;

		//当日の年月取得
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		month=month+1; //カレンダーメソッドで取得した月は実際の月-1される（12月だったら11になる的な）ので+1して戻す
	
		List<Workdays> workdays =workdaysService.findYearMonth(users.getId(),year,month);
		System.out.println("month="+month);
		//String fileName = "勤怠表_"+lastname+"_"+year+"年"+month+"月.xlsx";//aws.ver　ここに書き込まれる
		String fileName="//LS520De8d/Public/"+lastname+"/勤怠表_"+year+"年"+month+"月.xlsx";
		
		//既存のファイルを消去
		Path p0= Paths.get(fileName);
		if(p0!=null) {
			try{
		      Files.delete(p0);
		    }catch(IOException e){
		      System.out.println(e);
		    }
		}
		
		System.out.println("filename="+fileName);
		//ベースになるエクセルファイルを定義
		String INPUT_DIR = "/apache/htdocs/image/test.xlsx";
		//String INPUT_DIR = path+"test.xlsx";//aws用　ベースになるエクセルファイル
		Path p1 = Paths.get(INPUT_DIR);//ベース
		//Path p2 = Paths.get(path+fileName);
	    Path p2 = Paths.get(fileName);
		try{
			Files.copy(p1, p2);
		}catch(IOException e){
			e.printStackTrace();
		}
		//FileInputStream in  = new FileInputStream(path+fileName); aws用
		FileInputStream in  = new FileInputStream(fileName);
	    Workbook wb=null;
		try{
			// 既存のエクセルファイルを編集する際は、WorkbookFactoryを使用
		    wb = WorkbookFactory.create(in);
		    wb.setSheetName(0, month+"月")  ;
	    } catch (Exception e){
			e.printStackTrace();
	    }
	    
		//名前を所定のセルに記入
	    Sheet sheet = wb.getSheet(month+"月");
	    //System.out.println(month);
	    Row namerow = sheet.getRow(4);
	    Cell namecell=namerow.getCell(3);
	    namecell.setCellValue(name);
		
		int k=8;
		int tenhour=0;
		int hour=0;
		int tenminutes=0;
		int minutes=0;
		
		//ループを回してExcelに入力された値を入れ、フォントの設定をする
		//外のループは行のループで中のループはセルのループ
		//つまり具体的にどうループするかというと
		//日付が入力される→曜日開始終了休憩作業備考の順に入力
		//
		for(Workdays workday:workdays){
			String[]e={
				Integer.toString(workday.getDay()),workday.getWeekday(),workday.getStart().toString(),workday.getEnd().toString(),workday.getHalftime().toString(),workday.getWorktime().toString(),workday.getOther(),
			};
			Row row = sheet.getRow(k);//値を入れるセルの行を指定
			k=k+1;					 //行番号に1加えて次のループに備える

			//セル単位のループ　e.lengthは日付曜日開始終了休憩作業備考の7つなので7回処理を行うとその週のループが終わる
			for(int j=0;j<e.length;j++){
				Cell cell = row.getCell(j+1);
				//その行は何曜日かを明示
				String weekday=workday.getWeekday();

				//セルの値が開始(2)・終了(3)・休憩(4)・労働時間(5)だったら→後ろ3つの文字を消す
				//入力されたデータそのものは秒数が存在しない00:00形式の値だが
				//DBに登録されるのは
				if(j==2||j==3||j==4) {
					e[j]=e[j].substring(0,5);
					cell.setCellValue(e[j]);
				}

				//日付は数値として処理
				else if(j==0) {
					int dj=Integer.parseInt(e[j]);
					cell.setCellValue(dj);
				}

				//労働時間の合計時間を出す　労働時間をループで全部出して全部時間に変換して足し算する
				else if(j==5) {
					e[j]=e[j].substring(0,5);
					//セルに埋め込む値は前6文字切り出した時点で確定しておく
					cell.setCellValue(e[j]);
					
					//一文字目は10時間、二文字目は1時間、4文字目は10分、5文字目は1分
					//つまり10:35だったら前から順に1,0,3,5が抽出される
					//ループを重ねるごとにそれぞれの〇〇文字目の値を加算していき総労働時間を求めていくイメージ
					int first=Integer.parseInt(e[j].substring(0,1));
					int second=Integer.parseInt(e[j].substring(1,2));
					int third=Integer.parseInt(e[j].substring(3,4));
					int forth=Integer.parseInt(e[j].substring(4,5));
					tenhour=tenhour+first;
					hour=hour+second;
					tenminutes=tenminutes+third;
					minutes=minutes+forth;
				}
				
				else {
					cell.setCellValue(e[j]);
				}
				
				//フォント設定
				//全項目共通項
				Font font = wb.createFont();
				CellStyle cs = wb.createCellStyle();

				//ここからのif文6連打に関してザックリいうと
				//jは配列の中の何番手かを表す。j=0なら日付になる
				//weekday.equals(日)はあなたの所属する行は日曜ですってこと
				//e[j].equals("日")はあなたのセルの値は日ですってこと
				//ようはここでやってるのは曜日セルと日付セルに関して色を付けたりセルの枠の線の太さを規定してるってこと
				//Q色を変えるのが土曜と日曜だけなら残りの平日は特段セル弄らなくてよいのでは？
				//A ベースになってる勤怠表が2021年11月とかだから平日も真っ黒に塗り撫さないと
				//11月休日で当月が平日だった日の日付曜日が真っ赤になる
				//Qセル枠のフォント弄ってるのが日付と曜日のセルだけの理由は？全部弄るか全部弄らないかの二択でいいのでは
				//Aセルのフォントをどこか一つでも弄るとセルの枠が真っ白になるので文字を青・赤にした
				//曜日・日付
				if(e[j].equals("日")){//セルの値が「日」だったら
					font.setColor(IndexedColors.RED.index);
					cs.setBorderBottom(BorderStyle.HAIR);
		    		cs.setFont(font);
		    		cell.setCellStyle(cs);
		    	}
				if(j==0&&weekday.equals("日")){//セルが日付セルでありかつその行の曜日セルの値が「日」だったら
					font.setColor(IndexedColors.RED.index);
					cs.setBorderLeft(BorderStyle.MEDIUM);
					cs.setBorderRight(BorderStyle.HAIR);
					cs.setBorderBottom(BorderStyle.HAIR);
		    		cs.setFont(font);
		    		cell.setCellStyle(cs);
				}
				
				else if(e[j].equals("月")||e[j].equals("火")||e[j].equals("水")||e[j].equals("木")||e[j].equals("金")){
		    		font.setColor(IndexedColors.BLACK.index);
					cs.setBorderBottom(BorderStyle.HAIR);
			    	cs.setFont(font);
			    	cell.setCellStyle(cs);
		    	}

		    	else if(j==0&&weekday.equals("月")||j==0&&weekday.equals("火")||j==0&&weekday.equals("水")||j==0&&weekday.equals("木")||j==0&&weekday.equals("金")){
					font.setColor(IndexedColors.BLACK.index);
					cs.setBorderLeft(BorderStyle.MEDIUM);
					cs.setBorderRight(BorderStyle.HAIR);
					cs.setBorderBottom(BorderStyle.HAIR);
			    	cs.setFont(font);
			    	cell.setCellStyle(cs);
		    	}
					
		    	else if(e[j].equals("土")){
					font.setColor(IndexedColors.BLUE.index);
					cs.setBorderBottom(BorderStyle.HAIR);
		    		cs.setFont(font);
		    		cell.setCellStyle(cs);
		    	}
				else if(j==0&&weekday.equals("土")){
					font.setColor(IndexedColors.BLUE.index);
					cs.setBorderLeft(BorderStyle.MEDIUM);
					cs.setBorderRight(BorderStyle.HAIR);
					cs.setBorderBottom(BorderStyle.HAIR);
		    		cs.setFont(font);
		   			cell.setCellStyle(cs);
		    	}
			}

			//祝日分の書式設定
			List<Holiday> holidays=holidayService.findyearmonth(year, month);
			for(Holiday holiday:holidays) {
				int holiday2=holiday.getDay();
				holiday2=holiday2+7;
				Row row2 = sheet.getRow(holiday2);
				Font font = wb.createFont();
				font.setColor(IndexedColors.RED.index);
				//日付と曜日を赤くするので申し訳程度のループ　i=1&2
				for(int i=1; i<3; i++){
					Cell cell2 = row2.getCell(i);
					CellStyle cs = wb.createCellStyle();
					//日付の場合はセルの左枠を太線にしないとアカン
					if(i==1){
						cs.setBorderLeft(BorderStyle.MEDIUM);
						cs.setBorderRight(BorderStyle.HAIR);
					}
					cs.setBorderBottom(BorderStyle.HAIR);
		    		cs.setFont(font);
		    		cell2.setCellStyle(cs);
				}
			}
		}
	
		//労働時間の合計を出す　tenhour 10時間の合計　hour 1時間の合計 tenminutes 10分の合計 minutes 1分の合計
		tenminutes=tenminutes+minutes/10;
		hour=hour+tenminutes/6;
		tenhour=tenhour+hour/10;
		minutes=minutes%10;
		tenminutes=tenminutes%6;
		hour=hour%10;

		String totalhour=	String.valueOf(tenhour*10+hour);
		String totalminutes=	String.valueOf(tenminutes*10+minutes);
		String totalworktime=(totalhour+":"+totalminutes);
		Row row = sheet.getRow(3);
		Cell cell = row.getCell(9);
		cell.setCellValue(totalworktime);

		//年月を出して記入する
		String yearmonth=String.valueOf(year)+"年"+String.valueOf(month)+"月";
		Row row_ym = sheet.getRow(3);
		Cell cell_ym = row_ym.getCell(3);
		cell_ym.setCellValue(yearmonth);

	 	FileOutputStream out = null;
	 	try{
 	    	// 変更するエクセルファイルを指定
 	    	//out = new FileOutputStream(path+fileName);
			out = new FileOutputStream(fileName);
			// 書き込み
 	    	wb.write(out);
 	    	System.out.println("書き込みok");
	 	}catch(Exception e){
 			e.printStackTrace();
	 	} finally{
 		 out.close();
 		 wb.close();
		}
		return "done";
	}

}
