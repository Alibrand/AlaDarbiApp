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
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.models.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText txt_first_name,txt_last_name,txt_email,txt_password,txt_confirm_password,txt_phone;
    AppCompatButton btn_register;
    ImageView btn_back;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
         txt_first_name = findViewById(R.id.txt_first_name);
         txt_last_name = findViewById(R.id.txt_last_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        txt_phone = findViewById(R.id.txt_phone);
        btn_register = findViewById(R.id.btn_register);
        btn_back = findViewById(R.id.btn_back);




        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("إنشاء حساب");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        String register_type=getIntent().getStringExtra("register_type");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();
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
                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("الحقل مطلوب");
                     return;
                }
                if(!isValidEmail(str_txt_email))
                {
                    txt_email.setError("البريد الالكتروني غير صالح يجب ان يكون على النحو التالي example@mail.com");
                    return;
                }
                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("الحقل مطلوب");
                    return;
                }
                if(str_txt_password.isEmpty())
                {
                     txt_password.setError("الحقل مطلوب");
                     return;
                }

                if(!isValidPassword(str_txt_password))
                {
                    txt_password.setError("كلمةالمرور ضعيفة ... يجب ان تكون 8 أحرف وأرقام على الاقل مع مزيج من الاحرف الكبيرة والصغيرة");
                    return;
                }

                if(str_txt_confirm_password.isEmpty())
                {
                     txt_confirm_password.setError("الحقل مطلوب");
                     return;
                }
                if(!str_txt_password.equals(str_txt_confirm_password))
                {
                    txt_password.setError("كلمات المرور غير متطابقة");
                    txt_confirm_password.setError("كلمات المرور غير متطابقة");
                    return;
                }

                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(str_txt_email,str_txt_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String uid=authResult.getUser().getUid();

                                Account account=new Account();
                                account.setId(uid);
                                account.setRegister_type(register_type);
                                account.setFirst_name(str_txt_first_name);
                                account.setLast_name(str_txt_last_name);
                                account.setPhone(str_txt_phone);
                                account.setEmail(str_txt_email);

                                firestore.collection("accounts")
                                        .document(uid)
                                        .set(account)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                //الاحتفاظ بمعلومات المستخدم
                                                SharedPreferences sharedPreferences=RegisterActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("register_type",register_type);
                                                editor.putString("user_name",account.getFirst_name()+" "+account.getLast_name());
                                                editor.apply();

                                                //الانتقال الى الصفحة الرئيسية حسب نوع الحساب
                                                Intent intent;
                                                if(account.getRegister_type().equals("Client"))
                                                {
                                                    intent = new Intent(RegisterActivity.this, ClientHomeActivity.
                                                            class);
                                                }
                                                else
                                                {
                                                    intent = new Intent(RegisterActivity.this, CarOwnerHomeActivity.
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



    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}