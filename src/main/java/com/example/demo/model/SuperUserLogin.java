package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "superuser")

public class SuperUserLogin {
	@Id
	@Column(name="id")
	@NotBlank
	private String id;


	@Column(name="pass")
	@NotBlank
	private String pass;
}