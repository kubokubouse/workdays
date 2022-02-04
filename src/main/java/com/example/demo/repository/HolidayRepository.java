package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, String>
	{

		//@Query(value = "SELECT *from personaldata where email ="+"'"+ "ryowhite@icloud.com"+"'"+ "and password="+"'"+"1211"+"'", nativeQuery = true)
		List<Holiday> findByYearAndMonth(int year,int month);





}
