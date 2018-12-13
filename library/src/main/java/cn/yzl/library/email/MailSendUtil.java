package cn.yzl.library.email;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


//最近写的qq邮箱发送，也遇到一些问题，163的老是被拦截，后来QQ的成功了。所有分享给大家。
//这个只是发送一些简单文字，附件,图片没有用,项目中使用的比较着急,先这样

public class MailSendUtil {
    private static String myEmailAccount;//自己邮箱的账户
    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
    private static String smtpHost = "smtp.qq.com";
    private static String smtpPort = "465";

    private static String pwd;

    public static void init(String emailAccount, String pwd) {
        MailSendUtil.myEmailAccount = emailAccount;
        MailSendUtil.pwd = pwd;
    }

    public static void init(String emailAccount, String smtpHost, String smtpPort, String pwd) {
        MailSendUtil.myEmailAccount = emailAccount;
        MailSendUtil.smtpHost = smtpHost;
        MailSendUtil.smtpPort = smtpPort;
        MailSendUtil.pwd = pwd;
    }

    public static void sendEmail(String[] recEmail, String subject, String content) throws Exception {
        sendEmail(recEmail, subject, content, null);
    }


    public static void sendEmail(String recEmail[], String subject, String content, List<File> files) throws Exception {
        if (myEmailAccount == null || pwd == null) {
            return;
        }

        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", smtpHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        //开启ssl安全验证
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(BuildConfig.DEBUG);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
        MimeMessage message;
        if (files == null || files.isEmpty()) {
            message = createSampleMessage(session, myEmailAccount, recEmail, subject, content);
        } else {
            message = createFileMessage(session, myEmailAccount, recEmail, subject, content, files);
        }

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        transport.connect(myEmailAccount, pwd);//连接，XXXXXXXX填的是qq邮箱的授权码，登录QQ邮箱，然后在设置里面p0p3/smtp哪一块可以看到，协议必须打开。
        //登录qq邮箱---设置----账户-----然后下面可以看到，然后生成授权码。


        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        //   for(int i=0;i<100;i++){
        transport.sendMessage(message, message.getAllRecipients());//发送出去，那个for循环是我发给我朋友玩的，他直接收到100条
        // }
        // 7. 关闭连接
        transport.close();
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
    }


    /**
     * @param session
     * @param sendMail    发件人邮箱
     * @param receiveMail 收件人邮箱
     * @param content
     * @return
     * @throws Exception
     */
    private static MimeMessage createSampleMessage(Session session, String sendMail,
                                                   String[] receiveMail, String subject, String content) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);


        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, sendMail, "UTF-8"));


        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        addRecipient(message, receiveMail);


        // 4. Subject: 邮件主题
        message.setSubject(subject, "UTF-8");


        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");


        // 6. 设置发件时间
        message.setSentDate(new Date());


        // 7. 保存设置
        message.saveChanges();


        return message;
    }

    private static MimeMessage createFileMessage(Session session, String sendMail,
                                                 String receiveMail[], String subject, String content,
                                                 List<File> files) throws Exception {


        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);


        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, sendMail, "UTF-8"));


        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        addRecipient(message, receiveMail);


        // 4. Subject: 邮件主题
        message.setSubject(subject, "UTF-8");

        Multipart mulPartBody = new MimeMultipart();
        //文字
        MimeBodyPart textContent = new MimeBodyPart();
        textContent.setText(content);
        mulPartBody.addBodyPart(textContent);

        //添加文件
        for (File file : files) {
            if (!file.exists() || !file.isFile()) {
                continue;
            }
            MimeBodyPart temp = new MimeBodyPart();
            DataHandler tempH = new DataHandler(new FileDataSource(file.getAbsoluteFile()));
            temp.setDataHandler(tempH);
            temp.setFileName(MimeUtility.encodeText(file.getName()));
            mulPartBody.addBodyPart(temp);
        }


        //加入到message
        message.setContent(mulPartBody);

        // 6. 设置发件时间
        message.setSentDate(new Date());


        // 7. 保存设置
        message.saveChanges();


        return message;
    }

    private static void addRecipient(MimeMessage message, String[] recMails) throws UnsupportedEncodingException, MessagingException {
        if (recMails != null && recMails.length == 0) {
            return;
        }
        for (int i = 0; i < recMails.length; i++) {
            //第一个为收件人,其他为抄送
            message.setRecipient(i == 0 ? MimeMessage.RecipientType.TO : MimeMessage.RecipientType.CC, new InternetAddress(recMails[i], recMails[i], "UTF-8"));
        }

    }

}