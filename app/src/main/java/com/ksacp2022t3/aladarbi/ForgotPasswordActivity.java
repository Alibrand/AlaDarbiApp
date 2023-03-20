package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ForgotPasswordActivity extends AppCompatActivity {

    AppCompatButton btn_send;
    EditText txt_email;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btn_send = findViewById(R.id.btn_send);
        txt_email = findViewById(R.id.txt_email);
        btn_back = findViewById(R.id.btn_back);

        firebaseAuth=FirebaseAuth.getInstance();



        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("جاري الارسال");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();

                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("الحقل مطلوب");
                     return;
                }

                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(str_txt_email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(ForgotPasswordActivity.this,"تم ارسال رابط الى بريدك لإعادة تعيين كلمة المرور" , LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                translate_errors(e);
                            }
                        });




            }
        });


    }

    private void translate_errors(Exception error){
        try {
            throw error;
        } catch(FirebaseAuthWeakPasswordException e) {
            makeText(this,"كلمة المرور ضعيفة يرجى اختيار كلمة اقوى", LENGTH_LONG).show();

        } catch(FirebaseAuthInvalidCredentialsException e) {
            makeText(this,"كلمة المرور خطأ", LENGTH_LONG).show();
        }
        catch( FirebaseAuthInvalidUserException e) {
            makeText(this,"البريد الالكتروني غير موجود في النظام", LENGTH_LONG).show();
        }
        catch( FirebaseAuthUserCollisionException e) {
            makeText(this,"يوجد حساب آخر يستخدم البريد الالكتروني ذاته", LENGTH_LONG).show();
        }
        catch( FirebaseAuthEmailException e) {
            makeText(this,"البريد الالكتروني مكتوب بصيغة غير صحيحة", LENGTH_LONG).show();
        } catch(Exception e) {
            makeText(this,"حدث خطأ"+" "+error.getMessage(), LENGTH_LONG).show();

        }
    }
}