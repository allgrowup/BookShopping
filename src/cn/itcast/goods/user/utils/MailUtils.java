package cn.itcast.goods.user.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils {
    /**
     * 发送邮件
     * @param receiveMailAccount  收件人的邮箱
     * @param subject  邮件主题
     * @param content  邮件内容
     * @param ownEmailAccount  发件人的邮箱
     * @param ownEmailPassword 发送邮件的密码------》授权码
     */
    public static void send(String receiveMailAccount,String subject,String content,String ownEmailAccount,String ownEmailPassword){
    	Transport trans = null;
    	//String ownEmailAccount = "m15259070752@163.com";
        // 发送邮件的密码------》授权码
        //String ownEmailPassword = "zwy352167";
        // 发送邮件的smtp 服务器 地址
        String myEmailSMTPHost = "smtp.163.com";
        // 发送邮件对方的邮箱
       // String receiveMailAccount = "18039059139@163.com";
        Properties prop = new Properties();
        // 设置邮件传输采用的协议smtp
        prop.setProperty("mail.transport.protocol", "smtp");
        // 设置发送人邮件服务器的smtp地址
        // 这里以网易的邮箱smtp服务器地址为例
        prop.setProperty("mail.smtp.host", myEmailSMTPHost);
        // 设置验证机制
        prop.setProperty("mail.smtp.auth", "true");
        // 创建对象回话跟服务器交互
        Session session = Session.getInstance(prop);
        // 会话采用debug模式
        session.setDebug(true);
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 设置发送邮件地址,param1 代表发送地址 param2 代表发送的名称(任意的) param3 代表名称编码方式
        try{
        	 message.setFrom(new InternetAddress(ownEmailAccount, "utf-8"));
             // 代表收件人
             message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMailAccount, "utf-8"));
             // To: 增加收件人（可选）
             /*message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));
             // Cc: 抄送（可选）
             message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));
             // Bcc: 密送（可选）
             message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));*/
             // 设置邮件主题
             message.setSubject(subject);
             // 设置邮件内容
             message.setContent(content, "text/html;charset=utf-8");
             // 设置发送时间
             message.setSentDate(new Date());
             trans = session.getTransport();
             // 链接邮件服务器
             trans.connect(ownEmailAccount, ownEmailPassword);
             // 发送信息
             trans.sendMessage(message, message.getAllRecipients());
        }catch(Exception e){
        	throw new RuntimeException(e);
        }finally{
            try {
                // 关闭链接
				trans.close();
			} catch (MessagingException e){
				e.printStackTrace();
			}
        }
    }    
}
