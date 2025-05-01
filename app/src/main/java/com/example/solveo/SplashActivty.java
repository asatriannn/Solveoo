package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivty extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);

        DbQuery.g_firestore = FirebaseFirestore.getInstance();

        new Handler().postDelayed(() -> {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent;
            if (currentUser != null) {

                intent = new Intent(SplashActivty.this, MainActivity.class);
            } else {

                intent = new Intent(SplashActivty.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
