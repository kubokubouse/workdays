package com.example.demo.controller;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Holiday;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.service.HolidayService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;
import com.example.demo.service.WorkdayMapping;
import com.google.gson.Gson;

@Controller
public class AdminController {

	@Autowired
    UserService userService;

	@GetMapping("/logout")
	public String logout(){
		
		return "/logout";
	}

	@GetMapping("/userlist")
	public String userlist(){
		
		return "/userlist";
	}


}
