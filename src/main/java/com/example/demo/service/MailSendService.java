package com.example.demo.service;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import com.example.demo.WorkdaysProperties;
import com.example.demo.model.Workdays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
/**
 * 「メール送信」のクラスのサンプルクラス。
 */
@Service
public class MailSendService {

	@Value("${worklist.path}")
    private String path;
  /**
   * 「メール送信」のクラスをDIする。
   */
  @Autowired
  JavaMailSender mailSender;

  @Autowired
  ResourceLoader resourceLoader;

  @Autowired
  HttpSession session;

  

  public void send(String lastname,String mail)  {
	  //ファイル名などに必要なユーザーの名前と当月を取得
	  //User users=(User)session.getAttribute("Data");
	  //String lastname=users.getLastname();
	  Calendar cal = Calendar.getInstance();
	  int year=cal.get(Calendar.YEAR);
	  int month=cal.get(Calendar.MONTH);
	  month=month+1;
	  //String mail=users.getEmail();
	  // メールに添付する「C:\text.txt」にあるファイルのオブジェクトを生成
    String fileName = "勤怠表_"+lastname+"_"+year+"年"+month+"月.xlsx";

    FileSystemResource fileResource = new FileSystemResource(path+fileName);


    // メッセージクラス生成
    MimeMessage mimeMsg = mailSender.createMimeMessage();

    // メッセージ情報をセットするためのヘルパークラスを生成(添付ファイル使用時の第2引数はtrue)
    try{
      MimeMessageHelper helper = new MimeMessageHelper(mimeMsg,true);
      // テンプレートエンジンを使用するための設定インスタンスを生成します。
      ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
      /*
      * テンプレートエンジンの種類を指定します。
      * メールテンプレートとして使用するため、テキストを指定しています。
      */
      templateResolver.setTemplateMode(TemplateMode.TEXT);
      /*
      * テンプレートファイルとして読み込む文字エンコードを指定します。
      * 以下のように指定すると「UTF-8」の文字エンコードなります。
      */
      templateResolver.setCharacterEncoding("UTF-8");

      // テンプレートエンジンを使用するためのインスタンスを生成します。
      SpringTemplateEngine engine = new SpringTemplateEngine();
      engine.setTemplateResolver(templateResolver);

      // メールテンプレートに設定するパラメータを設定します。
      Map<String, Object> variables = new HashMap<>();
      variables.put("name", "てすと氏名");
      variables.put("items", Arrays.asList("りんご", "みかん"));
      variables.put("display", true);

      // テンプレートエンジンを実行してテキストを取得します。
      Context context = new Context();
      context.setVariables(variables);
      // 使用するテンプレートのファイル名とパラメータ情報を設定します。
      String text = engine.process("/mail/sample.txt", context);
      helper.setText(text);


      // 送信元アドレスをセット
      helper.setFrom(WorkdaysProperties.fromMailAdress);//mail
      // 送信先アドレスをセット
      helper.setTo("ryowhite@icloud.com");//y-otsuki@connectcrew.co.jp
      // 表題をセット
      helper.setSubject("勤怠表の提出");

      // 添付ファイルをセット
      helper.addAttachment(fileName, fileResource);

    }
    catch (MessagingException e) {
      throw new MailParseException(e);
    }
    // メール送信
    mailSender.send(mimeMsg);
    System.out.println("メール送れたよ");
  }
  
  public void send(String adress)  {
	  

    // メッセージクラス生成
    MimeMessage mimeMsg = mailSender.createMimeMessage();

    // メッセージ情報をセットするためのヘルパークラスを生成(添付ファイル使用時の第2引数はtrue)
    try{
      MimeMessageHelper helper = new MimeMessageHelper(mimeMsg,true);
      // テンプレートエンジンを使用するための設定インスタンスを生成します。
      ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
      /*
      * テンプレートエンジンの種類を指定します。
      * メールテンプレートとして使用するため、テキストを指定しています。
      */
      templateResolver.setTemplateMode(TemplateMode.TEXT);
      /*
      * テンプレートファイルとして読み込む文字エンコードを指定します。
      * 以下のように指定すると「UTF-8」の文字エンコードなります。
      */
      templateResolver.setCharacterEncoding("UTF-8");

      // テンプレートエンジンを使用するためのインスタンスを生成します。
      SpringTemplateEngine engine = new SpringTemplateEngine();
      engine.setTemplateResolver(templateResolver);

      // メールテンプレートに設定するパラメータを設定します。
      Map<String, Object> variables = new HashMap<>();
      variables.put("name", adress);

      // テンプレートエンジンを実行してテキストを取得します。
      Context context = new Context();
      context.setVariables(variables);
      // 使用するテンプレートのファイル名とパラメータ情報を設定します。
      String text = engine.process("/mail/repass.txt", context);
      helper.setText(text);


      // 送信元アドレスをセット
      helper.setFrom(WorkdaysProperties.fromMailAdress);//mail
      // 送信先アドレスをセット
      helper.setTo(adress);//y-otsuki@connectcrew.co.jp
      // 表題をセット
      helper.setSubject("勤怠表の提出");

      
    }
    catch (MessagingException e) {
      throw new MailParseException(e);
    }
    // メール送信
    mailSender.send(mimeMsg);
    System.out.println("メール送れたよ");
  }

  public void mailsend(String adress,String textpath)  {
	  

    // メッセージクラス生成
    MimeMessage mimeMsg = mailSender.createMimeMessage();

    // メッセージ情報をセットするためのヘルパークラスを生成(添付ファイル使用時の第2引数はtrue)
    try{
      MimeMessageHelper helper = new MimeMessageHelper(mimeMsg,true);
      // テンプレートエンジンを使用するための設定インスタンスを生成します。
      ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
      /*
      * テンプレートエンジンの種類を指定します。
      * メールテンプレートとして使用するため、テキストを指定しています。
      */
      templateResolver.setTemplateMode(TemplateMode.TEXT);
      /*
      * テンプレートファイルとして読み込む文字エンコードを指定します。
      * 以下のように指定すると「UTF-8」の文字エンコードなります。
      */
      templateResolver.setCharacterEncoding("UTF-8");

      // テンプレートエンジンを使用するためのインスタンスを生成します。
      SpringTemplateEngine engine = new SpringTemplateEngine();
      engine.setTemplateResolver(templateResolver);

      // メールテンプレートに設定するパラメータを設定します。
      Map<String, Object> variables = new HashMap<>();
      variables.put("name", adress);

      // テンプレートエンジンを実行してテキストを取得します。
      Context context = new Context();
      context.setVariables(variables);
      // 使用するテンプレートのファイル名とパラメータ情報を設定します。
      String text = engine.process(textpath, context);
      helper.setText(text);


      // 送信元アドレスをセット
      helper.setFrom(WorkdaysProperties.fromMailAdress);//mail
      // 送信先アドレスをセット
      helper.setTo(adress);
      // 表題をセット
      helper.setSubject("ユーザー登録のお知らせ");

      
    }
    catch (MessagingException e) {
      throw new MailParseException(e);
    }
    // メール送信
    mailSender.send(mimeMsg);
    System.out.println("メール送れたよ");
  }

  //メールアドレスかをチェックする
  public String checkMailAddress(String address) {
		boolean result;
		String aText = "[\\w!#%&'/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]";
		String dotAtom = aText + "+" + "(\\." + aText + "+)*";
		String regularExpression = "^" + dotAtom + "@" + dotAtom + "$";
		result = checkMailAddress(address, regularExpression);
		if (result) {
      return null;
    }	
		System.out.println("不正なメールアドレス：" + address);
    return address;
	}

  private static boolean checkMailAddress(String address, String regularExpression) {
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(address);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

}