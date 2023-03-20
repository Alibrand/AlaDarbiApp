package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.models.Account;

public class UpdateAccountActivity extends AppCompatActivity {
    EditText txt_first_name,txt_last_name, txt_phone;
    AppCompatButton btn_save;
    ImageView btn_back;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);

        txt_phone = findViewById(R.id.txt_phone);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("تحميل");
        progressDialog.setMessage("الرجاء الانتظار");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog.show();
        firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                                .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                progressDialog.dismiss();
                                                Account account=documentSnapshot.toObject(Account.class);
                                                txt_phone.setText(account.getPhone());
                                                txt_first_name.setText(account.getFirst_name());
                                                txt_last_name.setText(account.getLast_name());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(UpdateAccountActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();

                String str_txt_phone =txt_phone.getText().toString();

                if(str_txt_first_name.isEmpty())
                {
                    txt_first_name.setError("الحقل مطلوب");
                    return;
                }
                if(str_txt_last_name.isEmpty())
                {
                    txt_last_name.setError("الحقل مطلوب");
                    return;
                }

                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("الحقل مطلوب");
                    return;
                }


                progressDialog.show();


                                firestore.collection("accounts")
                                        .document(firebaseAuth.getUid())
                                        .update("first_name",str_txt_first_name,
                                                "last_name",str_txt_last_name,
                                                "phone",str_txt_phone)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                SharedPreferences sharedPreferences=UpdateAccountActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("full_name",str_txt_first_name+" "+str_txt_last_name);
                                                editor.apply();
                                                makeText(UpdateAccountActivity.this,"تم تحديث المعلومات بنجاح" , LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(UpdateAccountActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });







            }
        });

    }
}