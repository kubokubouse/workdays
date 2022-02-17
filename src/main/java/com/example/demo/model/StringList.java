package com.example.demo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;



	@Data
	public class StringList implements Serializable {
		@Id
		private int id;

		 private Integer userid;//integer4

		 private String day;



		 private String  weekday;


		 private String start;//time4

		//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "〇〇:〇〇:〇〇の形式で入力してください")
		//@NotEmpty(message = "終了時刻を入力してください")
		@Column
		 private String end;
		//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "〇〇:〇〇:〇〇の形式で入力してください")
		//@NotEmpty(message = "休憩時刻を入力してください")
		@Column
		 private String  halftime;
		//@Pattern(regexp ="^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "〇〇:〇〇:〇〇の形式で入力してください")
		//@NotEmpty(message = "勤務時刻を入力してください")

		 private String  worktime;


		 private String other1;

		 private String other2;

		 private String other3;


}
