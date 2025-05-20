package com.example.solveo;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveo.Adapter.VideoAdapter;
import com.example.solveo.Models.VideoModel;

import java.util.ArrayList;
import java.util.List;

public class WatchFragment extends Fragment {

    private RecyclerView videoView;
    private Dialog dialogProgress;
    private TextView dialogText;
    private EditText etSearch;
    private VideoAdapter adapter;
    private List<VideoModel> allVideos;

    public WatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);

        videoView = view.findViewById(R.id.videoRecyclerView);
        etSearch = view.findViewById(R.id.etSearch);
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
                allVideos = new ArrayList<>(DbQuery.g_videoList);
                adapter = new VideoAdapter(requireContext(), allVideos);
                videoView.setAdapter(adapter);
                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogText.setText("Failed to load videos.");
            }
        });

        // 🔍 Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVideos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterVideos(String query) {
        List<VideoModel> filteredList = new ArrayList<>();
        for (VideoModel video : DbQuery.g_videoList) {
            if (video.getVideoTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(video);
            }
        }
        adapter.filterList(filteredList);
    }
}
