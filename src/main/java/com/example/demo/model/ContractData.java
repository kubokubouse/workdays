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
@Table(name = "contract_data")
public class ContractData
{
	 @Id
	 @Column
	 private int companyID;
	
	 @Column
	  private Date register;

	  @Column
	  private Date start_contract;
	  
	 @Column
	 private Date end_contract;

	  @Column
	  private Integer limited_user;

	  @Column
	  private String user_rank;

	  @Column
	  private Integer tax_include;
	  
	 @Column
	 private Integer tax_exclude;

}
