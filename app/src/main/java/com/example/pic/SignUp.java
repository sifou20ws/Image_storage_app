package com.example.pic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    EditText name , email , pass ;
    Button SignUp;
    TextView SignIn;
    ProgressDialog loadingBar;

    FirebaseAuth mAuth;
    String userUID;

    void ID(){
        name= findViewById(R.id.username_SignUp);
        email= findViewById(R.id.userEmail_SignUp);
        pass = findViewById(R.id.userPass_SignUp);
        SignUp = findViewById(R.id.signUp_signUp);
        SignIn = findViewById(R.id.signIn_signUp);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ID();

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
        SignIn_();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    void CreateAccount(){
        loadingBar.setTitle("create account");
        loadingBar.setMessage("please wait!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        String userName= name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = pass.getText().toString();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"fill the fields", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(userName)){
                name.setError("Name is required!");
                name.requestFocus();
            }
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
        }else {
            loadingBar.setTitle("create account");
            loadingBar.setMessage("please wait!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(userName,userEmail,userPassword);
                            userUID = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userUID).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        addImagesChild();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage()
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }
    }

    void addImagesChild(){
        final DatabaseReference RootRef ;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(userUID).child("Data").exists())){
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("Null","Null");
                    RootRef.child("Users").child(userUID).child("Data").updateChildren(userdataMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, SignIn.class));
                            //Toast.makeText(SignUpPatient.this , "account created" , Toast.LENGTH_SHORT).show();
                            //loadingBar.dismiss();
                        }else{
                            Toast.makeText(SignUp.this , "network error! try again" , Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(SignUp.this , "this"+email+" already exist" , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void SignIn_(){
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this , SignIn.class);
                startActivity(i);
            }
        });
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