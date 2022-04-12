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
import com.example.demo.model.RegularTime;
import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.Holiday;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingData;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
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
	@Autowired
    RegularTimeService rtService;

    public List <Workdays> findId(int id){
    	return workdaysRepository.findByUserid(id);
    }

    public List <Workdays> findYearMonth(int id,int year,int month){
    	return workdaysRepository.findByUseridAndYearAndMonth(id,year,month);
    }

    public  Workdays findUseridYearMonthDay(int userid,int year,int month,int day){
    	return workdaysRepository.findByUseridAndYearAndMonthAndDay(userid,year,month,day);
    }

    public  int insertdata(int userid,int year,int month,int day, String weekday){
    	return workdaysRepository.InsertData(userid,year,month,day,weekday);
    }
    public void update(Workdays workdays){
        workdaysRepository.save(workdays);
    }

	public List <Workdays> findHoliday(String holiday){
    	return workdaysRepository.findByHoliday(holiday);
    }

	public List <Workdays> findWeekday(String weekday){
    	return workdaysRepository.findByWeekday(weekday);
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
			//祝日の場合はテーブルの祝日カラムに1を挿入する+休憩時間を00:00:00にする
			List<Holiday> holidays=holidayService.findyearmonth(year, month);
			for(Holiday holiday:holidays) {
				int holiday2=holiday.getDay();
				workdays =findUseridYearMonthDay(userid,year, month,holiday2);
				workdays.setHoliday(1);
				workdays.setHalftime(userService.toTime("00:00"));
				update(workdays);
			}

			List<Workdays>workdaysList=findWeekday("土");
			for(Workdays workday:workdaysList){
				workday.setHalftime(userService.toTime("00:00"));
				update(workday);

			}

			workdaysList=findWeekday("日");
			for(Workdays workday:workdaysList){
				workday.setHalftime(userService.toTime("00:00"));
				update(workday);

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
		workingListParam.setMonth(month);
		workingListParam.setYear(year);
        return workingListParam;

	}

	//指定された年月の時間項目（開始休憩終了総合）に定時を入れる
	public void insertOntime(BeanRegularTime Brtime){
		YearMonth yearMonth=(YearMonth)session.getAttribute("yearMonth");
		User user=(User)session.getAttribute("Data");
		List<Workdays> workdayslist =findYearMonth(user.getId(),yearMonth.getYear(),yearMonth.getMonth());
		
		//送信された定時データが空だった場合→DBにある定時テーブルの値が埋め込まれる
		if(Brtime.getStart().equals("")||Brtime.getEnd().equals("")||Brtime.getHalftime().equals("")){
			RegularTime rTime=rtService.findId(1);
			for(Workdays workday : workdayslist) {

				if(workday.getWeekday().equals("土")||workday.getWeekday().equals("日")||workday.getHoliday()==1){

				}
				else{
					workday.setStart(rTime.getStart());
					workday.setEnd(rTime.getEnd());
					workday.setHalftime(rTime.getHalftime());
					workday.setWorktime(rTime.getWorktime());
					update(workday);
				}
			}
		}
		//送信された定時データが空でなかった場合→そのデータが埋め込まれる
		else{
			for(Workdays workday : workdayslist) {
				if(workday.getWeekday().equals("土")||workday.getWeekday().equals("日")||workday.getHoliday()==1){

				}
				else{
					workday.setStart(userService.toTime(Brtime.getStart()));
					workday.setEnd(userService.toTime(Brtime.getEnd()));
					workday.setHalftime(userService.toTime(Brtime.getHalftime()));
					String worktime=InquireWorkTime(Brtime);
					System.out.println(worktime);
					workday.setWorktime(userService.toTime(worktime));
					update(workday);
				}
			}
		}
	}

	//開始終了休憩時刻（String）を含んだBeanRegularTimeから労働時間（Sting？）を割り出す
	public String InquireWorkTime(BeanRegularTime Brtime){
		//開始終了休憩時間をそれぞれ分単位にして引き算する（終了-開始-休憩＝労働時間）
		int sminutes=userService.allminutes(Brtime.getStart());
		int hminutes=userService.allminutes(Brtime.getHalftime());
		int eminutes=userService.allminutes(Brtime.getEnd());
		int wminutes=eminutes-hminutes-sminutes;
		//分になった労働時間を00:00形式に変換
		String worktime=userService.wminutes(wminutes);
		
		return worktime;
	}
    //ユーザーから備考が使われているかの確認
    public List<Otherpa> oplist(User user, int companyid){
        CellvalueGet cellgetvalue=new CellvalueGet();
		
		//ユーザーが所属する企業を引数として渡し
		//その企業の名前がファイル名になっているテンプレートにおいて備考1,2,3(oa,ob,oc)が使用されているかを判定する
		//例えば会社1において備考1,2が使用されていた場合はjudgeued.oa=1,ob=1,oc=0になる
		List<Otherpa>opList=new ArrayList<Otherpa>();
		Judgeused judgeused1=cellgetvalue.GetCellvalue(companyid, user.getCompany1());
		Judgeused judgeused2=cellgetvalue.GetCellvalue(companyid, user.getCompany2());
		Judgeused judgeused3=cellgetvalue.GetCellvalue(companyid, user.getCompany3());
		
		//会社1,2,3,においてoaが使用されているかどうかのクラスを作る
		
		Otherpa opa_oa=new Otherpa();
		opa_oa.setCompany1(judgeused1.getOa());
		opa_oa.setCompany2(judgeused2.getOa());
		opa_oa.setCompany3(judgeused3.getOa());

		opList.add(opa_oa);
		
		//会社1,2,3,においてobが使用されているかどうかのクラスを作る
		Otherpa opa_ob=new Otherpa();
		opa_ob.setCompany1(judgeused1.getOb());
		opa_ob.setCompany2(judgeused2.getOb());
		opa_ob.setCompany3(judgeused3.getOb());
		opList.add(opa_ob);

		//会社1,2,3,においてocが使用されているかどうかのクラスを作る
		Otherpa opa_oc=new Otherpa();
		opa_oc.setCompany1(judgeused1.getOc());
		opa_oc.setCompany2(judgeused2.getOc());
		opa_oc.setCompany3(judgeused3.getOc());
		opList.add(opa_oc);

		//opListは
		//oaが会社1,2,3で使われているかどうか分かるopa_oa
		//oaが会社1,2,3で使われているかどうか分かるopa_ob
		//oaが会社1,2,3で使われているかどうか分かるopa_oc
		//から成るListである
		return opList;
    }
		
		
}