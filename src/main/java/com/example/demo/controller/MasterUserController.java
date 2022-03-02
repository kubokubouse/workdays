package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
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

import com.example.demo.model.CompanyInfo;
import com.example.demo.model.ContractData;
import com.example.demo.service.CompanyInfoService;
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

	//メニュー　<p><a href="/masteruser">メニュー画面へ戻る</a></p>
	@GetMapping("/masteruser")
	public String showMenue(@ModelAttribute CompanyInfo companyInfo){
		//TODO セッション管理
		return "masteruser";
	}

    //会社情報登録画面
    @GetMapping("/company_info")
	public String register_superUser(@ModelAttribute CompanyInfo companyInfo){
	//TODO セッション管理	
		return "company_info";
	}

    //会社情報登録→確認画面へ
    @PostMapping("/send_company_info")
	public String confirm(@Validated @ModelAttribute CompanyInfo companyInfo,BindingResult result, Model model){
		//CompanyIDに重複があった場合重複画面に飛ぶ
		CompanyInfo companyID = ciService.findByCompanyID(companyInfo.getCompanyID());
		if(companyID !=null){
            model.addAttribute("error", "既に使用されています");
			return "company_info";
		}
        
        if (result.hasErrors()){
            return "company_info";
        }
		return "confirm_companyInfo";
	}

    //会社情報、契約情報をDBに登録
	@PostMapping("/ci_regist")
    public String regist(@Validated @ModelAttribute CompanyInfo ci, BindingResult result, 
		@ModelAttribute ContractData cData, Model model){
		
        if (result.hasErrors()){
			return "confirm_companyInfo";
		}
        ci.setBanned(0);

		//更新日は現在時刻
		String timestamp = getTimeStamp();
		ci.setTopupContract(timestamp);
		companyRepository.save(ci);

		//契約情報も会社IDと登録日、更新日のみを入力
		cData.setCompanyID(ci.getCompanyID());
		cData.setRegister(ci.getRegister());
		cData.setTopupContract(timestamp);
		cData.setLimitedUser(ci.getLimitedUser());

		contractRepository.save(cData);

		//テンプレ格納フォルダを作成
		File input = getInputFolder(ci.getCompanyID());
		File output = getOutputFolder(ci.getCompanyID());

		input.mkdir();
		output.mkdir();

		//TODO セッション管理つける
		return "masteruser";
    }

	//会社情報一覧
	@GetMapping("/companylist")
	public String companylist(Model model){

	//TODO セッション管理する

		List<CompanyInfo> ciList = new ArrayList<CompanyInfo>();
		ciList = ciService.searchAllCompanyInfo();
		model.addAttribute("ciList", ciList);
		return "companyList";
	}  

	//会社情報更新
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

	//会社情報削除
	@RequestMapping("/cidelete")
	public String deleteCompany(@RequestParam("id") String id, Model model){
		ciService.deleteCompany(Integer.valueOf(id));
		List<CompanyInfo> ciList = ciService.searchAllCompanyInfo();
		model.addAttribute("ciList", ciList);

		//テンプレ格納フォルダも削除
		File input = getInputFolder(Integer.valueOf(id));
		File output = getOutputFolder(Integer.valueOf(id));

		input.delete();
		output.delete();

		return "companyList";
	}

		//契約情報一覧
	@GetMapping("/contractlist")
	public String contractlist(Model model){
	
		//TODO セッション管理する
	
		List<ContractData> ciList = ciService.searchAllContractData();
		model.addAttribute("ciList", ciList);
		return "contractlist";
	}

	//契約情報削除
	@RequestMapping("/cddelete")
	public String deletContract(@RequestParam("id") String id, Model model){
		ciService.deleteContract(Integer.valueOf(id));
		List<ContractData> ciList = ciService.searchAllContractData();
		model.addAttribute("ciList", ciList);
		return "contractlist";
	}

		//契約情報更新
		@PostMapping("/updateContract")
		public String UpdateContract(@RequestParam String inputvalue, String userid, String name, Model model){	
			ContractData cd = ciService.findContractByCompanyID(Integer.parseInt(userid));
			System.out.println(name);
			System.out.println(inputvalue);
	
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
			List<ContractData> ciList = ciService.searchAllContractData();
			model.addAttribute("ciList", ciList);
			return "contractlist";
		}
	
	
	   
}