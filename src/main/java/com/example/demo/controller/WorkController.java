package com.example.demo.controller;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.Holiday;
import com.example.demo.model.User;
import com.example.demo.model.Workdays;
import com.example.demo.model.WorkingListParam;
import com.example.demo.service.HolidayService;
import com.example.demo.service.MailSendService;
import com.example.demo.service.UserService;
import com.example.demo.service.WorkdaysService;
@Controller
public class WorkController
{

    @Autowired
    HttpSession session;

    @Autowired
    UserService userService;
    @Autowired
    WorkdaysService workdaysService;

    @Autowired
    MailSendService mailsendService;
    @Autowired
    HolidayService holidayService;

	@GetMapping("/workdays")

	//userIdが一致かつログインした時の年と月が一致するデータのみを取り出す
	public String workdays(Model model)
	{
		WorkingListParam workingListParam = userService.searchAll();
		workingListParam.setYear(2021);
		workingListParam.setMonth(11);

		model.addAttribute("workingListParam", workingListParam);
		return "list";
	}

/*
	@GetMapping("/beforemonth")

	//userIdが一致かつログインした時の年と前の月のデータを取り出す
	public String beforemonth( Model model)
	{
		WorkingListParam workingListParam = userService.searchlastmonthAll();
		workingListParam.setYear(2021);
		workingListParam.setMonth(11);

		model.addAttribute("workingListParam", workingListParam);
		return "beforeworkdays";

	}

	@GetMapping("/nextmonth")
	//userIdが一致かつログインした時の年と前の月のデータを取り出す
		public String nextmonth(Model model)
		{
			WorkingListParam workingListParam = userService.searchnextmonthAll();
			workingListParam.setYear(2021);
			workingListParam.setMonth(11);

			model.addAttribute("workingListParam", workingListParam);
			return "nextworkdays";
		}



*/

	@GetMapping("/Excel")
	public String Excel( Model model)throws EncryptedDocumentException, IOException
	{

		User users=(User)session.getAttribute("Data");
		model.addAttribute("user",users);


		//苗字と名前合わせてname
		String lastname=users.getLastname();
		String firstname=users.getFirstname();
		String name=lastname+firstname;

		//当日の年月取得
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		String Stringyear=String.valueOf(year);
		String Stringmonth=String.valueOf(month);
		List<Workdays> workdays =workdaysService.findYearMonth(users.getId(),Stringyear,Stringmonth);

		month=month+1; //カレンダーメソッドで取得した月は実際の月-1される（12月だったら11になる的な）ので+1して戻す
		String filename="//LS520De8d/Public/"+lastname+"/勤怠表_"+year+"年"+month+"月.xlsx";
		//ここから既存のファイル消去パート
		Path p0= Paths.get(filename);
		if(p0!=null) {
			try{
		      Files.delete(p0);
		    }catch(IOException e){
		      System.out.println(e);
		    }
		  }

		//ここから実験
		 String INPUT_DIR = "/apache/htdocs/image/test.xlsx";
		 Path p1 = Paths.get(INPUT_DIR);
		 Path p2 = Paths.get(filename);

		 try{
		   Files.copy(p1, p2);
		 }catch(IOException e){
		  e.printStackTrace();
		 }

		  FileInputStream in  = new FileInputStream("//LS520De8d/Public/"+lastname+"/勤怠表_"+year+"年"+month+"月.xlsx");
	        Workbook wb=null;

	        try
	        {
			    // 既存のエクセルファイルを編集する際は、WorkbookFactoryを使用
		        wb = WorkbookFactory.create(in);
		        wb.setSheetName(0, month+"月")  ;
	        } catch (Exception e)
	        {
			    e.printStackTrace();
	        }
	        //名前を所定のセルに記入
	        Sheet sheet = wb.getSheet(month+"月");
	        System.out.println(month);
	        Row namerow = sheet.getRow(4);
	        Cell namecell=namerow.getCell(3);
	        namecell.setCellValue(name);

	 int k=8;
	 int tenhour=0;
	 int hour=0;
	 int minutes=0;
	 for(Workdays workday:workdays)
	 {

		String[]e=
			{
				workday.getDay(),workday.getWeekday(),workday.getStart().toString(),workday.getEnd().toString(),workday.getHalftime().toString(),workday.getWorktime().toString(),workday.getOther(),
			};

		Row row = sheet.getRow(k);
		System.out.println("k="+k);
		System.out.println("row="+row);
		k=k+1;


		//セル単位のループ
		for(int j=0;j<e.length;j++)
		{	System.out.println("j;1="+j+1);
			Cell cell = row.getCell(j+1);
			//その行は何曜日かを明示
			String s=workday.getWeekday();
			//getstart・end・falftime・worktimeだったら→後ろ3つの文字を消す
			if(j==2||j==3||j==4) {
				e[j]=e[j].substring(0,5);

				cell.setCellValue(e[j]);
			}
			//日付は数値として処理
			else if(j==0) {
				int dj=Integer.parseInt(e[j]);
				cell.setCellValue(dj);
			}

			//労働時間の合計時間を出す　労働時間をループで全部出して全部時間に変換して足し算する
			else if(j==5) {

				e[j]=e[j].substring(0,5);
				//セルに埋め込む値は前6文字切り出した時点で確定しておく
				cell.setCellValue(e[j]);

				//一文字目は10時間、二文字目は1時間、3文字目は10分（10分単位でしか計算しない前提だけど実際どうなの）
				int first=Integer.parseInt(e[j].substring(0,1));
				int second=Integer.parseInt(e[j].substring(1,2));
				int third=Integer.parseInt(e[j].substring(3,4));

				tenhour=tenhour+first;
				hour=hour+second;
				minutes=minutes+third;
			}

			else {

			cell.setCellValue(e[j]);
			}

			 //フォント設定
			if(e[j].equals("日"))
			{
			Font font = wb.createFont();
			font.setColor(IndexedColors.RED.index);
			CellStyle cs = wb.createCellStyle();

			cs.setBorderBottom(BorderStyle.HAIR);
		    cs.setFont(font);
		    cell.setCellStyle(cs);
		    }

			if(j==0&&s.equals("日"))
			{
			Font font = wb.createFont();
			font.setColor(IndexedColors.RED.index);
			CellStyle cs = wb.createCellStyle();
			cs.setBorderLeft(BorderStyle.MEDIUM);
			cs.setBorderRight(BorderStyle.HAIR);
			cs.setBorderBottom(BorderStyle.HAIR);
		    cs.setFont(font);
		    cell.setCellStyle(cs);
		    }

		    else if(e[j].equals("月")||e[j].equals("火")||e[j].equals("水")||e[j].equals("木")||e[j].equals("金"))
		    {
		    	Font font = wb.createFont();
				font.setColor(IndexedColors.BLACK.index);
				CellStyle cs = wb.createCellStyle();
				cs.setBorderBottom(BorderStyle.HAIR);
			    cs.setFont(font);
			    cell.setCellStyle(cs);
		    }

		    else if(j==0&&s.equals("月")||j==0&&s.equals("火")||j==0&&s.equals("水")||j==0&&s.equals("木")||j==0&&s.equals("金"))
		    {
		    	Font font = wb.createFont();
				font.setColor(IndexedColors.BLACK.index);
				CellStyle cs = wb.createCellStyle();
				cs.setBorderLeft(BorderStyle.MEDIUM);
				cs.setBorderRight(BorderStyle.HAIR);
				cs.setBorderBottom(BorderStyle.HAIR);
			    cs.setFont(font);
			    cell.setCellStyle(cs);
		    }
		    else if(e[j].equals("土"))
			{
			Font font = wb.createFont();
			font.setColor(IndexedColors.BLUE.index);
			CellStyle cs = wb.createCellStyle();
			cs.setBorderBottom(BorderStyle.HAIR);
		    cs.setFont(font);
		    cell.setCellStyle(cs);
		    }

		    else if(j==0&&s.equals("土"))
			{
			Font font = wb.createFont();
			font.setColor(IndexedColors.BLUE.index);
			CellStyle cs = wb.createCellStyle();
			cs.setBorderLeft(BorderStyle.MEDIUM);
			cs.setBorderRight(BorderStyle.HAIR);
			cs.setBorderBottom(BorderStyle.HAIR);
		    cs.setFont(font);
		    cell.setCellStyle(cs);
		    }


		}

//祝日分の書式設定



		List<Holiday> holidays=holidayService.findyearmonth(year, month);

		for(Holiday holiday:holidays) {


			int holiday2=holiday.getDay();
			holiday2=holiday2+7;
			Row row2 = sheet.getRow(holiday2);
			//日付と曜日を赤くするので申し訳程度のループ　i=1&2
			for(int i=1; i<3; i++)
			{
			Cell cell2 = row2.getCell(i);
			Font font = wb.createFont();
			font.setColor(IndexedColors.RED.index);
			CellStyle cs = wb.createCellStyle();
			if(i==1)
			{
				cs.setBorderLeft(BorderStyle.MEDIUM);
				cs.setBorderRight(BorderStyle.HAIR);
			}
			cs.setBorderBottom(BorderStyle.HAIR);
		    cs.setFont(font);
		    cell2.setCellStyle(cs);
			}
		}

	 }
	//労働時間の合計を出す　tenhour 10時間の合計　hour 1時間の合計 minutes 10分の合計
			hour=hour+tenhour*10;//10時間の合計＊10+1時間の合計で全部で何時間か分かる
			hour=hour+minutes/6;//10分の合計を6で割った数を1時間としてカウント
			minutes=minutes%6;//6で割った余りを10分としてカウント　1なら10分
			int tenminutes=minutes*10;

		String totalhour=	String.valueOf(hour);
		String totalminutes=	String.valueOf(tenminutes);
		if(tenminutes==0) {//6で割って余りがでない場合分数の表記が0になってしまうのでその時は00表記にする
			totalminutes="00";
		}
		String totalworktime=(totalhour+":"+totalminutes);

		Row row = sheet.getRow(3);
		Cell cell = row.getCell(9);
		cell.setCellValue(totalworktime);
	//年月を出す
		String yearmonth=	String.valueOf(year)+"年"+String.valueOf(month)+"月";
		Row row_ym = sheet.getRow(3);
		Cell cell_ym = row_ym.getCell(3);
		cell_ym.setCellValue(yearmonth);

	 FileOutputStream out = null;
	 try
	 {
 	    // 変更するエクセルファイルを指定
 	    out = new FileOutputStream("//LS520De8d/Public/"+lastname+"/勤怠表_"+year+"年"+month+"月.xlsx");

 	    // 書き込み
 	    wb.write(out);
	 }catch(Exception e)
	 {
 		e.printStackTrace();
	 } finally
	 {
 		 out.close();
 		 wb.close();

	 }

		return "done";

}

}
