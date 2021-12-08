package com.example.demo.service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingData;
import com.example.demo.model.WorkingListParam;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkdaysRepository;

@Service
@Transactional
public class UserService
{
    @Autowired
    UserRepository userRepository;
    @Autowired
    WorkdaysRepository workdaysRepository;
    @Autowired
    HttpSession session;

    @Autowired
    WorkdaysService workdaysService;

    //ポイント①
    public User findEmailPassword(String emil, String password)
    {
    	return userRepository.findByEmailAndPassword(emil,password);
    }

    /*public List<User> findAll() {
        return UserRepository.findAllOrderById();
    }*/

    public void insert(User user)
    {
        userRepository.save(user);
    }

    public void update(User user)
    {
        userRepository.save(user);
    }

    public User updateUser(User user)
    {
        return userRepository.save(user);
    }


    //画面一覧を表示する
    public WorkingListParam searchAll() {
    	User users=(User)session.getAttribute("Data");
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		month=month+1;
		String year2 = String.valueOf(year);
		String month2 = String.valueOf(month);
		List<Workdays> workdayslist =workdaysService.findYearMonth(users.getId(),year2,month2);
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
          data.setOther(workday.getOther());

          list.add(data);
        }
        workingListParam.setWorkingDataList(list);
        return workingListParam;
      }

 /*   public WorkingListParam searchlastmonthAll() {
    	User users=(User)session.getAttribute("Data");
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		if(month==0) {
			month=month+1;
		}
		String year2 = String.valueOf(year);
		String month2 = String.valueOf(month);
		List<Workdays> workdayslist =workdaysService.findYearMonth(users.getId(),year2,month2);
		WorkingListParam workingListParam = new WorkingListParam();
        List<WorkingData> list = new ArrayList<WorkingData>();

        for(Workdays workday : workdayslist) {
          WorkingData data = new WorkingData();
          data.setDay(workday.getDay());
          data.setWeekday(workday.getWeekday());
          data.setStart(LocalTime.of(workday.getStart().getHours(),workday.getStart().getMinutes()));
          data.setEnd(workday.getEnd());
          data.setHalftime(workday.getHalftime());
          data.setWorktime(workday.getWorktime());
          data.setOther(workday.getOther());

          list.add(data);
        }
        workingListParam.setWorkingDataList(list);
        return workingListParam;
      }


    public WorkingListParam searchnextmonthAll() {
    	User users=(User)session.getAttribute("Data");
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		month=month+2;
		if(month==13) {
			month=1;
		}
		String year2 = String.valueOf(year);
		String month2 = String.valueOf(month);
		List<Workdays> workdayslist =workdaysService.findYearMonth(users.getId(),year2,month2);
		WorkingListParam workingListParam = new WorkingListParam();
        List<WorkingData> list = new ArrayList<WorkingData>();

        for(Workdays workday : workdayslist) {
          WorkingData data = new WorkingData();
          data.setDay(workday.getDay());
          data.setWeekday(workday.getWeekday());
          data.setStart(workday.getStart());
          data.setEnd(workday.getEnd());
          data.setHalftime(workday.getHalftime());
          data.setWorktime(workday.getWorktime());
          data.setOther(workday.getOther());

          list.add(data);
        }
        workingListParam.setWorkingDataList(list);
        return workingListParam;
      }
*/

      /**
       * ユーザー情報更新
       * @param param 画面パラメータ
       */

    public void updateAll(WorkingListParam param) {
        List<Workdays> userList = new ArrayList<Workdays>();
        // 画面パラメータをエンティティに詰め替える
        for (WorkingData data : param.getWorkingDataList()) {
        	String d=data.getDay();
        	Calendar cal = Calendar.getInstance();
    		int year=cal.get(Calendar.YEAR);
    		int month=cal.get(Calendar.MONTH);
    		//month=month+1; 11月の勤怠表送る暫定処置　終わったら消す
    		String year2 = String.valueOf(year);
    		String month2 = String.valueOf(month);
    		User users=(User)session.getAttribute("Data");
    		int i=users.getId();

          //変更するのはどのユーザーか特定する
        	Workdays workdays= workdaysService.findUseridYearMonthDay(i,year2,month2,d);

            workdays.setStart(new Time( data.getStart().getHour(),data.getStart().getMinute(),data.getStart().getSecond()));
            workdays.setEnd(new Time( data.getEnd().getHour(),data.getEnd().getMinute(),data.getEnd().getSecond()));
            workdays.setHalftime(new Time( data.getHalftime().getHour(),data.getHalftime().getMinute(),data.getHalftime().getSecond()));
            workdays.setWorktime(new Time( data.getHalftime().getHour(),data.getHalftime().getMinute(),data.getHalftime().getSecond()));
            workdays.setOther(data.getOther());
        }
        workdaysRepository.saveAll(userList);
      }

   /* public void updatebeforeAll(WorkingListParam param) {
        List<Workdays> userList = new ArrayList<Workdays>();
        // 画面パラメータをエンティティに詰め替える
        for (WorkingData data : param.getWorkingDataList()) {
        	String d=data.getDay();
        	Calendar cal = Calendar.getInstance();
    		int year=cal.get(Calendar.YEAR);
    		int month=cal.get(Calendar.MONTH);
    		if(month==0) {
    			month=month+1;
    		}
    		String year2 = String.valueOf(year);
    		String month2 = String.valueOf(month);
    		User users=(User)session.getAttribute("Data");
    		int i=users.getId();

          //変更するのはどのユーザーか特定する
        	Workdays workdays= workdaysService.findUseridYearMonthDay(i,year2,month2,d);

            workdays.setStart(data.getStart());
            workdays.setEnd(data.getEnd());
            workdays.setHalftime(data.getHalftime());
            workdays.setWorktime(data.getWorktime());
            workdays.setOther(data.getOther());
        }
        workdaysRepository.saveAll(userList);
      }
    public void updatenextAll(WorkingListParam param) {
        List<Workdays> userList = new ArrayList<Workdays>();
        // 画面パラメータをエンティティに詰め替える
        for (WorkingData data : param.getWorkingDataList()) {
        	String d=data.getDay();
        	Calendar cal = Calendar.getInstance();
    		int year=cal.get(Calendar.YEAR);
    		int month=cal.get(Calendar.MONTH);
    		month=month+1;
    		if(month==13) {
    			month=1;
    		}
    		String year2 = String.valueOf(year);
    		String month2 = String.valueOf(month);
    		User users=(User)session.getAttribute("Data");
    		int i=users.getId();

          //変更するのはどのユーザーか特定する
        	Workdays workdays= workdaysService.findUseridYearMonthDay(i,year2,month2,d);

            workdays.setStart(data.getStart());
            workdays.setEnd(data.getEnd());
            workdays.setHalftime(data.getHalftime());
            workdays.setWorktime(data.getWorktime());
            workdays.setOther(data.getOther());
        }
        workdaysRepository.saveAll(userList);
      }*/
}