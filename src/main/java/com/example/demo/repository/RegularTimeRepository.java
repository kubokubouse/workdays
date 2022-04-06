package com.example.demo.repository;
import com.example.demo.model.RegularTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularTimeRepository extends JpaRepository<RegularTime, String>{
    RegularTime findById(int id);
}
