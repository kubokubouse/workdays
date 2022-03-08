package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

import com.example.demo.model.ContractData;
public interface ContractDataRepository extends JpaRepository<ContractData, Integer> {
	@Query(value ="insert into contract_data(companyID,register,start_contract,end_contract,limited_user,user_rank,tax_include,tax_exclude,topup_contract,contractID) values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10) ",
			nativeQuery = true)
	int insert(int companyID,Date register,Date start_contract,Date end_contract,int limited_user,String user_rank,int tax_include, int tax_exclude,String topup_contract, int contractID);
	
	//契約情報を会社IDと契約IDで一意にする以上IDだけで一つの契約情報を絞り込むことはシステム上できない（契約を一つしかしていない会社なら実際は特定できるけどシステムがそれを許さない）
	//ContractData findById(int id);
	List<ContractData> findByCompanyID(int companyID);
	ContractData findByCompanyIDAndContractID(int companyID,int contractID);
	
}