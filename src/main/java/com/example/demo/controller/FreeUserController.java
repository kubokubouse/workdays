package com.example.demo.controller;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import javax.servlet.ServletException;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.box.sdk.*;
import com.box.sdk.BoxItem.Info;

import javax.servlet.http.*;

import com.example.demo.model.Otherpa;
import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.CompanyInfo;
import com.example.demo.model.IdUser;
import com.example.demo.model.IndividualData;
import com.example.demo.model.Login;
import com.example.demo.model.Mail;
import com.example.demo.model.Onetime;
import com.example.demo.model.StringListParam;
import com.example.demo.model.User;
import com.example.demo.model.SuperUser;
import com.example.demo.model.RePassword;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.model.YearMonth;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.repository.ContractDataRepository;
import com.example.demo.repository.SuperUserRepository2;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HolidayService;
import com.example.demo.service.IndividualService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.OnetimeService;
import com.example.demo.service.SuperUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.ContractService;
import com.example.demo.service.WorkdayMapping;
import com.example.demo.service.WorkdaysService;
import com.example.demo.service.CellvalueGet;
import com.example.demo.WorkdaysProperties;
import com.google.gson.Gson;
import com.example.demo.model.Judgeused;

@Controller
public class FreeUserController extends WorkdaysProperties {
    @Autowired
    UserService userService;
    @Autowired
    SuperUserRepository2 superUserRepository2;
    @Autowired
    SuperUserService superUserService;
	@Autowired
    CompanyInfoService companyInfoService;
	@Autowired
    ContractDataRepository contractRepository;
	@Autowired
	ContractService contractService;
	@Autowired
	HolidayService holidayService;
    @Autowired
    IndividualService individualService;
	
    @Autowired
    HttpSession session;

    //新規フリーユーザー登録
    @GetMapping("/freeuser")
	public String freeuser(@ModelAttribute User user){
        return "freeuser";
	}

    @PostMapping("/confirmfree")
    public String confirmfree(@ModelAttribute User user,Model model){
        model.addAttribute("user",user);
        return "/confirmfree";
    }

    @PostMapping("/registfree")
    public String registfree(@ModelAttribute User user,Model model){
        user.setBanned(0);
        userService.update(user);

        IndividualData iData=individualService.toiData(user);
        individualService.insert(iData);

        File input = getfreeInputFolder(user.getEmail());
		File output = getfreeOutputFolder(user.getEmail());

		input.mkdir();
		output.mkdir();
        return "/registerdone";
    }
}
