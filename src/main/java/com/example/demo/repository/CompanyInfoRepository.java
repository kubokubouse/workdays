package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CompanyInfo;
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Integer> {
	CompanyInfo findById(int id);
}