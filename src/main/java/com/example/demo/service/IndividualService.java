package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import com.example.demo.model.IndividualData;
import com.example.demo.repository.IndividualDataRepository;


@Service
public class IndividualService {
    @Autowired
    IndividualDataRepository individualRepository;

    public List <IndividualData> findMail(String mail){
    	return individualRepository.findByMail(mail);
    }
   
    public int insert(int companyID, String mail, int individual_id,String name, int banned, int registered, String company1,String company2,String company3){
        return individualRepository.InsertIndividualData(companyID, mail, individual_id,name, banned, registered,company1,company2,company3);
    }
}