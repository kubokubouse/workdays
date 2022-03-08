package com.example.demo.model;




import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contract_data")
@IdClass(value=ContractDataKey.class)
public class ContractData
{
	/*@Id
	@Column
	
	private int id;*/
	@Id

	@Column
	@NotNull(message = "会社IDを入力してください")
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

	 
	@Id
	@Column
	private Integer contractID;


}
