package com.example.pic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pic.Prevelent.Prevelent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText  email , pass ;
    TextView SignUp , ForgetPassword;
    Button SignIn;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;


    void ID(){
        email= findViewById(R.id.userEmail_SignIn);
        pass = findViewById(R.id.userPass_signIn);
        SignUp = findViewById(R.id.signUp_signIn);
        SignIn = findViewById(R.id.signIn_signIn);
        ForgetPassword = findViewById(R.id.forget_pass);
        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);
        loadingBar = new ProgressDialog(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        ID();

        String UserEmailKey=Paper.book().read(Prevelent.userEmailKey);
        String UserPasswordKey=Paper.book().read(Prevelent.userPasswordKey);
        if (UserEmailKey!="" && UserPasswordKey!=""){
            if (!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPasswordKey)){
                LogIn(UserEmailKey , UserPasswordKey);
            }
        }

        SignUp_();
        ForgetPass();
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = pass.getText().toString();
                LogIn(userEmail , userPassword);
            }
        });
    }

    void SignUp_(){
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this , SignUp.class);
                startActivity(i);
            }
        });
    }
    void ForgetPass(){
        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this , ResetPassword.class);
                startActivity(i);
            }
        });
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void LogIn(String userEmail , String userPassword){
        loadingBar.setTitle("Login in");
        loadingBar.setMessage("please wait!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"fill the fields", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(userEmail)) {
                email.setError("Email is required!");
                email.requestFocus();
            }
            if (TextUtils.isEmpty(userPassword)) {
                pass.setError("Password is required!");
                pass.requestFocus();
            }
        } else if (!isEmailValid(userEmail)) {
            email.setError("Please provide a valid Email!");
            Toast.makeText(this, "Wrong Email", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Paper.book().write(Prevelent.userEmailKey,userEmail );
                        Paper.book().write(Prevelent.userPasswordKey,userPassword );
                        Toast.makeText(SignIn.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                    }else{
                        Toast.makeText(SignIn.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    int count=0;
    private long pressedTime;
    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}