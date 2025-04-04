package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    EditText email, name, password, confpassword;
    Button signupbtn;
    LinearLayout login;
    private FirebaseAuth mAuth;
    private String emailStr, passStr, confPassStr, nameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.Email);
        name = findViewById(R.id.FullName);
        password = findViewById(R.id.Password);
        confpassword = findViewById(R.id.ConfPassword);
        signupbtn = findViewById(R.id.signupbtn);
        login = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()){

                    signupNewUser();
                }
            }
        });
    }

    private boolean validate(){
        nameStr = name.getText().toString().trim();
        passStr = password.getText().toString().trim();
        emailStr = email.getText().toString().trim();
        confPassStr = confpassword.getText().toString().trim();

        if(nameStr.isEmpty()){
            name.setError("Enter your name");
            return false;
        }

        if(emailStr.isEmpty()){
            email.setError("Enter your E-mail address");
            return false;
        }

        if(passStr.isEmpty()){
            password.setError("Enter your password");
            return false;
        }
        if(confPassStr.isEmpty()){
            confpassword.setError("Enter your password");
            return false;
        }

        if(passStr.compareTo(confPassStr) != 0){
            Toast.makeText(SignupActivity.this, "Passwords must be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void signupNewUser(){
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "SignUp succesfull", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            SignupActivity.this.finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}