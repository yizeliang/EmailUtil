# 发送邮件工具类

[![](https://jitpack.io/v/yizems/EmailUtil.svg)](https://jitpack.io/#yizems/EmailUtil)

```java

 MailSendUtil.init("aaa@qq.com","smtp.qq.com","465","pwd");


 new Thread(){
      @Override
      public void run() {
          super.run();
          try {
              List<File> list = new ArrayList<>();
              list.add(new File("/storage/emulated/0/AAA.log"));
              MailSendUtil.sendEmail(new String[]{"收件人@126.com","抄送@qq.com"},"测试","内容",list);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }.start();

```
