package com.example.demo.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "contract_data")
@IdClass(value=ContractDataKey.class)
public class ContractData
{
	 @Id
	 @Column
	 private int companyID;
	
	 @Column
	  private Date register;

	  @Column
	  private Date startContract;
	  
	 @Column
	 private Date endContract;

	  @Column
	  private Integer limitedUser;

	  @Column
	  private String userRank;

	  @Column
	  private Integer taxInclude;
	  
	 @Column
	 private Integer taxExclude;

	 @Column
	 private String topupContract;
	 
	 @Id
	 @Column
	 private Integer contractID;


}
