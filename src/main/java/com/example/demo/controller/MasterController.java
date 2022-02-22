package com.example.demo.controller;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

import com.example.demo.model.Login;
import com.example.demo.model.User;
import com.example.demo.service.HolidayService;
import com.example.demo.service.UserService;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.UserListParam;
import com.example.demo.repository.UserRepository;
import com.example.demo.WorkdaysProperties;


@Controller

public class MasterController {
    @GetMapping("/master")
	public String master(@ModelAttribute Login login){

		return "masterlogin";
	}

    @RequestMapping("/masterlogin")
	public String masterlogin(@ModelAttribute User user){

		return "masteruser";
	}
}

