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
@Table(name="master_user")
public class MasterUser{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @NotBlank
	 private String id;



	@Column
	@NotBlank
	  private String password;
}
