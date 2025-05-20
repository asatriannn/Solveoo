package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.solveo.Models.LeaderboardUserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private LinearLayout logout_btn, settings, bookmarkBtn;
    private TextView profileName, ranking, meanScore;
    private ImageView profilePic;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleClient = GoogleSignIn.getClient(getContext(), gso);

                mGoogleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        });



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DbQuery.getUserData(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                profileName.setText(DbQuery.myProfile.getName());
                meanScore.setText(String.valueOf(DbQuery.myProfile.getMean_Score()));

                String url = DbQuery.myProfile.getProfileURL();
                if (url != null && !url.isEmpty()) {
                    Glide.with(getContext()).load(url).into(profilePic);
                }

                // 🔹 Now load leaderboard users
                List<LeaderboardUserModel> leaderboardList = new ArrayList<>();

                DbQuery.loadLeaderboardUsers(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        leaderboardList.sort((a, b) -> Integer.compare(b.getMeanScore(), a.getMeanScore()));
                        String currentUserId = FirebaseAuth.getInstance().getUid();

                        for (int i = 0; i < leaderboardList.size(); i++) {
                            LeaderboardUserModel user = leaderboardList.get(i);
                            if (user.getUserID().equals(currentUserId)) {
                                ranking.setText(String.valueOf(i + 1));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        ranking.setText("N/A");
                    }
                }, leaderboardList);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void init(View view) {
        logout_btn = view.findViewById(R.id.logout_btn);
        settings = view.findViewById(R.id.setting_layout);


        profileName = view.findViewById(R.id.profile_name);
        ranking = view.findViewById(R.id.ranking);
        meanScore = view.findViewById(R.id.mean_score);
        profilePic = view.findViewById(R.id.prodile_picture); // your ImageView id is misspelled as "prodile_picture"
    }
}
