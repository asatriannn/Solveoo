package com.example.solveo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.solveo.Models.LeaderboardUserModel;
import com.example.solveo.R;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private List<LeaderboardUserModel> userList;
    private Context context;

    public RankAdapter(Context context, List<LeaderboardUserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardUserModel model = userList.get(position);

        holder.rank.setText(String.valueOf(position + 1));
        holder.name.setText(model.getName());
        holder.score.setText(String.valueOf(model.getMeanScore()));

        Glide.with(context)
                .load(model.getProfileURL())
                .placeholder(R.drawable.profile_picture)
                .into(holder.profilePhoto);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank, name, score;
        ImageView profilePhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank_number);
            name = itemView.findViewById(R.id.rank_name);
            score = itemView.findViewById(R.id.rank_score);
            profilePhoto = itemView.findViewById(R.id.rank_profile_photo);
        }
    }
}
