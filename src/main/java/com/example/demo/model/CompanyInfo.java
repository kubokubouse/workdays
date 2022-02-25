package com.example.demo.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "company_info")
public class CompanyInfo
{
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column
	 private int companyID;
	
	 @Column
	  private Date register;

	  @Column
	  private Date startContract;

	 @Column
	  private Date topupContract;
	  
	 @Column
	 private Date endContract;

	 @Column
	 @NotEmpty
	 private String companyName;

	  @Column
	  @Email
	  private String mail;

	  @Column
	  private String person;

	  @Column
	  private String tel;

	  @Column
	  private String homepage;

	  @Column
	  private Integer banned;

	  @Column
	  private Integer limitedUser;

}
