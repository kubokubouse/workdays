package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
/**
 * ユーザー情報
 */
@Data
public class WorkingData implements Serializable {
	@Id
	private int id;
	@Column
	private Integer userid;//integer4
	
	@Column
	private Integer day;


	@Column
	private String  weekday;

	//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]$", message = "〇〇:〇〇の形式で入力してください")
	//@NotEmpty(message = "開始時刻を入力してください")
	@Column
	@DateTimeFormat(pattern="HH:mm")
	private LocalTime start;//time4

	//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "〇〇:〇〇:〇〇の形式で入力してください")
	//@NotEmpty(message = "終了時刻を入力してください")
	@Column
	private LocalTime end;
	//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "〇〇:〇〇:〇〇の形式で入力してください")
	//@NotEmpty(message = "休憩時刻を入力してください")
	@Column
	private LocalTime  halftime;
	//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "〇〇:〇〇:〇〇の形式で入力してください")
	//@NotEmpty(message = "勤務時刻を入力してください")
	@Column
	private LocalTime  worktime;

	@Column
	private String other1;
	@Column
	private String other2;
	@Column
	private String other3;

	@Column
	private Integer holiday;
}