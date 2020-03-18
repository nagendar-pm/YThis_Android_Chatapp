package com.android.example.ythis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginPhoneActivity extends AppCompatActivity {

    MaterialEditText username, phone, otp;
    Button bt_login;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String mVerId;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        FirebaseApp.initializeApp(this);
        userIsLoggedIn();

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register/ Login through Phone");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        userName = username.getText().toString();
        phone = findViewById(R.id.phoneNo);
        otp = findViewById(R.id.otp);
        bt_login = findViewById(R.id.btn_phone_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = username.getText().toString();
                String txt_phone = phone.getText().toString();
                String txt_otp = otp.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(txt_phone)){
                    Toast.makeText(LoginPhoneActivity.this, "All fields are necessary", Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVerification();
                    /*if (mVerId != null){
                        verifyPhoneNumberWithCode();
                        Toast.makeText(LoginPhoneActivity.this, "verId not null", Toast.LENGTH_SHORT).show();
                    }else {
                        startPhoneNumberVerification();
                    }*/
                }
            }
        });



        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Toast.makeText(LoginPhoneActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginPhoneActivity.this, "OTP not sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerId = s;
                bt_login.setText("Verify OTP");
                Toast.makeText(LoginPhoneActivity.this, "OTP on sent", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void verifyPhoneNumberWithCode(){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerId, otp.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String userid = user.getUid();
                    if (user != null){
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Map<String,String> userMap = new HashMap<>();

                                    userMap.put("id",userid);
                                    userMap.put("username",userName);
                                    userMap.put("imageURL", "default");
                                    userMap.put("status", "offline");
                                    userMap.put("search", userName.toLowerCase());

                                    mUserDB.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(LoginPhoneActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(LoginPhoneActivity.this, "in if else of intent", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                userIsLoggedIn();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    //userIsLoggedIn();
                }
            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone.getText().toString(), 60, TimeUnit.SECONDS,this, callbacks
        );
        Toast.makeText(LoginPhoneActivity.this, "in process", Toast.LENGTH_SHORT).show();
    }
}
