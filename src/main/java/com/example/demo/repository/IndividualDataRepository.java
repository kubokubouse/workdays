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
	
	@Query(value ="update individual_data set mail=?1 where companyID=?2 and mail=?3 ",
			nativeQuery = true)
	
	//1から順に変更後のmailの値、どのデータを変更するか特定するための会社IDとメールアドレスが入る感じ　
	void UpdateIndividualEmail(String mail,int company_id,String beforemail);
	
	IndividualData findById(int id);
	IndividualData findByMailAndCompanyID(String mail,int companyID);
	List<IndividualData> findByCompanyID(int companyID);
	List<IndividualData> findByCompanyIDAndRegistered(int companyID,int registered);
	List<IndividualData> findByMail(String mail);
}