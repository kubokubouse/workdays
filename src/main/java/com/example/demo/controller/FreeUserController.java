package com.example.demo.controller;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.BeanFile;
import com.example.demo.model.IndividualData;
import com.example.demo.model.User;

import com.example.demo.repository.ContractDataRepository;
import com.example.demo.repository.SuperUserRepository2;
import com.example.demo.service.HolidayService;
import com.example.demo.service.IndividualService;
import com.example.demo.service.SuperUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyInfoService;
import com.example.demo.service.ContractService;

import com.example.demo.WorkdaysProperties;


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
    public String confirmfree(@ModelAttribute User user,BindingResult result,Model model){
        if (result.hasErrors()||user==null){
			model.addAttribute("error","入力内容に不備があります");
			return "freeuser";
		}

        User existCheck = userService.findEmail(user.getEmail());
        if (existCheck != null) {
            model.addAttribute("error","このメールアドレスは既に登録されています");
			return "freeuser";
        }

        model.addAttribute("user",user);
        return "confirmfree";
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
        return "registerdone";
    }
    @GetMapping("/filecheck")
    public String filecheck(Model model){
        User user = (User)session.getAttribute("Data");
        if (user == null) {
            return "accessError";
        }
        List<BeanFile> BeanFileList = new ArrayList<BeanFile>();
        String email=user.getEmail();
        String fileFolderPath = getfreeInputFolder(email).getAbsolutePath();
            
        File fileFolder = new File(fileFolderPath);
        File[] fileList = fileFolder.listFiles();
            
        int fileCount = 0;
        if (fileList != null) {
            for (File file : fileList){
                BeanFile beanFile=new BeanFile();
                beanFile.setFileName(file.getName());
                beanFile.setFilePath("https://workdays.jp/download/"+email + "_input/"+file.getName());
                BeanFileList.add(beanFile); 
                fileCount++;
            }  
        } 
        if(fileCount == 0) {
            model.addAttribute("error", "ファイルが存在しません");
            return "templatelist";
        }   
        model.addAttribute("folder", email + "_input");
        model.addAttribute("fileName", BeanFileList);
        return "templatelist";       
    }

    
}
