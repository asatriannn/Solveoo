package com.example.solveo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private LinearLayout goToRead, goToWatch, goToTest, goToCollegeBoard;
    private CardView goToLeaderboard;

    public HomeFragment() {
        // Required empty public constructor
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

        // 🧠 INIT
        goToTest = view.findViewById(R.id.gototest);
        goToLeaderboard = view.findViewById(R.id.goToLeaderboard);
        goToCollegeBoard = view.findViewById(R.id.goToCollegeBoard);





        goToTest.setOnClickListener(v -> {
            Fragment current = getParentFragmentManager().findFragmentById(R.id.main_frame);
            if (!(current instanceof TestFragment)) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, new TestFragment());
                transaction.addToBackStack(null);
                transaction.commit();

                ((MainActivity) requireActivity()).setSelectedNavItem(R.id.navigation_test);
            }
        });


        goToLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LeaderboardActivity.class);
            startActivity(intent);
        });

        goToCollegeBoard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.collegeboard.org"));
            startActivity(intent);
        });

        loadUserName();
        return view;
    }

    private void loadUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("USERS")
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
                        tvGreeting.setText("Hi!");
                    });
        } else {
            tvGreeting.setText("Hi!");
        }
    }
}
