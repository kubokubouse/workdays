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
import com.example.demo.model.IndividualData;
import com.example.demo.model.MasterUser;
import com.example.demo.repository.IndividualDataRepository;
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
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkdaysRepository;

@Service
public class IndividualService {
    @Autowired
    IndividualDataRepository individualRepository;

    public List <IndividualData> findMail(String mail){
    	return individualRepository.findByMail(mail);
    }
   
    public int insert(int companyID, String mail, int individual_id,String name,String company1,String company2,String company3,String number){
        return individualRepository.InsertIndividualData(companyID, mail, individual_id,name,company1,company2,company3, number);
    }
}
