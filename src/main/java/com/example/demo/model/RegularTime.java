package com.example.demo.model;


import java.sql.Time;

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
public class RegularTime{
    @Id
	private int id;
	
	@Column
	private Time start;
	
	@Column
	private Time end;
	
	@Column
	private Time  halftime;
	
	@Column
	private Time  worktime;
}
