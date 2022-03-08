package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Holiday
{
	 @Id


	private int id;



	 @Column
	  private Integer year;



	  @Column
	  private Integer month;


	  @Column
	  private Integer day;

}
