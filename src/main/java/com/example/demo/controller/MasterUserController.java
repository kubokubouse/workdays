package com.example.demo.controller;
import java.sql.Date;
import java.io.File;

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
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;

import com.example.demo.model.MasterUser;
import com.example.demo.model.RegularTime;
import com.example.demo.model.SuperUser;
import com.example.demo.model.BeanContractData;
import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.CompanyInfo;
import com.example.demo.model.ContractData;
import com.example.demo.model.IndividualData;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.ContractService;
import com.example.demo.service.IndividualService;
import com.example.demo.service.RegularTimeService;
import com.example.demo.service.SuperUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;
import com.example.demo.repository.CompanyInfoRepository;
import com.example.demo.repository.ContractDataRepository;
import com.example.demo.WorkdaysProperties;

@Controller
public class MasterUserController extends WorkdaysProperties{

	private final CompanyInfoRepository companyRepository;
	private final ContractDataRepository contractRepository;

	@Autowired
    public MasterUserController(CompanyInfoRepository cirepository, ContractDataRepository cDataRepository){
        this.companyRepository = cirepository;
		this.contractRepository = cDataRepository;
    }

    @Autowired
    CompanyInfoService ciService;
	@Autowired
    HttpSession session;
	@Autowired
	ContractService contractService;
	@Autowired
    UserService userService;
	@Autowired
    RegularTimeService rtService;
	@Autowired
    WorkdaysService workdaysService;
	@Autowired
    IndividualService individualService;
	@Autowired
    SuperUserService superUserService;

	//???????????????????????????
	@GetMapping("/masteruser")
	public String showMenue(@ModelAttribute CompanyInfo companyInfo){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		return "masteruser";
	}

    //????????????????????????
    @GetMapping("/company_info")
	public String register_superUser(@ModelAttribute CompanyInfo companyInfo){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		return "company_info";
	}

    //????????????????????????????????????
    @PostMapping("/send_company_info")
	public String confirm(@Validated @ModelAttribute CompanyInfo companyInfo,BindingResult result, Model model){
		//CompanyID?????????????????????????????????
		int companyId = companyInfo.getCompanyID();
		CompanyInfo company = ciService.findByCompanyID(companyId);
		while (company == null) {
			company = ciService.findByCompanyID(companyId);
			if(company == null){
				break;
			}
			companyId++;
		}
		
		if(companyInfo.getRegister()==null){
			model.addAttribute("error2", "????????????????????????");
			return "company_info";
		}
        
        if (result.hasErrors()){
            return "company_info";
        }
		return "confirm_companyInfo";
	}

    //??????????????????????????????DB?????????
	@PostMapping("/ci_regist")
    public String regist(@Validated @ModelAttribute CompanyInfo ci, BindingResult result, 
		@ModelAttribute ContractData cData, Model model){
		
        if (result.hasErrors()){
			return "confirm_companyInfo";
		}
        ci.setBanned(0);

		//????????????????????????
		String timestamp = getTimeStamp();
		ci.setTopupContract(timestamp);
		companyRepository.save(ci);


		//???????????????????????????????????????
		File input = getInputFolder(ci.getCompanyID());
		File output = getOutputFolder(ci.getCompanyID());

		input.mkdir();
		output.mkdir();

		return "masteruser";
    }

	//??????????????????
	@GetMapping("/companylist")
	public String companylist(Model model){

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }

		List<CompanyInfo> ciList = new ArrayList<CompanyInfo>();
		ciList = ciService.searchAllCompanyInfo();
		model.addAttribute("ciList", ciList);
		return "companyList";
	}  

	//??????????????????
	@PostMapping("/updateCompanyInfo")
	public String UpdateCompanyInfo(@RequestParam String inputvalue, String userid, String name, Model model){	
		CompanyInfo ci = ciService.findByCompanyID(Integer.parseInt(userid));
        System.out.println(name);
        System.out.println(inputvalue);

		switch (name) {
			case "companyName":
			  ci.setCompanyName(inputvalue);
			  break;
			case "register":
			  ci.setRegister(Date.valueOf(inputvalue));
			  break;
			  case "topupContract":
			  ci.setTopupContract(getTimeStamp());
			  break;
			case "person":
			  ci.setPerson(inputvalue);
			  break;
			case "tel":
			  ci.setTel(inputvalue);
			  break;
			case "mail":
			  ci.setMail(inputvalue);
			  break;
			case "homepage":
			  ci.setHomepage(inputvalue);;
			  break;
			case "limitedUser":
			  ci.setLimitedUser(Integer.valueOf(inputvalue));
			  break;
            case "banned":
			  ci.setBanned(Integer.valueOf(inputvalue));
			  break;
			} 
		companyRepository.save(ci);
		List<CompanyInfo> ciList = ciService.searchAllCompanyInfo();
		model.addAttribute("ciList", ciList);
		return "companyList";
	}

	//??????????????????
	@RequestMapping("/cidelete")
	public String deleteCompany(@RequestParam("id") String id, Model model){
		int cid=Integer.valueOf(id);
		ciService.deleteCompany(cid);
		List<CompanyInfo> ciList = ciService.searchAllCompanyInfo();
		model.addAttribute("ciList", ciList);

		//???????????????????????????????????????
		File input = getInputFolder(cid);
		File output = getOutputFolder(cid);

		input.delete();
		output.delete();

		//??????ID???????????????????????????????????????
		List<ContractData>cdList= contractService.findCompanyID(cid);
		for(ContractData contractdata:cdList){
			contractService.deleteContract(cid,contractdata.getContractID());
		}
		//??????ID??????????????????????????????????????????
		List<SuperUser>superList=superUserService.findCompanyID(cid);
		for(SuperUser superUser:superList){
			superUserService.delete(superUser.getId());
		}
		//??????ID???????????????????????????????????????????????????
		List <IndividualData>iDataList= individualService.findCompanyId(cid);
		for(IndividualData iData:iDataList){
			individualService.delete(iData);
		}
		return "companyList";
	}

		//??????????????????
	@GetMapping("/contractlist")
	public String contractlist(Model model){
	
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
	
		//????????????????????????????????????????????????????????????
		List<BeanContractData>contractDataList=contractService.createBeanContractList();
		model.addAttribute("contractDataList",contractDataList);
		return "contractList";
	}

	//??????????????????
	@RequestMapping("/cddelete")
	public String deletContract(@RequestParam("id") String id, Model model){
		String[] values = id.split(":");
		int companyID = Integer.parseInt(values[0]);
		int contractID=Integer.parseInt(values[1]);
		contractService.deleteContract(companyID,contractID);

		List<BeanContractData>contractDataList=contractService.createBeanContractList();
		model.addAttribute("contractDataList",contractDataList);
		return "contractList";
	}

		//??????????????????
	@PostMapping("/updateContract")
	public String UpdateContract(@RequestParam String inputvalue, String userid, String name, Model model){
		String[] values = userid.split(":");
		int companyID = Integer.parseInt(values[0]);
		int contractID=Integer.parseInt(values[1]);	
		ContractData cd = contractService.findCompanyIDContractID(companyID,contractID);
	
		switch (name) {
		    case "register":
			 cd.setRegister(Date.valueOf(inputvalue));
			break;
			case "startContract":
			 cd.setStartContract(Date.valueOf(inputvalue));
			break;
			case "endContract":
			 cd.setEndContract(Date.valueOf(inputvalue));
			break;
			case "limitedUser":
			 cd.setLimitedUser(Integer.valueOf(inputvalue));
			break;
			case "userRank":
			 cd.setUserRank(inputvalue);
			break;
			case "taxInclude":
			 cd.setTaxInclude(Integer.valueOf(inputvalue));
			break;
			case "taxExclude":
			 cd.setTaxExclude(Integer.valueOf(inputvalue));
			break;
		} 
		contractRepository.save(cd);
		List<BeanContractData>contractDataList=contractService.createBeanContractList();
		model.addAttribute("contractDataList",contractDataList);
		return "contractList";
	}

	//????????????????????????
	@GetMapping("/regulartimelist")
	public String regulartimelist(Model model,@ModelAttribute BeanRegularTime beanRegularTime){

		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }

		//Bean??????????????????
		List<RegularTime> rtbList = new ArrayList<RegularTime>();
		rtbList = rtService.findAll();
		List<BeanRegularTime> rtList=new ArrayList<BeanRegularTime>();
		for(RegularTime brt:rtbList){
			BeanRegularTime BRtime=new BeanRegularTime();
			BRtime.setStart(brt.getStart().toString().substring(0,5));
			BRtime.setEnd(brt.getEnd().toString().substring(0,5));
			BRtime.setHalftime(brt.getHalftime().toString().substring(0,5));
			BRtime.setWorktime(brt.getWorktime().toString().substring(0,5));
			BRtime.setId(brt.getId());
			rtList.add(BRtime);
		}
		
		model.addAttribute("rtList", rtList);
		//model.addAttribute("Brtime", Brtime);
		return "regulartimelist";
	}  
	

	//??????????????????
    @GetMapping("/regulartime")
	public String register_regulartime(@ModelAttribute BeanRegularTime beanRegularTime, Model model){
		MasterUser masterUser = (MasterUser)session.getAttribute("masterUser");
        if(masterUser==null){
            return "masterloginfault";
        }
		//model.addAttribute("Brtime",beanRegularTime);
		return "regulartime";
	}

    //????????????????????????????????????
    @PostMapping("/send_Brtime")
	public String confirm_Brtime(@Validated @ModelAttribute BeanRegularTime Brtime,BindingResult result, Model model){
		
        if (result.hasErrors()){
            return "company_info";
        }
		model.addAttribute("Brtime",Brtime);
		return "confirm_regulartime";
	}

    //???????????????DB?????????
	@PostMapping("/br_regist")
    public String regist_regulartime(@Validated @ModelAttribute BeanRegularTime beanRegularTime, BindingResult result, 
		@ModelAttribute ContractData cData, Model model){
		
        if (result.hasErrors()){
			List<RegularTime> rtbList = new ArrayList<RegularTime>();
			rtbList = rtService.findAll();
			List<BeanRegularTime> rtList=new ArrayList<BeanRegularTime>();
			for(RegularTime brt:rtbList){
			BeanRegularTime BRtime=new BeanRegularTime();
			BRtime.setStart(brt.getStart().toString().substring(0,5));
			BRtime.setEnd(brt.getEnd().toString().substring(0,5));
			BRtime.setHalftime(brt.getHalftime().toString().substring(0,5));
			BRtime.setWorktime(brt.getWorktime().toString().substring(0,5));
			BRtime.setId(brt.getId());
			rtList.add(BRtime);
		}
		
		model.addAttribute("rtList", rtList);
			return "regulartimelist";
		}
		
		//Brtime?????????RegularTime???regulartime???????????????DB?????????
		//Brtime?????????String?????????time????????????

		//??????-??????-???????????????????????????????????????
		int sminutes=userService.allminutes(beanRegularTime.getStart());
		int hminutes=userService.allminutes(beanRegularTime.getHalftime());
		int eminutes=userService.allminutes(beanRegularTime.getEnd());
		int wminutes=eminutes-hminutes-sminutes;
		//??????????????????????????????00:00???????????????
		String worktime=userService.wminutes(wminutes);

		//??????id=1??????????????????????????????????????????????????????????????????id=1???????????????????????????????????????2022???4????????????
		RegularTime rTime=rtService.findId(1);		
		RegularTime regularTime=new RegularTime();
		regularTime.setStart(userService.toTime(beanRegularTime.getStart()));
		regularTime.setEnd(userService.toTime(beanRegularTime.getEnd()));
		regularTime.setHalftime(userService.toTime(beanRegularTime.getHalftime()));
		regularTime.setWorktime(userService.toTime(worktime));
		if (rTime == null) {
			regularTime.setId(1);
		}
		rtService.insert(regularTime);

		List<RegularTime> rtbList = new ArrayList<RegularTime>();
		rtbList = rtService.findAll();
		List<BeanRegularTime> rtList=new ArrayList<BeanRegularTime>();
		for(RegularTime brt:rtbList){
			BeanRegularTime BRtime=new BeanRegularTime();
			BRtime.setStart(brt.getStart().toString().substring(0,5));
			BRtime.setEnd(brt.getEnd().toString().substring(0,5));
			BRtime.setHalftime(brt.getHalftime().toString().substring(0,5));
			BRtime.setWorktime(brt.getWorktime().toString().substring(0,5));
			BRtime.setId(brt.getId());
			rtList.add(BRtime);
		}
		
		model.addAttribute("rtList", rtList);
		//model.addAttribute("Brtime", Brtime);

		return "regulartimelist";
    }

	//????????????
	@RequestMapping("/regulardelete")
	public String deleteregular(@RequestParam("id") String id, @ModelAttribute BeanRegularTime beanRegularTime, Model model ){
		rtService.delete(Integer.valueOf(id));
		
		List<RegularTime> rtbList = new ArrayList<RegularTime>();
		rtbList = rtService.findAll();

		List<BeanRegularTime> rtList=new ArrayList<BeanRegularTime>();
		for(RegularTime brt:rtbList){
			BeanRegularTime BRtime=new BeanRegularTime();
			BRtime.setStart(brt.getStart().toString().substring(0,5));
			BRtime.setEnd(brt.getEnd().toString().substring(0,5));
			BRtime.setHalftime(brt.getHalftime().toString().substring(0,5));
			BRtime.setWorktime(brt.getWorktime().toString().substring(0,5));
			BRtime.setId(brt.getId());
			rtList.add(BRtime);
		}
		
		model.addAttribute("rtList", rtList);
		

		return "regulartimelist";
	}

	@PostMapping("/RbAjaxServlet")
	public String AjaxServlet(@RequestParam String inputvalue, String name,String day, Model model ){	
		int id=Integer.parseInt(day);
		RegularTime rtime= rtService.findId(id);
		System.out.println("before="+inputvalue);
		
		//0900????????????:??????????????????????????????????????????:???????????????
		if(inputvalue.length()==4){
			StringBuilder sb = new StringBuilder();
            sb.append(inputvalue);
			sb.insert(2, ":");
			inputvalue=sb.toString();  

		}
		System.out.println("after="+inputvalue);
		switch (name) {
			case "start":
				//?????????????????????????????????????????????????????????????????????????????????-??????-????????????????????????
				int sminutes=userService.allminutes(inputvalue);
				int hminutes=userService.allminutes(rtime.getHalftime().toString());
				int eminutes=userService.allminutes(rtime.getEnd().toString());
				int wminutes=eminutes-hminutes-sminutes;
			    //??????????????????????????????00:00???????????????
				String worktime=userService.wminutes(wminutes);
				//String??????time??????????????????DB?????????
				rtime.setWorktime(userService.toTime(worktime));
				rtime.setStart(userService.toTime(inputvalue));//String???localTime??????????????????Time???????????????????????????
			break;
			
			case "end":
			sminutes=userService.allminutes(rtime.getStart().toString());
			hminutes=userService.allminutes(rtime.getHalftime().toString());
			eminutes=userService.allminutes(inputvalue);
			wminutes=eminutes-hminutes-sminutes;
			//??????????????????????????????00:00???????????????
			worktime=userService.wminutes(wminutes);
			//String??????time??????????????????DB?????????
			rtime.setWorktime(userService.toTime(worktime));
	
			rtime.setEnd(userService.toTime(inputvalue));
			break;

			case "halftime":
			sminutes=userService.allminutes(rtime.getStart().toString());
			hminutes=userService.allminutes(inputvalue);
			eminutes=userService.allminutes(rtime.getEnd().toString());
			wminutes=eminutes-hminutes-sminutes;
			//??????????????????????????????00:00???????????????
			worktime=userService.wminutes(wminutes);
			//String??????time??????????????????DB?????????
			rtime.setWorktime(userService.toTime(worktime));
			rtime.setHalftime(userService.toTime(inputvalue));
			
			break;

			case "worktime":
			 rtime.setEnd(userService.toTime(inputvalue));
			break;
			
			
			} 
		rtService.update(rtime);

		//Bean??????????????????
		/*
		List<RegularTime> rtbList = new ArrayList<RegularTime>();
		rtbList = rtService.findAll();
		List<BeanRegularTime> rtList=new ArrayList<BeanRegularTime>();
		BeanRegularTime Brtime=new BeanRegularTime();
		int i=1;
		for(RegularTime brt:rtbList){
			Brtime.setStart(brt.getStart().toString().substring(0,5));
			Brtime.setEnd(brt.getEnd().toString().substring(0,5));
			Brtime.setHalftime(brt.getHalftime().toString().substring(0,5));
			Brtime.setWorktime(brt.getWorktime().toString().substring(0,5));
			Brtime.setId(i);
			i=i+1;
			rtList.add(Brtime);
		}
		
		model.addAttribute("rtList", rtList);*/
		return "regulartimelist";
		
	}
	 
}