package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.example.demo.model.ContractData;
public interface ContractDataRepository extends JpaRepository<ContractData, Integer> {
	ContractData findById(int id);
	List<ContractData> findByCompanyID(int companyID);
	ContractData findByCompanyIDAndContractID(int companyID,int contractID);
	
}