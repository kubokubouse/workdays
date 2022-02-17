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
//@Table(name = "workload")
public class Workdays
{
	@Id
	private int id;
	@Column
	 private Integer userid;//integer4
	@Column
	 private Integer day;
	@Column
	 private Integer year;
	@Column
	 private Integer month;

	@Column
	 private String  weekday;
	@Column
	 private Time start;//time4

	@Column
	 private Time end;

	@Column
	 private Time  halftime;

	@Column
	 private Time  worktime;

	@Column
	 private String other1;

	 @Column
	 private Integer holiday;
	 
	 @Column
	 private String other2;

	 @Column
	 private String other3;
	 

}
