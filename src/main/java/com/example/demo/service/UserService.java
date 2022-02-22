package com.example.demo.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Holiday;
import com.example.demo.model.MasterUser;
import com.example.demo.repository.MasterUserRepository;
import com.example.demo.model.StringList;
import com.example.demo.model.StringListParam;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.User;
import com.example.demo.model.UserData;
import com.example.demo.model.UserListParam;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingData;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.repository.SuperUserRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkdaysRepository;
@Service
@Transactional


public class UserService{
    @Autowired
    UserRepository userRepository;
	@Autowired
    SuperUserRepository suserRepository;
    @Autowired
    WorkdaysRepository workdaysRepository;
    @Autowired
    HttpSession session;

    @Autowired
    WorkdaysService workdaysService;

    @Autowired
    HolidayService holidayService;

	@Autowired
	MasterUserRepository masterRepository;

    @Value("${worklist.path}")
    private String path;

	
    //ログイン処理
    public User findEmailPassword(String emil, String password){
    	return userRepository.findByEmailAndPassword(emil,password);
	}

	//管理者用ログイン処理
	public SuperUserLogin findIdAndPass(String id, String pass) {
		return suserRepository.findByIdAndPass(id, pass);
	}
	//マスターユーザーログイン処理
	public MasterUser findIdPass(String id, String password) {
		return masterRepository.findByIdAndPassword(id, password);
	}

    public void insert(User user){
        userRepository.save(user);
    }

    public void update(User user){
        userRepository.save(user);
    }
	public User updateUser(User user){
        return userRepository.save(user);
    }

	public void delete(int id){
		User user=userRepository.findById(id);
        userRepository.delete(user);
		List<Workdays> workList = workdaysRepository.findByUserid(id);
		for(Workdays wd : workList) {
			workdaysRepository.delete(wd);
		}
    }
	public User findId(int id){
		return userRepository.findById(id);
	}

	public User findEmail(String Email){
		return userRepository.findByEmail(Email);
	}
	
	//画面一覧を表示する	
    public WorkingListParam searchAll(User users) {
    	
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		month=month+1;
		
		List<Workdays> workdayslist =workdaysService.findYearMonth(users.getId(),year,month);
		WorkingListParam workingListParam = new WorkingListParam();
        List<WorkingData> list = new ArrayList<WorkingData>();

        for(Workdays workday : workdayslist) {
          WorkingData data = new WorkingData();
          data.setDay(workday.getDay());
          data.setWeekday(workday.getWeekday());
          data.setStart(LocalTime.of(workday.getStart().getHours(),workday.getStart().getMinutes()));
          data.setEnd(LocalTime.of(workday.getEnd().getHours(),workday.getEnd().getMinutes()));
          data.setHalftime(LocalTime.of(workday.getHalftime().getHours(),workday.getHalftime().getMinutes()));
          data.setWorktime(LocalTime.of(workday.getWorktime().getHours(),workday.getWorktime().getMinutes()));
          data.setOther1(workday.getOther1());
		  data.setOther2(workday.getOther2());
		  data.setOther3(workday.getOther3());
		  data.setHoliday(workday.getHoliday());
          list.add(data);
        }
        workingListParam.setWorkingDataList(list);
        return workingListParam;
    }

    public StringListParam reactsearchAll(User users) {
		YearMonth yearMonth=nowYearMonth();
		List<Workdays> workdayslist =workdaysService.findYearMonth(users.getId(),yearMonth.getYear(),yearMonth.getMonth());
		StringListParam stringListParam = new StringListParam();
        List<StringList> list = new ArrayList<StringList>();
        for(Workdays workday : workdayslist) {
          StringList stringList = new StringList();
          stringList.setDay(Integer.toString(workday.getDay()));
          stringList.setWeekday(workday.getWeekday());
          stringList.setStart(workday.getStart().toString().substring(0,5));
          stringList.setEnd(workday.getEnd().toString().substring(0,5));
          stringList.setHalftime(workday.getHalftime().toString().substring(0,5));
          stringList.setWorktime(workday.getWorktime().toString().substring(0,5));
          stringList.setOther1(workday.getOther1());
		  stringList.setOther2(workday.getOther2());
		  stringList.setOther3(workday.getOther3());

          list.add(stringList);
        }
        stringListParam.setStringList2(list);
        return stringListParam;
    }

	public StringListParam searchAll3() {
		User users=(User)session.getAttribute("Data");
		YearMonth yearMonth=nowYearMonth();
		List<Workdays> workdayslist =workdaysService.findYearMonth(users.getId(),yearMonth.getYear(),yearMonth.getMonth());
		StringListParam stringListParam = new StringListParam();
        List<StringList> list = new ArrayList<StringList>();
        for(Workdays workday : workdayslist) {
          StringList stringList = new StringList();
          stringList.setDay(Integer.toString(workday.getDay()));
          stringList.setWeekday(workday.getWeekday());
          stringList.setStart(workday.getStart().toString());
          stringList.setEnd(workday.getEnd().toString());
          stringList.setHalftime(workday.getHalftime().toString());
          stringList.setWorktime(workday.getWorktime().toString());
          stringList.setOther1(workday.getOther1());
		  stringList.setOther2(workday.getOther2());
		  stringList.setOther3(workday.getOther3());
          list.add(stringList);
        }
        stringListParam.setStringList2(list);
        return stringListParam;
    }
    
	//DBの全てのユーザーをuserListParamに埋め込む
	public UserListParam searchAllUser() {
		List<User>userList=userRepository.findAll();
		UserListParam userListParam=new UserListParam();
		List<UserData> userDataList= new ArrayList<UserData>();
		for(User user:userList){
			UserData userData=new UserData();
			userData.setId(user.getId());
			userData.setFirstname(user.getFirstname());
			userData.setLastname(user.getLastname());
			userData.setEmail(user.getEmail());
			userData.setPassword(user.getPassword());
			userData.setCompany1(user.getCompany1());
			userData.setCompany2(user.getCompany2());
			userData.setCompany3(user.getCompany3());
			userData.setBanned(user.getBanned());
			userDataList.add(userData);
		}
		userListParam.setUserDataList(userDataList);
		return userListParam;
	}


	
    public void updateAll(WorkingListParam param) {
        List<Workdays> userList = new ArrayList<Workdays>();
		User users=(User)session.getAttribute("Data");
		int i=users.getId();
        // 画面パラメータをエンティティに詰め替える
        for (WorkingData data : param.getWorkingDataList()) {
        	int d=data.getDay();
        	Calendar cal = Calendar.getInstance();
    		int year=cal.get(Calendar.YEAR);
    		int month=cal.get(Calendar.MONTH);
    		month=month+1;
          //変更するのはどのユーザーか特定する
        	Workdays workdays= workdaysService.findUseridYearMonthDay(i,year,month,d);

            workdays.setStart(new Time( data.getStart().getHour(),data.getStart().getMinute(),data.getStart().getSecond()));
            workdays.setEnd(new Time( data.getEnd().getHour(),data.getEnd().getMinute(),data.getEnd().getSecond()));
            workdays.setHalftime(new Time( data.getHalftime().getHour(),data.getHalftime().getMinute(),data.getHalftime().getSecond()));
            workdays.setWorktime(new Time( data.getHalftime().getHour(),data.getHalftime().getMinute(),data.getHalftime().getSecond()));
            workdays.setOther1(data.getOther1());
			workdays.setOther2(data.getOther2());
			workdays.setOther3(data.getOther3());
        }
        workdaysRepository.saveAll(userList);
    }
	
	//現在の年月を出すメソッド 
	public YearMonth nowYearMonth(){
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int lastmonth=cal.get(Calendar.MONTH);
		int month=lastmonth+1; //カレンダーメソッドで取得した月は実際の月-1される（12月だったら11になる的な）ので+1して戻す
		YearMonth  yearMonth=new YearMonth();
		yearMonth.setMonth(month);
		yearMonth.setYear(year);
		yearMonth.setLastmonth(lastmonth);
		return yearMonth;
	}
  
	//名前と苗字とユーザーidからExcelを作る
    public void toExcel(String lastname,String firstname, int id) throws EncryptedDocumentException, IOException{
		//苗字と名前合わせてname
		String name=lastname+firstname;
        //当日の年月取得
	    YearMonth yearMonth=nowYearMonth();
	    User users=(User)session.getAttribute("Data");
	    int year=yearMonth.getYear();
	    int month=yearMonth.getMonth();
	    List<Workdays> workdays =workdaysService.findYearMonth(users.getId(),year,month);
		String fileName = "勤怠表_"+lastname+"_"+year+"年"+month+"月.xlsx";//aws.ver　ここに書き込まれる
	    //String filename="//LS520De8d/Public/"+lastname+"/勤怠表_"+year+"年"+month+"月.xlsx";
		//ここから既存のファイル消去パート
		Path p0= Paths.get(fileName);
		if(p0!=null) {
			try{
	      	 Files.delete(p0);
	    	}catch(IOException e){
	    	 System.out.println(e);
	    	}
	    }
		//ここから実験
	 	//String INPUT_DIR = "/apache/htdocs/image/test.xlsx";
		String INPUT_DIR = path+"test.xlsx";//aws用　ベースになるエクセルファイル
	 	Path p1 = Paths.get(INPUT_DIR);//ベース
	 	Path p2 = Paths.get(path+fileName);
		try{
			Files.copy(p1, p2);
		}catch(IOException e){
			e.printStackTrace();
		}
	 	
		FileInputStream in  = new FileInputStream(path+fileName);
	  //FileInputStream in  = new FileInputStream("//LS520De8d/Public/"+lastname+"/勤怠表_"+year+"年"+month+"月.xlsx");
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
        System.out.println(month);
        Row namerow = sheet.getRow(4);
        Cell namecell=namerow.getCell(3);
        namecell.setCellValue(name);
		int k=8;
		int tenhour=0;
		int hour=0;
		int minutes=0;
		
		for(Workdays workday:workdays){
			String[]e=
			{
				Integer.toString(workday.getDay()),workday.getWeekday(),workday.getStart().toString(),workday.getEnd().toString(),workday.getHalftime().toString(),workday.getWorktime().toString(),workday.getOther1(),
		    };
			
			Row row = sheet.getRow(k);
			System.out.println("k="+k);
			System.out.println("row="+row);
			k=k+1;//セル単位のループ
			for(int j=0;j<e.length;j++){	
				System.out.println("j;1="+j+1);
				Cell cell = row.getCell(j+1);
				//その行は何曜日かを明示
				String s=workday.getWeekday();
				//getstart・end・falftime・worktimeだったら→後ろ3つの文字を消す
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
					//一文字目は10時間、二文字目は1時間、3文字目は10分（10分単位でしか計算しない前提だけど実際どうなの）
					int first=Integer.parseInt(e[j].substring(0,1));
					int second=Integer.parseInt(e[j].substring(1,2));
					int third=Integer.parseInt(e[j].substring(3,4));
					tenhour=tenhour+first;
					hour=hour+second;
					minutes=minutes+third;
				}
				else {
					cell.setCellValue(e[j]);
				}
				//フォント設定
				if(e[j].equals("日")){
					Font font = wb.createFont();
					font.setColor(IndexedColors.RED.index);
					CellStyle cs = wb.createCellStyle();
					cs.setBorderBottom(BorderStyle.HAIR);
	    			cs.setFont(font);
	    			cell.setCellStyle(cs);
	    		}
				if(j==0&&s.equals("日")){
					Font font = wb.createFont();
					font.setColor(IndexedColors.RED.index);
					CellStyle cs = wb.createCellStyle();
					cs.setBorderLeft(BorderStyle.MEDIUM);
					cs.setBorderRight(BorderStyle.HAIR);
					cs.setBorderBottom(BorderStyle.HAIR);
	    			cs.setFont(font);
	    			cell.setCellStyle(cs);
				}
				
				else if(e[j].equals("月")||e[j].equals("火")||e[j].equals("水")||e[j].equals("木")||e[j].equals("金")){
					Font font = wb.createFont();
					font.setColor(IndexedColors.BLACK.index);
					CellStyle cs = wb.createCellStyle();
					cs.setBorderBottom(BorderStyle.HAIR);
					cs.setFont(font);
					cell.setCellStyle(cs);
	    		}
				
				else if(j==0&&s.equals("月")||j==0&&s.equals("火")||j==0&&s.equals("水")||j==0&&s.equals("木")||j==0&&s.equals("金")){
					Font font = wb.createFont();
					font.setColor(IndexedColors.BLACK.index);
					CellStyle cs = wb.createCellStyle();
					cs.setBorderLeft(BorderStyle.MEDIUM);
					cs.setBorderRight(BorderStyle.HAIR);
					cs.setBorderBottom(BorderStyle.HAIR);
		    		cs.setFont(font);
		    		cell.setCellStyle(cs);
				}
				
				else if(e[j].equals("土")){
					Font font = wb.createFont();
					font.setColor(IndexedColors.BLUE.index);
					CellStyle cs = wb.createCellStyle();
					cs.setBorderBottom(BorderStyle.HAIR);
	    			cs.setFont(font);
	    			cell.setCellStyle(cs);
	    		}

	    		else if(j==0&&s.equals("土")){
					Font font = wb.createFont();
					font.setColor(IndexedColors.BLUE.index);
					CellStyle cs = wb.createCellStyle();
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
				//日付と曜日を赤くするので申し訳程度のループ　i=1&2
				for(int i=1; i<3; i++){
					Cell cell2 = row2.getCell(i);
					Font font = wb.createFont();
					font.setColor(IndexedColors.RED.index);
					CellStyle cs = wb.createCellStyle();
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
		//労働時間の合計を出す　tenhour 10時間の合計　hour 1時間の合計 minutes 10分の合計
		hour=hour+tenhour*10;//10時間の合計＊10+1時間の合計で全部で何時間か分かる
		hour=hour+minutes/6;//10分の合計を6で割った数を1時間としてカウント
		minutes=minutes%6;//6で割った余りを10分としてカウント　1なら10分
		int tenminutes=minutes*10;
		String totalhour=	String.valueOf(hour);
		String totalminutes=	String.valueOf(tenminutes);
		if(tenminutes==0) {//6で割って余りがでない場合分数の表記が0になってしまうのでその時は00表記にする
			totalminutes="00";
		}
		String totalworktime=(totalhour+":"+totalminutes);
		Row row = sheet.getRow(3);
		Cell cell = row.getCell(9);
		cell.setCellValue(totalworktime);
		
		//年月を出す
		String yearmonth=	String.valueOf(year)+"年"+String.valueOf(month)+"月";
		Row row_ym = sheet.getRow(3);
		Cell cell_ym = row_ym.getCell(3);
		cell_ym.setCellValue(yearmonth);
		FileOutputStream out = null;
		try{
			// 変更するエクセルファイルを指定
			out = new FileOutputStream(path+fileName);
			// 書き込み
	    	wb.write(out);
	    	System.out.println("書き込みok");
		}catch(Exception e){
			e.printStackTrace();
 		} finally{
		 out.close();
		 wb.close();
		}
	}
}