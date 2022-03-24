package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.demo.repository.CompanyInfoRepository;
import com.example.demo.repository.ContractDataRepository;
import com.example.demo.model.BeanContractData;
import com.example.demo.model.CompanyInfo;
import com.example.demo.model.ContractData;

@Service
@Transactional
public class ContractService {

    @Autowired
    CompanyInfoRepository companyInfoRepository;
    @Autowired
    ContractDataRepository contractDataRepository;

    @Autowired
    CompanyInfoService ciService;

    public CompanyInfo findByCompanyID(int id){
    	return companyInfoRepository.findById(id);
	}

	/*public ContractData findContractByCompanyID(int id){
    	return contractDataRepository.findById(id);
	}*/

	
    public List<ContractData> searchAllContractData() {
		List<ContractData> contractList = contractDataRepository.findAll();
		return contractList;
	}

	public List<ContractData> findCompanyID(int companyID) {
		List<ContractData> contractList = contractDataRepository.findByCompanyID(companyID);
		return contractList;
	}

	public ContractData findCompanyIDContractID(int companyID,int contractID) {
		ContractData contractData = contractDataRepository.findByCompanyIDAndContractID(companyID,contractID);
		return contractData;
	}
    


	public void deleteContract(int companyID,int contractID){
		ContractData cd = contractDataRepository.findByCompanyIDAndContractID(companyID,contractID);
        contractDataRepository.delete(cd);
    }

	public int insert(int companyID,Date register,Date start_contract,Date end_contract,int limited_user,String user_rank,int tax_include, int tax_exclude,String topup_contract, int contractID){
		return contractDataRepository.insert(companyID,register,start_contract,end_contract,limited_user,user_rank,tax_include,tax_exclude,topup_contract, contractID);
	}


    public List<BeanContractData> createBeanContractList() {
        List<ContractData> ciList = contractDataRepository.findAll();
		List<BeanContractData>contractDataList=new ArrayList<BeanContractData>();

		for(ContractData contractdata:ciList){
			BeanContractData beanContractData=new BeanContractData();
			beanContractData.setRegister(contractdata.getRegister());
			System.out.println(contractdata.getRegister());
			beanContractData.setStartContract(contractdata.getStartContract());
			beanContractData.setEndContract(contractdata.getEndContract());
			beanContractData.setTopupContract(contractdata.getTopupContract());
			beanContractData.setLimitedUser(contractdata.getLimitedUser());
			beanContractData.setUserRank(contractdata.getUserRank());
			beanContractData.setTaxInclude(contractdata.getTaxInclude());
			beanContractData.setTaxExclude(contractdata.getTaxExclude());
			beanContractData.setCompanyID(contractdata.getCompanyID());
			beanContractData.setContractID(contractdata.getContractID());
			beanContractData.setLimitedCapacity(contractdata.getLimitedCapacity());
			contractDataList.add(beanContractData);
        }
		
		//会社IDと契約IDで昇順にする
		contractDataList.sort(Comparator.comparing(BeanContractData::getCompanyID)
		.thenComparing(Comparator.comparing(BeanContractData::getContractID)));
		
		return contractDataList;
	}
}