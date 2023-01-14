package com.example.pic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {
    Button reset ;
    EditText EmailReset;

    FirebaseAuth auth;

    ProgressDialog loadingBar;

    void id(){
        reset = findViewById(R.id.resetPass);
        EmailReset = findViewById(R.id.userEmail_Reset);
        loadingBar = new ProgressDialog(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        id();

        auth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    void resetPassword(){
        String email = EmailReset.getText().toString().trim();
        if (email.isEmpty()){
            EmailReset.setError("Email is required!");
            EmailReset.requestFocus();
        }
        loadingBar.setTitle("Resetting");
        loadingBar.setMessage("please wait!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                loadingBar.dismiss();
                Toast.makeText(ResetPassword.this, "check your email to reset password. ", Toast.LENGTH_SHORT).show();
            }else{
                loadingBar.dismiss();
                Toast.makeText(ResetPassword.this, "Error: " + task.getException().getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}