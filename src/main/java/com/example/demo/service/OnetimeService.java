package com.example.demo.service;

import com.example.demo.repository.OnetimeRepository;
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
import com.example.demo.model.Onetime;
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
public class OnetimeService {
    @Autowired
    OnetimeRepository onetimeRepository;
    
    public void insert(Onetime onetime){
        onetimeRepository.save(onetime);

    }

    public void delete(Onetime onetime){
        onetimeRepository.delete(onetime);

    }

    public List<Onetime> searchAll(){
        return onetimeRepository.findAll();
    }
    
    public Onetime findId(int id){
        return onetimeRepository.findById(id);

    }
    
}
