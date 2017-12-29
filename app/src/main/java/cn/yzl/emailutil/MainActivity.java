package cn.yzl.emailutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.yzl.library.email.MailSend;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MailSend.init("1111@qq.com","smtp.qq.com","465","111");
        findViewById(R.id.aaa)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MailSend.sendEmail("111@126.com","测试","内容");
                    }
                });
    }
}
