package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name = "super_user")
public class SuperUser{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
	
	private Integer id;
	
	@Column
	@NotBlank(message = "パスワードを入力してください")
	private String pass;

	@Column
	@NotBlank(message = "メールアドレスを入力してください")
	@Email
	private String email;

	@Column
	@NotNull(message = "会社IDを入力してください")
	private Integer companyID;
}
