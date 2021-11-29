package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workload")
public class Another
{
	@Id
	private int id;
	@Column
	 private Integer userid;//integer4
	@Column
	 private String day;
	@Column
	 private String year;
	@Column
	 private String month;

	@Column
	 private String  weekday;
	@Column
	 private String start;//time4

	@Column
	 private String end;

	@Column
	 private String  halftime;

	@Column
	 private String  worktime;

	@Column
	 private String other;

}
