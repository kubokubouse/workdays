package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.SuperUserLogin;

public interface SuperUserRepository extends JpaRepository<SuperUserLogin, String> {
	SuperUserLogin findByEmailAndPass(String mail, String pass);
}
