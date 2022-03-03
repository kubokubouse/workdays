package com.example.demo.model;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

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
//@Table(name = "super_user")
public class SuperUser{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
	private Integer id;
	
	@Column
	@NotBlank
	private String pass;

	@Column
	@NotBlank
	private String email;

	@Column
	
	private Integer companyID;
}
