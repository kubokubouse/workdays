package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.example.demo.model.SuperUser;

public interface SuperUserRepository2 extends JpaRepository<SuperUser, String> {
	
	SuperUser findByEmailAndCompanyID(String Email,int companyID);
    SuperUser findById(int Id);
    SuperUser findByEmail(String email);
    List<SuperUser> findByCompanyID(int companyID);
}
