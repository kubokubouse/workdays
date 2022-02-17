package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.User;
import com.example.demo.model.UserListParam;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.ObjectError;


@Controller
public class AdminController {

	@Autowired
    UserService userService;

	@GetMapping("/logout")
	public String logout(){
		
		return "/logout";
	}

	@GetMapping("/userlist")
	public String userlist(@Validated  Model model){
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		return "/userlist";
	}

	
	@RequestMapping(value = "/userlistUpdate", method = RequestMethod.POST)
	public String listUpdate(@Validated @ModelAttribute UserListParam userListParam, BindingResult result, Model model) {
	   
	  if (result.hasErrors()) {
		  List<String> errorList = new ArrayList<String>();
		  for (ObjectError error : result.getAllErrors()) {
			  if (!errorList.contains(error.getDefaultMessage())) {
				  errorList.add(error.getDefaultMessage());
				}
			}
		  model.addAttribute("validationError", errorList);
		  return "userlist";
		}
		userService.updateUserAll(userListParam);
		return "userlist";
	
	}

	@RequestMapping(value="/userdelete")
	public String deleteuser(@RequestParam String userid, Model model){
		System.out.println(userid);
		userService.delete(Integer.parseInt(userid));
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		return "/userlist";
	}


	@PostMapping("/UserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String userid, Model model){	
		User user=userService.findId(Integer.parseInt(userid));
		switch (name) {
			case "lastname":
			  user.setLastname(inputvalue);
			  break;
			case "firsttname":
			  user.setFirstname(inputvalue);
			  break;
			  case "email":
			  user.setEmail(inputvalue);
			  break;
			case "password":
			  user.setPassword(inputvalue);
			  break;
			case "company1":
			  user.setCompany1(inputvalue);
			  break;
			case "company2":
			  user.setCompany2(inputvalue);
			  break;
			} 
		userService.update(user);
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		return "/userlist";
	}

}