package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

import com.example.demo.model.IndividualData;
public interface IndividualDataRepository extends JpaRepository<IndividualData, Integer> {
	@Transactional
	@Modifying
	@Query(value ="insert into individual_data(companyID,mail,individual_id,name,banned,registered,company1,company2,company3) values(?1,?2,?3,?4,?5,?6,?7,?8,?9) ",
			nativeQuery = true)
	
	//1から順にuserid,year,month,day,id,weekdayが入る感じ　
	int InsertIndividualData(int companyID,String mail,int individual_id,String name,int banned,int registered, String company1,String company2,String company3);
	
	IndividualData findById(int id);
	IndividualData findByMailAndCompanyID(String mail,int companyID);
	List<IndividualData> findByCompanyID(int companyID);
	List<IndividualData> findByMail(String mail);
}