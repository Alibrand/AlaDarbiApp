package com.ksacp2022t3.aladarbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    AppCompatButton btn_register;
    TextView txt_login;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_register = findViewById(R.id.btn_register);
        txt_login = findViewById(R.id.txt_login);

        firebaseAuth=FirebaseAuth.getInstance();
        //التحقق من وجود معلومات تسجيل دخول مستخدم
        if(firebaseAuth.getCurrentUser()!=null)
        {
            //اذا وجدنا معلومات مستخدم
            //نتحقق من نوع حساب المستخدم
            //وننتقل الى الصفحة الرئيسية لنوع الحساب إما زبون او صاحب سيارة
            SharedPreferences sharedPreferences=this.getSharedPreferences("aladarbi",MODE_PRIVATE);
            String register_type=sharedPreferences.getString("register_type","");
            Intent intent;
            if(register_type.equals("Client"))
            {
                intent = new Intent(MainActivity.this, ClientHomeActivity.class);
            }
            else{
                intent = new Intent(MainActivity.this, CarOwnerHomeActivity.class);
            }
            startActivity(intent);
            finish();//انهاء الصفحة الحالية

        }


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterTypeActivity. class);
                startActivity(intent);
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity. class);
                startActivity(intent);
            }
        });


    }
}