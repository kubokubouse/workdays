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
public class Idregulartime{
    @Id
	private Integer userid;
	
	@Column
	private String start;
	
	@Column
	private String end;
	@Column
	private String halftime;
	
	
}
