package com.example.solveo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginbtn;
    TextView fgpassword;
    LinearLayout signup;
    private RelativeLayout googleLogIn;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private Dialog dialogProgress;
    private TextView dialogText;

    private static final int RC_SIGN_IN = 9001;

    @SuppressLint("WrongViewCast")
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
        googleLogIn = findViewById(R.id.google_login);

        mAuth = FirebaseAuth.getInstance();
        DbQuery.g_firestore = FirebaseFirestore.getInstance(); // ✅ Initialize Firestore

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Progress Dialog
        dialogProgress = new Dialog(LoginActivity.this);
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Logging in");

        // Sign up button
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
            finish();
        });

        // Login with email/password
        loginbtn.setOnClickListener(v -> {
            if (validateData()) {
                login();
            }
        });

        // Google login button
        googleLogIn.setOnClickListener(v -> googlelog());
    }

    private boolean validateData() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Enter E-mail address");
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Enter password");
            return false;
        }
        return true;
    }

    private void login() {
        dialogProgress.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    dialogProgress.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void googlelog() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        dialogProgress.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        String email = user.getEmail();
                        String name = user.getDisplayName();

                        // Check if user already exists in Firestore
                        DbQuery.g_firestore.collection("USERS").document(uid)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // User exists → Go to MainActivity
                                        dialogProgress.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        // User doesn't exist → Create user
                                        DbQuery.createUserData(email, name, new MyCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                dialogProgress.dismiss();
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onFailure() {
                                                dialogProgress.dismiss();
                                                Toast.makeText(LoginActivity.this, "Failed to create user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    dialogProgress.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error checking user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        dialogProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
