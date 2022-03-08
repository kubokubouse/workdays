package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(value=IndividualDataKey.class)
public class IndividualData
{
	 @Id
	 @Column
	 private int companyID;

	 @Column
	 @NotEmpty
	 private String company1;

	 @Column
	 private String company2;

	 @Column
	 private String company3;

	 @Id
	  @Column
	  @Email
	  private String mail;

	  @Column(name="individual_id")
	  private String number;

	  @Column
	  private String name;

	  @Column
	  private Integer banned;

	  //運用管理用
	  @Column
	  private Integer registered;

	  //画面表示用
	  @Column
	  private String regist;

}
