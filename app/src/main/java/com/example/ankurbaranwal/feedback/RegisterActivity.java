package com.example.ankurbaranwal.feedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    Button signup,sendotp;
    EditText username,password,mobile,otp;
    FirebaseAuth mAuth;
    String name,pass,mob;
    String codeSent;
    ProgressDialog loadingbar;
    AdView adView;

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){

                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MobileAds.initialize(RegisterActivity.this,"ca-app-pub-9044775629101422~7585002201");
        adView =(AdView)findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        loadingbar = new ProgressDialog(this);

        FirebaseApp.initializeApp(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();

        signup = (Button)findViewById(R.id.signup);

        username =(EditText)findViewById(R.id.username);
        password =(EditText)findViewById(R.id.setpassword);
        mobile = (EditText)findViewById(R.id.mobileno);
        otp= (EditText)findViewById(R.id.otp);
        sendotp =(Button)findViewById(R.id.otpbutton);


        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = username.getText().toString();
                pass = password.getText().toString();
                if (TextUtils.isEmpty(name))
                {
                    Toast.makeText(RegisterActivity.this, "Name is Mandatory..", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(pass))
                {
                    Toast.makeText(RegisterActivity.this, "Set your password", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    sentcode();
                    otp.setVisibility(View.VISIBLE);
                    loadingbar.setTitle("Please Wait !");
                    loadingbar.setMessage("OTP is Sending...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                }
//                saveUserInformation(name, mob, pass);
//                mAuth.getUid();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp.getText().toString();

                verifyVerificationCode(code);

            }
        });
    }

    private void saveUserInformation(final String name,final String mob,final String pass)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("User").child(mob).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",mob);
                    userdataMap.put("password",pass);
                    userdataMap.put("name",name);

                    RootRef.child("User").child(mob).updateChildren(userdataMap);
            }
            else
                {
                    Toast.makeText(RegisterActivity.this,"This "+ mob +" already exists.",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    //Toast.makeText(RegisterActivity.this,"Please try again using another phone number.",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        }


    private void sentcode()
    {
        mob = mobile.getText().toString();
        if (mob.isEmpty())
        {
            mobile.setError("Mobile no is required");
            mobile.requestFocus();
            return;
        }
        if (mob.length()<10)
        {
            mobile.setError("Please enter valid phone no.");
            mobile.requestFocus();
            return;
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" +mob,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();


            if (code != null) {
                otp.setText(code);

            }
         }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent  = s;
            loadingbar.dismiss();
        }
    };

    private void verifyVerificationCode(String code)
    {
        loadingbar.setTitle("Please Wait!!");
        loadingbar.setMessage("Checking the Code...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity

                            saveUserInformation(name, mob, pass);
                            mAuth.getUid();
                            loadingbar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Login Successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            loadingbar.dismiss();
                            Toast.makeText(RegisterActivity.this, "OTP is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
