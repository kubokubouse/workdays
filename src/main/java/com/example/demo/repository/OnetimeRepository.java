package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Onetime;

public interface OnetimeRepository extends JpaRepository<Onetime, String> {
	Onetime findById(int id);
}
