package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ContractData;
public interface ContractDataRepository extends JpaRepository<ContractData, Integer> {
	ContractData findById(int id);
}