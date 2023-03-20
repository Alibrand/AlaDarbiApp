package com.ksacp2022t3.aladarbi;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.models.Account;

public class LoginActivity extends AppCompatActivity {

    EditText txt_email,txt_password;
    TextView txt_forgot_password;
    AppCompatButton btn_login;
    ImageView btn_back;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_login = findViewById(R.id.btn_login);
        btn_back = findViewById(R.id.btn_back);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);



        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("تسجيل الدخول");
        progressDialog.setMessage("الرجاء الانتظار");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity. class);
                startActivity(intent);
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();

                if(str_txt_email.isEmpty())
                {
                    txt_email.setError("الحقل مطلوب");
                    return;
                }

                if(str_txt_password.isEmpty())
                {
                    txt_password.setError("الحقل مطلوب");
                    return;
                }



                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(str_txt_email,str_txt_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String uid=authResult.getUser().getUid();
                                //نبحث عن بيانات المستخدم في فاير ستور من خلال رقم المستخدم الخاص
                                firestore.collection("accounts")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                progressDialog.dismiss();
                                                Account account=documentSnapshot.toObject(Account.class);
                                                //الاحتفاظ بمعلومات المستخدم
                                                SharedPreferences sharedPreferences=LoginActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("register_type",account.getRegister_type());
                                                editor.putString("user_name",account.getFirst_name()+" "+account.getLast_name());
                                                editor.apply();

                                                //الانتقال الى الصفحة الرئيسية حسب نوع الحساب
                                                Intent intent;
                                                if(account.getRegister_type().equals("Client"))
                                                {
                                                    intent = new Intent(LoginActivity.this, ClientHomeActivity.
                                                            class);
                                                }
                                                else
                                                {
                                                    intent = new Intent(LoginActivity.this, CarOwnerHomeActivity.
                                                            class);
                                                }
                                                //اغلاق جميع الصفحات السابقة والانتقال الى الصفحة الرئيسية
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                translate_errors(e);
                                            }
                                        });

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
            Toast.makeText(this,"كلمة المرور ضعيفة يرجى اختيار كلمة اقوى", Toast.LENGTH_LONG).show();

        } catch(FirebaseAuthInvalidCredentialsException e) {
            Toast.makeText(this,"كلمة المرور خطأ", Toast.LENGTH_LONG).show();
        }
        catch( FirebaseAuthInvalidUserException e) {
            Toast.makeText(this,"البريد الالكتروني غير موجود في النظام", Toast.LENGTH_LONG).show();
        }
        catch( FirebaseAuthUserCollisionException e) {
            Toast.makeText(this,"يوجد حساب آخر يستخدم البريد الالكتروني ذاته", Toast.LENGTH_LONG).show();
        }
        catch( FirebaseAuthEmailException e) {
            Toast.makeText(this,"البريد الالكتروني مكتوب بصيغة غير صحيحة", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(this,"حدث خطأ"+" "+error.getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}