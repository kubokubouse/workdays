package com.example.demo.service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Holiday;
import com.example.demo.model.IndividualData;
import com.example.demo.model.MasterUser;
import com.example.demo.model.Onetime;
import com.example.demo.repository.IndividualDataRepository;
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

	@Autowired
	IndividualDataRepository iDataRepository;

    //@Value("${worklist.path}")
    private String path;

	
    //ログイン処理
    public User findEmailPassword(String emil, String password){
    	return userRepository.findByEmailAndPassword(emil,password);
	}

	//管理者用ログイン処理
	public SuperUserLogin findEmailAndPass(String email, String pass) {
		return suserRepository.findByEmailAndPass(email, pass);
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

	public IndividualData updateIndividualData(IndividualData id) {
		return iDataRepository.save(id);
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
		//yearMonth.setLastmonth(lastmonth);
		return yearMonth;
	}

	//00：00形式のStringを合計oo分のintに変換する
	public int allminutes(String time){
		int tenhour=Integer.parseInt(time.substring(0, 1));
		int hour=Integer.parseInt(time.substring(1, 2));
		int tenminutes=Integer.parseInt(time.substring(3, 4));
		int minutes=Integer.parseInt(time.substring(4, 5));
		int aminutes=(tenhour*10+hour)*60+tenminutes*10+minutes;
		return aminutes;
		
	}
	//intの分をStringの00:00形式に変換する
	public String wminutes(int aminutes){
		int tenhour=aminutes/600;
		int hour=aminutes%600/60;
		int tenminutes=aminutes%600%60/10;
		int minutes=aminutes%600%60%10%10;
		String time=Integer.toString(tenhour)+Integer.toString(hour)+":"+Integer.toString(tenminutes)+Integer.toString(minutes);
		return time;

	}

	public Time toTime(String inputvalue){
		LocalTime lt=LocalTime.parse(inputvalue);
		Time time=new Time( lt.getHour(),lt.getMinute(),lt.getSecond());
		return time;
	}

	//User型userを引数にしてontimeを返す
	public Onetime inOnetime(User user){
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
		return onetime;
	}
	

	//個別ユーザーテーブルから全データを取得
	public List<IndividualData> searchAllIndividualData() {
		List<IndividualData> idList = iDataRepository.findAll();
		return idList;
	}
	//個別ユーザーテーブルからその会社の全データを取得
	public List<IndividualData> findCompanyID(int companyID) {
		
		return iDataRepository.findByCompanyID(companyID);
	}

	//個別ユーザーテーブルから削除
	public void deleteIndividualData (IndividualData id) {
		iDataRepository.delete(id);
	}

	//個別ユーザーテーブルから個人ID取得
	public String findIndividualID(String mail, int companyId) {
		IndividualData id = iDataRepository.findByMailAndCompanyID(mail,companyId);
		return id.getNumber();
	}

	//個別ユーザーテーブルから選択データ削除
	public void deleteIndividualData(String mail){
		int companyId=(int)session.getAttribute("companyId");
		IndividualData id = iDataRepository.findByMailAndCompanyID(mail,companyId);
		iDataRepository.delete(id);
    }

	//個別ユーザーテーブルにデータ登録
	public IndividualData InsertIndividualData(IndividualData id){
		return iDataRepository.saveAndFlush(id);
	}
}