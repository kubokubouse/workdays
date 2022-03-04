package com.example.demo.service;
import com.example.demo.repository.SuperUserRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Holiday;
import com.example.demo.model.MasterUser;
import com.example.demo.repository.MasterUserRepository;
import com.example.demo.model.StringList;
import com.example.demo.model.StringListParam;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.SuperUser;
import com.example.demo.model.SuperUserData;
import com.example.demo.model.SuperUserListParam;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingData;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.repository.SuperUserRepository2;
import com.example.demo.repository.SuperUserRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkdaysRepository;

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
