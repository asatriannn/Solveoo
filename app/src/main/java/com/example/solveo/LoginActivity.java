package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginbtn;
    TextView fgpassword;
    LinearLayout signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        loginbtn = findViewById(R.id.loginbtn);
        fgpassword = findViewById(R.id.forget);
        signup = findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(validateData()){
                    login();
                }
            }
        });

    }

    private boolean validateData(){
        boolean status = false;
        if (email.getText().toString().isEmpty()){
            email.setError("Enter E-mail address");
            return false;
        }
        if (password.getText().toString().isEmpty()){
            password.setError("Enter password");
            return false;
        }
        return true;
    }

    private void login() {

    }
}