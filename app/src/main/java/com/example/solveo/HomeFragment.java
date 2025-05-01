package com.example.solveo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);

        loadUserName();

        return view;
    }

    private void loadUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("USERS")  // Assuming your collection is called 'users'
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("Name");
                            if (name != null) {
                                tvGreeting.setText("Hi " + name + "!");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Optional: handle error
                        tvGreeting.setText("Hi!");
                    });
        } else {
            tvGreeting.setText("Hi!");
        }
    }
}
