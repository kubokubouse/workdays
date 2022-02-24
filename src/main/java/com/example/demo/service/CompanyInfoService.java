package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.repository.CompanyInfoRepository;
import com.example.demo.model.CompanyInfo;

@Service
@Transactional
public class CompanyInfoService {

    @Autowired
    CompanyInfoRepository companyInfoRepository;

    public CompanyInfo findByCompanyID(int id){
    	return companyInfoRepository.findById(id);
	}
    
	public List<CompanyInfo> searchAllCompanyInfo() {
		List<CompanyInfo> companyList = companyInfoRepository.findAll();
		return companyList;
	}

    
	public void delete(int id){
		CompanyInfo ci = companyInfoRepository.findById(id);
        companyInfoRepository.delete(ci);
    }
}
