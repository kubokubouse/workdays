package com.example.demo.model;




import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


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
	@NotNull  (message = "ユーザー上限数を入力してください")
	@Positive
	private Integer limitedUser;

	@Column
	@NotBlank (message = "ランクを入力してください")
	private String userRank;

	@Column
	@NotNull (message = "税込み価格を入力してください")
	private Integer taxInclude;
	
	
	@Column
	@NotNull (message = "税抜き価格を入力してください")
	private Integer taxExclude;

	 
	@Id
	@Column
	private Integer contractID;

	@Column
	private long limitedCapacity;


}
