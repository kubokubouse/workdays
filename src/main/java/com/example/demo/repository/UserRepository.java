package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.User;
public interface UserRepository extends JpaRepository<User, Integer>
{//Stringなのになんで会員登録できるんですかね…

	//@Query(value = "SELECT *from personaldata where email ="+"'"+ "ryowhite@icloud.com"+"'"+ "and password="+"'"+"1211"+"'", nativeQuery = true)
	User findByEmailAndPassword(String email,String password);
	User findById(int id);

}


