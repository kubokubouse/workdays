package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.demo.model.CompanyInfo;
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Integer> {
	CompanyInfo findById(int id);
	List<CompanyInfo> findByCompanyName(String company_name);

}