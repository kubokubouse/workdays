package com.example.demo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Data
public class UserData implements Serializable{
    @Id
	private int id;
	
	@Column
	private String lastname;

	@Column
	private String firstname;

	@Column
	private String email;


	@Column
	private String password;

	@Column
	private String company1;

	@Column
	private String company2;
	@Column
	private String company3;


}
