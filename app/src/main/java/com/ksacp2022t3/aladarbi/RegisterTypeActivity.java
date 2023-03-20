package com.ksacp2022t3.aladarbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegisterTypeActivity extends AppCompatActivity {
    AppCompatButton btn_register_client,btn_register_owner;
    TextView txt_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_type);
        btn_register_client = findViewById(R.id.btn_register_client);
        btn_register_owner = findViewById(R.id.btn_register_owner);
        txt_login = findViewById(R.id.txt_login);

        btn_register_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterTypeActivity.this,RegisterActivity
                        . class);
                intent.putExtra("register_type","Client");
                startActivity(intent);
            }
        });

        btn_register_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterTypeActivity.this,RegisterActivity
                        . class);
                intent.putExtra("register_type","Car Owner");
                startActivity(intent);
            }
        });

        txt_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RegisterTypeActivity.this,LoginActivity. class);
                        startActivity(intent);
                    }
                }
        );


    }
}