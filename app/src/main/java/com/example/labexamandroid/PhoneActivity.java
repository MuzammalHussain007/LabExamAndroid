package com.example.labexamandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private EditText phone;
    private TextView mView;
    private Button btn_send ,btn_verify;
    private FirebaseAuth mAuth;
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verification_code,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        setConnection();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoneAuth();

            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                code=phoneAuthCredential.getSmsCode();
                verifyCode(code);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification_code = s;
                Toast.makeText(getApplicationContext(),"Code Send SuccessFully"+s,Toast.LENGTH_SHORT).show();
            }
        };
    }



    private void setPhoneAuth() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + phoneNumber,
                2,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
        Toast.makeText(getApplicationContext(),"Number Send SuccessFully",Toast.LENGTH_SHORT).show();
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code,code);
        signInWithPhoneAuthCredential(credential);

    }


    private void setConnection() {
        phone=findViewById(R.id.phonenumber);
        phoneNumber=phone.getText().toString();
        mView= findViewById(R.id.otp_code);
        btn_send= findViewById(R.id.btn_send);
        btn_verify= findViewById(R.id.btn_verify);
        mAuth=FirebaseAuth.getInstance();



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
              mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful())
                      {
                          FirebaseUser user = task.getResult().getUser();
                          Toast.makeText(getApplicationContext(),""+user.getPhoneNumber(),Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(PhoneActivity.this,HomeActivity.class));
                      }
                      else
                      {
                          Toast.makeText(getApplicationContext(),""+task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                      }

                  }
              });
    }
}