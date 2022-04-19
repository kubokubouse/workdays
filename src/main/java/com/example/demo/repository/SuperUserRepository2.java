package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.model.SuperUser;

public interface SuperUserRepository2 extends JpaRepository<SuperUser, String> {
	@Transactional
	@Modifying
	@Query(value ="update individual_data set email=?1 where id=?2  " ,

	        nativeQuery = true)
    int updateEmail(String email,int id);
    
    @Transactional
	@Modifying
	@Query(value ="update individual_data set copmanyID=?1 where id=?2  " ,

	        nativeQuery = true)
    int updateCompanyID(int companyID,int id);
    

	SuperUser findByEmailAndCompanyID(String Email,int companyID);
    SuperUser findById(int Id);
    SuperUser findByEmail(String email);
    List<SuperUser> findByCompanyID(int companyID);
}
