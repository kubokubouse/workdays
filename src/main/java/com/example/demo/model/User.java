package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class User
{
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)

	private int id;



	 @Column
	 @NotBlank(message = "苗字を入力してください")
	 @Size(max = 60)
	  private String lastname;

	 @Column
	 @NotBlank(message = "名前を入力してください")
	 @Size(max = 60)
	  private String firstname;

	  @Column
	  @NotBlank(message = "メールアドレスを入力してください")
	  @Email
	  private String email;


	  @Column
	  @NotBlank @NotBlank(message = "パスワードを入力してください")
	  private String password;

	  @Column
	  private String company1;

	  @Column
	  private String company2;
	  @Column
	  private String company3;
	  @Column
	  private Integer banned;

	  
	



}
