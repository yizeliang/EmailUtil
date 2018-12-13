package cn.yzl.emailutil;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.yzl.library.email.MailSendUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MailSendUtil.init("aaa@qq.com","smtp.qq.com","465","pwd");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        }


        findViewById(R.id.aaa)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
