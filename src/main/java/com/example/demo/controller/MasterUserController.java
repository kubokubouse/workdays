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

import com.example.demo.model.CompanyInfo;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.repository.CompanyInfoRepository;

@Controller
public class MasterUserController {

	private final CompanyInfoRepository repository;

	@Autowired
    public MasterUserController(CompanyInfoRepository repository){
        this.repository = repository;
    }

    @Autowired
    CompanyInfoService ciService;

    //会社情報登録画面
    @GetMapping("/company_info")
	public String register_superUser(@ModelAttribute CompanyInfo companyInfo){
		
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

    //会社情報をDBに登録
	@PostMapping("/ci_regist")
    public String regist(@Validated @ModelAttribute CompanyInfo ci, BindingResult result, Model model){
		
        if (result.hasErrors()){
			return "confirm_companyInfo";
		}
        ci.setBanned(0);
		repository.save(ci);

		//TODO セッション管理つける
		return "masteruser";
    }


    
    
    
}
