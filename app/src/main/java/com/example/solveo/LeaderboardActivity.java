package com.example.solveo;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.solveo.Adapters.RankAdapter;
import com.example.solveo.Models.LeaderboardUserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private TextView totalUser, myRank, myScore, myName;
    private RecyclerView usersView;
    private ImageView myProfilePic;

    private List<LeaderboardUserModel> leaderboardList = new ArrayList<>();
    private RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        init();

        usersView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RankAdapter(this, leaderboardList);
        usersView.setAdapter(adapter);

        DbQuery.loadLeaderboardUsers(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                // Sort descending by mean score
                Collections.sort(leaderboardList, Comparator.comparingInt(LeaderboardUserModel::getMeanScore).reversed());
                adapter.notifyDataSetChanged();

                totalUser.setText("Total Users: " + leaderboardList.size());

                String currentUserId = FirebaseAuth.getInstance().getUid();

                for (int i = 0; i < leaderboardList.size(); i++) {
                    LeaderboardUserModel user = leaderboardList.get(i);
                    if (user.getUserID().equals(currentUserId)) {
                        myRank.setText(String.valueOf(i + 1));
                        myName.setText("You: " + user.getName());
                        myScore.setText(String.valueOf(user.getMeanScore()));

                        Glide.with(LeaderboardActivity.this)
                                .load(user.getProfileURL())
                                .placeholder(R.drawable.profile_picture)
                                .into(myProfilePic);
                        break;
                    }
                }
            }

            @Override
            public void onFailure() {
                totalUser.setText("Failed to load leaderboard");
            }
        }, leaderboardList);
    }

    private void init() {
        totalUser = findViewById(R.id.lb_totalUsers);
        myRank = findViewById(R.id.myRank);
        myScore = findViewById(R.id.myMeanScore);
        myName = findViewById(R.id.myName);
        usersView = findViewById(R.id.userView);
        myProfilePic = findViewById(R.id.myProfilePhoto);
    }
}
