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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
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
	private String topupContract;

	@Column
	@NotEmpty(message = "会社名を入力してください")
	private String companyName;

	@Column
	@Email
	@NotEmpty(message = "メールアドレスを入力してください")
	private String mail;

	@Column
	@NotEmpty(message = "担当者名を入力してください")
	private String person;

	@Column
	@Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "電話番号の形式で入力してください")
	@NotEmpty(message = "電話番号を入力してください")
	private String tel;

	
	@Column
	private String homepage;

	//@PositiveOrZero
	@Column
	private Integer banned;

	@Column
	@Positive
	@NotNull(message = "上限ユーザー数を入力してください")
	private Integer limitedUser;

}
