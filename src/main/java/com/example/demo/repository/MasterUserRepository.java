package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.MasterUser;

public interface MasterUserRepository extends JpaRepository<MasterUser, String> {
	MasterUser findByIdAndPassword(String id, String password);
}
