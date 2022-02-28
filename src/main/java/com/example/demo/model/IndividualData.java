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

	  @Column
	  @Email
	  private String mail;

	  @Column
	  private String number;

	  @Column
	  private String name;

	  @Column
	  private Integer banned;

	  @Column
	  private Integer registered;

}
