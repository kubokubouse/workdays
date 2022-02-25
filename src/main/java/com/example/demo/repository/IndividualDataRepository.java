package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.demo.model.IndividualData;
public interface IndividualDataRepository extends JpaRepository<IndividualData, Integer> {
	IndividualData findById(int id);
	IndividualData findByMailAndCompanyID(String mail,int companyID);
	List<IndividualData> findByMail(String mail);
}
