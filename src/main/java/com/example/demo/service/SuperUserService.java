package com.example.demo.service;
import com.example.demo.repository.SuperUserRepository;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import com.example.demo.model.SuperUser;
import com.example.demo.model.SuperUserData;
import com.example.demo.model.SuperUserListParam;

import com.example.demo.repository.SuperUserRepository2;


@Service

public class SuperUserService {
    @Autowired
    SuperUserRepository2 superRepository2;

    @Autowired
    SuperUserRepository superUserRepository;
    
    public SuperUser findEmailCompanyID(String Email,int companyID){
        return  superRepository2.findByEmailAndCompanyID(Email, companyID);

    }
    public SuperUser findbyEmail(String Email){
        return  superRepository2.findByEmail(Email);

    }
    public void update(SuperUser superUser){
        superRepository2.save(superUser);
    }
    public SuperUser findId(String superUserid){
        return superRepository2.findById(Integer.parseInt(superUserid));
       
    }
    public void delete(String superUserid){
        SuperUser superUser=superRepository2.findById(Integer.parseInt(superUserid));
        superRepository2.delete(superUser);
    }
    public SuperUserListParam searchAllSuperUser() {
		List<SuperUser>superUserList=superRepository2.findAll();
		SuperUserListParam superUserListParam=new SuperUserListParam();
		List<SuperUserData> superUserDataList= new ArrayList<SuperUserData>();
		for(SuperUser superUser:superUserList){
			SuperUserData superUserData=new SuperUserData();
			superUserData.setId(superUser.getId());
            superUserData.setPass(superUser.getPass());
			superUserData.setEmail(superUser.getEmail());
            superUserData.setCompanyID(superUser.getCompanyID());
			superUserDataList.add(superUserData);
		}
		superUserListParam.setSuperUserDataList(superUserDataList);
		return superUserListParam;
	}
}
