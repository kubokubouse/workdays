package com.example.demo.model;
import javax.persistence.Column;
import lombok.Data;


@Data
public class SuperUserData {
    @Column
	private Integer id;
	
	@Column
	
	private String pass;

	@Column
	
	private String email;

	@Column
	private Integer companyID;
}
