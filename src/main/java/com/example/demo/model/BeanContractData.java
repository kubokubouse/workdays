package com.example.demo.model;

import java.sql.Date;

import javax.persistence.Column;

import lombok.Data;

@Data
public class BeanContractData {
    @Column
    private Integer companyID;
	
	 @Column
	  private Date register;

	  @Column
	  private Date startContract;
	  
	 @Column
	 private Date endContract;

	 @Column
	 private String topupContract;

	  @Column
	  private Integer limitedUser;

	  @Column
	  private String userRank;

	  @Column
	  private Integer taxInclude;
	  
	 @Column
	 private Integer taxExclude;

	 
	 
	 @Column
	 private Integer contractID;
}