package com.example.demo.repository;
import com.example.demo.model.Idregulartime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdRegularTimeRepository extends JpaRepository<Idregulartime, Integer>{
    Idregulartime findByUserid(int userid);
}
