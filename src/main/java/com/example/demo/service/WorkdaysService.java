package com.example.demo.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Judgeused;
import com.example.demo.model.Otherpa;
import com.example.demo.model.Holiday;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingData;
import com.example.demo.model.WorkingListParam;
import com.example.demo.repository.WorkdaysRepository;

@Service
public class WorkdaysService {
    @Autowired
    WorkdaysRepository workdaysRepository;
    @Autowired
    HttpSession session;
    @Autowired
    HolidayService holidayService;
	@Autowired
    UserService userService;


    public List <Workdays> findId(int id){
    	return workdaysRepository.findByUserid(id);
    }

    public List <Workdays> findYearMonth(int id,int year,int month){
    	return workdaysRepository.findByUseridAndYearAndMonth(id,year,month);
    }

    public  Workdays findUseridYearMonthDay(int userid,int year,int month,int day){
    	return workdaysRepository.findByUseridAndYearAndMonthAndDay(userid,year,month,day);
    }

    public  int insertdata(int userid,int year,int month,int day,String weekday){
    	return workdaysRepository.InsertData(userid,year,month,day,weekday);
    }
    public void update(Workdays workdays){
        workdaysRepository.save(workdays);
    }
	
	//指定された年月が既に存在するかどうかの確認＋なかったらデータを追加
	public void checkYearMonth(int year,int month){
		User user=(User)session.getAttribute("Data");
		int userid=user.getId();
		Workdays workdays =findUseridYearMonthDay(userid,year, month,1);
		Calendar cal = Calendar.getInstance();
		if (workdays==null){
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month-1);//カレンダーメソッドなので使うのは実際の月-1の方
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
			for(int i=0;i<lastDayOfMonth;i++){
				String yobi[] = {"日","月","火","水","木","金","土"};
				cal.set(year,month-1, i+1);
				String weekday=yobi[cal.get(Calendar.DAY_OF_WEEK)-1];
				insertdata(userid, year, month, i+1, weekday);//int d=が左辺にあったから動かなくなったら足すこと
			}
			List<Holiday> holidays=holidayService.findyearmonth(year, month);
			for(Holiday holiday:holidays) {
				int holiday2=holiday.getDay();
				workdays =findUseridYearMonthDay(userid,year, month,holiday2);
				workdays.setHoliday(1);
				update(workdays);
			}
        }
	}

    //年月日入れたら勤怠データをDBからリスト形式で取得
    public WorkingListParam date(int year,int month){
		User user=(User)session.getAttribute("Data");
	
		List<Workdays> workdayslist =findYearMonth(user.getId(),year,month);
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
		workingListParam.setMonth(year);
		workingListParam.setYear(month);
        return workingListParam;

	}

	//指定された年月の時間項目（開始休憩終了総合）に定時を入れる
	public void insertOntime(int year,int month){
		User user=(User)session.getAttribute("Data");
		List<Workdays> workdayslist =findYearMonth(user.getId(),year,month);
		//テスト:規定された定時データを勤怠データに突っ込む
		for(Workdays workday : workdayslist) {
			if(workday.getWeekday().equals("土")||workday.getWeekday().equals("日")||workday.getHoliday()==1){
			}
			else{
				workday.setStart(userService.toTime("09:00"));
				workday.setEnd(userService.toTime("18:00"));
				workday.setHalftime(userService.toTime("01:00"));
				workday.setWorktime(userService.toTime("08:00"));
				update(workday);
			}
		}
	}


    //ユーザーから備考が使われているかの確認
    public List<Otherpa> oplist(User user){
        CellvalueGet cellgetvalue=new CellvalueGet();
		
		List<Otherpa>opList=new ArrayList<Otherpa>();
		Judgeused judgeused1=cellgetvalue.GetCellvalue(user.getCompany1());
		Judgeused judgeused2=cellgetvalue.GetCellvalue(user.getCompany2());
		Judgeused judgeused3=cellgetvalue.GetCellvalue(user.getCompany3());
		
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

		return opList;
    }
		
		
		
}