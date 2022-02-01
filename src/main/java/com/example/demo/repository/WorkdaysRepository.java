package com.example.demo.repository;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Workdays;

public interface WorkdaysRepository extends JpaRepository<Workdays, Long>
{	@Transactional
	@Modifying
	@Query(value ="insert into workdays(userid,year,month,day,start,end,halftime,worktime,other,weekday) values(?1,?2,?3,?4,'00:00:00','00:00:00','00:00:00','00:00:00','',?5) ",

	        nativeQuery = true)
	//1から順にuserid,year,month,day,id,weekdayが入る感じ　
		int InsertData(int userid,int year,int month,int day,String weekday);

	    List<Workdays> findByUserid(int userid);

	    List<Workdays> findByUseridAndYearAndMonth(int userid,int year, int month);

	    Workdays findByUseridAndYearAndMonthAndDay(int userid,int year, int month,int Day);
}

