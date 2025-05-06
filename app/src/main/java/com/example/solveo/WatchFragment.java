package com.example.solveo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveo.Adapter.VideoAdapter;

public class WatchFragment extends Fragment {

    private RecyclerView videoView;
    private Dialog dialogProgress;
    private TextView dialogText;

    public WatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);

        videoView = view.findViewById(R.id.videoRecyclerView);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));

        dialogProgress = new Dialog(requireContext());
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading videos...");
        dialogProgress.show();

        DbQuery.loadVideos(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                VideoAdapter adapter = new VideoAdapter(requireContext(), DbQuery.g_videoList);
                videoView.setAdapter(adapter);
                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogText.setText("Failed to load videos.");
            }
        });

        return view;
    }
}
