package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Holiday;
import com.example.demo.repository.HolidayRepository;
@Service
public class HolidayService
{
    @Autowired
    HolidayRepository holidayRepository;

   
    public List <Holiday> findyearmonth(int year, int month)
    {
    	return holidayRepository.findByYearAndMonth(year,month);
    }

    public List<Holiday> findyear(int year){
        return holidayRepository.findByYear(year);
    }
    public void delete(int id){
        Holiday holiday=holidayRepository.findById(id);
        holidayRepository.delete(holiday);
    }

    public Holiday findId(int id){
        return holidayRepository.findById(id);
    }

    public void update(Holiday holiday){
        holidayRepository.save(holiday);
    }
    public void insert(Holiday holiday){
        holidayRepository.save(holiday);
    }
}