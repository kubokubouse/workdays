package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.demo.model.CompanyInfo;
import com.example.demo.model.IdUser;
import com.example.demo.model.IndividualData;
import com.example.demo.repository.IndividualDataRepository;


@Service
public class IndividualService {
    @Autowired
    IndividualDataRepository individualRepository;
    @Autowired
    CompanyInfoService companyInfoService;

    public void delete(IndividualData iData){
        individualRepository.delete(iData);
    }
    public List <IndividualData> findMail(String mail){
    	return individualRepository.findByMail(mail);
    }

    public List <IndividualData> findCompanyId(int cid){
    	return individualRepository.findByCompanyID(cid);
    }
    public List <IndividualData> findCompanyIdRegistered(int cid,int registered){
    	return individualRepository.findByCompanyIDAndRegistered(cid,registered);
    }
   
    public int insert(int companyID, String mail, int individual_id,String name, int banned, int registered, String company1,String company2,String company3){
        return individualRepository.InsertIndividualData(companyID, mail, individual_id,name, banned, registered,company1,company2,company3);
    }
    
    //個別ユーザーデータのデータをidUserに詰め替える+会社名を取得する
    public IdUser getIdUser(IndividualData iData){
        IdUser idUser=new IdUser();
        CompanyInfo companyInfo= companyInfoService.findByCompanyID(iData.getCompanyID());
		idUser.setCompany1(iData.getCompany1());
		idUser.setCompany2(iData.getCompany2());
		idUser.setCompany3(iData.getCompany3());
		idUser.setBanned(iData.getBanned());
		idUser.setCompanyID(iData.getCompanyID());
		idUser.setCompanyName(companyInfo.getCompanyName());
        return idUser;
    }
}