package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.repository.CompanyInfoRepository;
import com.example.demo.repository.ContractDataRepository;
import com.example.demo.model.CompanyInfo;
import com.example.demo.model.ContractData;

@Service
@Transactional
public class CompanyInfoService {

    @Autowired
    CompanyInfoRepository companyInfoRepository;
    @Autowired
    ContractDataRepository contractDataRepository;

    public CompanyInfo findByCompanyID(int id){
    	return companyInfoRepository.findById(id);
	}

	public ContractData findContractByCompanyID(int id){
    	return contractDataRepository.findById(id);
	}

	public List<CompanyInfo> findByCompanyName(String companyName) {
		List<CompanyInfo> ci = companyInfoRepository.findByCompanyName(companyName);
		return ci;
	}
    
	public List<CompanyInfo> searchAllCompanyInfo() {
		List<CompanyInfo> companyList = companyInfoRepository.findAll();
		return companyList;
	}

    public List<ContractData> searchAllContractData() {
		List<ContractData> contractList = contractDataRepository.findAll();
		return contractList;
	}
    
	public void deleteCompany(int id){
		CompanyInfo ci = companyInfoRepository.findById(id);
        companyInfoRepository.delete(ci);
    }

	public void deleteContract(int id){
		ContractData cd = contractDataRepository.findById(id);
        contractDataRepository.delete(cd);
    }
}
