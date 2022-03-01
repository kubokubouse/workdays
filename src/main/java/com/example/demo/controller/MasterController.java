package com.example.demo.controller;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;
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
import com.example.demo.model.UserData;
import com.example.demo.model.UserListParam;
import com.example.demo.model.SuperUser;
import com.example.demo.model.MasterUser;
import com.example.demo.service.HolidayService;
import com.example.demo.service.UserService;
import com.example.demo.service.SuperUserService;
import com.example.demo.model.SuperUserLogin;
import com.example.demo.model.SuperUserListParam;
import com.example.demo.repository.SuperUserRepository2;
import com.example.demo.WorkdaysProperties;


@Controller

public class MasterController {
    @Autowired
    UserService userService;
    @Autowired
    SuperUserRepository2 superUserRepository2;
    @Autowired
    SuperUserService superUserService;
    
    @Autowired
    HttpSession session;

    @GetMapping("/master")
	public String master(@ModelAttribute Login login){

		return "masterlogin";
	}

    @RequestMapping("/masterlogin")
	public String masterlogin(@ModelAttribute Login login){
        String id = login.getEmail();
		String password = login.getPassword();
        MasterUser masterUser=userService.findIdPass(id, password);
        if(masterUser==null){
            return "masterloginfault";
        }

		return "masteruser";
	}
    //管理者登録ページに移行
	@GetMapping("/registersuperuser")
	public String registerAdmin(@ModelAttribute SuperUser superUser,Model model,BindingResult result){
        
        
		return "registersuperuser";
	}

    //データ送信時に確認画面に移行
    @PostMapping("/confirmsuperuser")
    public String confirmsuperuser(@ModelAttribute SuperUser superUser,Model model,BindingResult result){
        SuperUser usedEmailuser=superUserService.findEmailCompanyID(superUser.getEmail(),superUser.getCompanyID());
		if(usedEmailuser!=null){
			return "usedemail";
		}
        
        if (result.hasErrors()){
			// エラーがある場合、index.htmlに戻る
            return "registersuperuser";
        }
		

        model.addAttribute("superUser", superUser);
		return "confirmsuperuser";
	}

    //会員情報をDBに登録
	@PostMapping("/registsuperuser")
    public String registsuperuser(@Validated @ModelAttribute SuperUser superUser, BindingResult result, Model model){
		
		System.out.println(superUser.getPass());
		superUserRepository2.save(superUser);
		// ルートパス("/") にリダイレクトします
		return "masteruser";
    }

    //ユーザー編集画面
	@GetMapping("/superuserlist")
	public String superuserlist(@Validated  Model model, @ModelAttribute SuperUser superuser){
        
        SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "/superuserlist";
	}

    @RequestMapping(value="/superuserdelete")
	public String superuserdelete(@RequestParam String superUserid, Model model){
		
		superUserService.delete(superUserid);
		SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "/superUserlist";
	}

    @PostMapping("/SuperUserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String superuserid, Model model){	
		System.out.println(name);
        System.out.println(inputvalue);
        System.out.println(superuserid);
        SuperUser superUser=superUserService.findId(superuserid);
        System.out.println(name);
        System.out.println(inputvalue);

		switch (name) {
			case "pass":
			  superUser.setPass(inputvalue);
			  break;
			case "email":
			  superUser.setEmail(inputvalue);
			  break;
			  case "companyID":
			  superUser.setCompanyID(Integer.parseInt(inputvalue));
			  break;
			
			} 
		superUserService.update(superUser);
		SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "/superuserlist";
	}

	//ユニバーサルユーザー一覧表示
	@GetMapping("/alluserlist")
	public String alluserlsit(Model model){
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		List<UserData> userDataList=userListParam.getUserDataList();
		model.addAttribute("userDataList",userDataList);
		
		return "/universaluserlist";
	}
	//Ajaxで値を取得しDBに登録
	@PostMapping("/UniversalUserAjaxServlet")
	public String UniversalUserAjaxServlet(@RequestParam String inputvalue, String userid, String name, Model model){	
		User user = userService.findId(Integer.parseInt(userid));
        System.out.println(name);
        System.out.println(inputvalue);

		switch (name) {
			case "banned":
			  user.setBanned(Integer.parseInt(inputvalue));
			break;
			
		} 
		userService.update(user);
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		List<UserData> userDataList=userListParam.getUserDataList();
		model.addAttribute("userDataList",userDataList);
		return "/universaluserlist";
	}
}

