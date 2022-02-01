package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Workdays;
import com.example.demo.repository.WorkdaysRepository;

@Service
public class WorkdaysService {
    @Autowired
    WorkdaysRepository workdaysRepository;

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
}