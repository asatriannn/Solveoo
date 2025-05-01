package com.example.solveo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView; // ✅ Changed from org.w3c.dom.Text

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText email, name, password, confpassword;
    Button signupbtn;
    LinearLayout login;

    private FirebaseAuth mAuth;

    private String emailStr, passStr, confPassStr, nameStr;

    private Dialog dialogProgress;
    private TextView dialogText; // ✅ Fixed type to TextView

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize UI elements
        email = findViewById(R.id.Email);
        name = findViewById(R.id.FullName);
        password = findViewById(R.id.Password);
        confpassword = findViewById(R.id.ConfPassword);
        signupbtn = findViewById(R.id.signupbtn);
        login = findViewById(R.id.login);

        // Setup dialog
        dialogProgress = new Dialog(SignupActivity.this);
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Registering user..."); // ✅ Changed from setTextContent()

        mAuth = FirebaseAuth.getInstance();

        // Go back to login
        login.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Sign up user
        signupbtn.setOnClickListener(view -> {
            if (validate()) {
                signupNewUser();
            }
        });
    }

    private boolean validate() {
        nameStr = name.getText().toString().trim();
        emailStr = email.getText().toString().trim();
        passStr = password.getText().toString().trim();
        confPassStr = confpassword.getText().toString().trim();

        if (nameStr.isEmpty()) {
            name.setError("Enter your name");
            return false;
        }

        if (emailStr.isEmpty()) {
            email.setError("Enter your E-mail address");
            return false;
        }

        if (passStr.isEmpty()) {
            password.setError("Enter your password");
            return false;
        }

        if (confPassStr.isEmpty()) {
            confpassword.setError("Confirm your password");
            return false;
        }

        if (!passStr.equals(confPassStr)) {
            Toast.makeText(SignupActivity.this, "Passwords must be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void signupNewUser() {
        dialogProgress.show();
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, task -> {
                    dialogProgress.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "SignUp successful", Toast.LENGTH_SHORT).show();

                        DbQuery.createUserData(emailStr, nameStr, new MyCompleteListener(){

                            @Override
                            public void onSuccess() {
                                dialogProgress.dismiss();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure() {
                                dialogProgress.dismiss();
                                Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            }
                        });


                    } else {
                        Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
