package com.example.demo.controller;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.demo.model.ContractData;
import com.example.demo.model.Login;
import com.example.demo.model.User;
import com.example.demo.model.UserData;
import com.example.demo.model.UserListParam;
import com.example.demo.model.SuperUser;
import com.example.demo.model.MasterUser;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.ContractService;
import com.example.demo.service.UserService;
import com.example.demo.service.SuperUserService;
import com.example.demo.model.SuperUserListParam;
import com.example.demo.repository.ContractDataRepository;
import com.example.demo.repository.SuperUserRepository2;



@Controller

public class MasterController {
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
		session.setAttribute("masterUser", masterUser);
		return "masteruser";
	}
    //管理者登録ページに移行
	@GetMapping("/registersuperuser")
	public String registerAdmin(@ModelAttribute SuperUser superUser,BindingResult result,Model model){
        
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
        
		return "registersuperuser";
	}

    //データ送信時に確認画面に移行
    @PostMapping("/confirmsuperuser")
    public String confirmsuperuser(@Validated @ModelAttribute SuperUser superUser,BindingResult result,Model model){
		if (result.hasErrors()){
			// エラーがある場合、index.htmlに戻る
            return "registersuperuser";
        }
        SuperUser usedEmailuser=superUserService.findEmailCompanyID(superUser.getEmail(),superUser.getCompanyID());
		if(usedEmailuser!=null){
			return "usedemail";
		}
        
        model.addAttribute("superUser", superUser);
		return "confirmsuperuser";
	}

    //管理者情報をDBに登録
	@PostMapping("/registsuperuser")
    public String registsuperuser(@Validated @ModelAttribute SuperUser superUser, BindingResult result, Model model){
		
		System.out.println(superUser.getPass());
		superUserRepository2.save(superUser);
		// ルートパス("/") にリダイレクトします
		return "masteruser";
    }


	//契約情報登録ページに遷移
	@GetMapping("/contract")
	public String contract(@ModelAttribute ContractData contractData){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		return "contract";
	}
 
	//契約情報送信時に確認画面に遷移
	@PostMapping("/confirmcontract")
	public String confirmcontract(@Validated @ModelAttribute ContractData contractData,BindingResult result, Model model){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		if(contractData.getRegister()==null){
			model.addAttribute("error1", "日付が未入力です");
			
		}
		if(contractData.getStartContract()==null){
			model.addAttribute("error2", "日付が未入力です");
			
		}
		if(contractData.getEndContract()==null){
			model.addAttribute("error3", "日付が未入力です");
		}

		if (result.hasErrors()){
			// エラーがある場合、登録画面に戻る
			return "contract";
		}
 		
		model.addAttribute("contractData", contractData);
		return "confirmcontract";
	}
 
	//契約情報をDBに登録
	@PostMapping("/registercontract")
	public String registercontract(@Validated @ModelAttribute  ContractData contractData, BindingResult result, Model model){
		List<ContractData> contractDataList=contractService.findCompanyID(contractData.getCompanyID());
		if(CollectionUtils.isEmpty(contractDataList)){
			int contractID=1;
			contractData.setContractID(contractID);
		}

		else{
			int contractID=0;
			for(ContractData contractdata:contractDataList){
				if (contractID<contractdata.getContractID()){
					contractID=contractdata.getContractID();
				}
				
			}
			contractID=contractID+1;
			contractData.setContractID(contractID);

		}
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime());
        long timeInMilliSeconds = date.getTime();
        java.sql.Date date1 = new java.sql.Date(timeInMilliSeconds);
		String timeStamp=date1.toString();

		contractData.setTopupContract(timeStamp);

		System.out.println(contractData);
		
		contractRepository.save(contractData);
		
		return "masteruser";
	}

    //ユーザー編集画面
	@GetMapping("/superuserlist")
	public String superuserlist(@Validated  Model model, @ModelAttribute SuperUser superuser){

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
        
        SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "superuserlist";
	}

    @RequestMapping(value="/superuserdelete")
	public String superuserdelete(@RequestParam("id") String id, Model model){
		
		superUserService.delete(id);
		SuperUserListParam superUserListParam = superUserService.searchAllSuperUser();
		model.addAttribute("superUserListParam", superUserListParam);
		return "superUserlist";
	}

    @PostMapping("/SuperUserAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String superuserid, Model model){	
	
        SuperUser superUser=superUserService.findId(superuserid);

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
		return "superuserlist";
	}

	//ユニバーサルユーザー一覧表示
	@GetMapping("/alluserlist")
	public String alluserlsit(Model model){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		
		UserListParam userListParam = userService.searchAllUser();
		model.addAttribute("userListParam", userListParam);
		List<UserData> userDataList=userListParam.getUserDataList();
		model.addAttribute("userDataList",userDataList);
		
		return "universaluserlist";
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
		return "universaluserlist";
	}
}

