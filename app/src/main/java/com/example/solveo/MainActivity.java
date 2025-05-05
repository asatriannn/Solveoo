package com.example.solveo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.solveo.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private Dialog dialogProgress;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // 🔹 Initialize Firestore
        DbQuery.g_firestore = FirebaseFirestore.getInstance();

        // 🔹 Setup loading dialog
        dialogProgress = new Dialog(MainActivity.this);
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");
        dialogProgress.show();

        // 🔹 Load categories and user data before showing home screen
        DbQuery.loadData(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                dialogProgress.dismiss();
                replaceFragment(new HomeFragment());
            }

            @Override
            public void onFailure() {
                dialogProgress.dismiss();
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 Handle bottom navigation clicks
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.navigation_read) {
                replaceFragment(new ReadFragment());
            } else if (itemId == R.id.navigation_watch) {
                replaceFragment(new WatchFragment());
            } else if (itemId == R.id.navigation_test) {
                replaceFragment(new TestFragment());
            } else if (itemId == R.id.navigation_profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
