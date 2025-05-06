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

import com.example.solveo.Adapter.ArticleAdapter;
import com.example.solveo.Models.ArticleModel;

public class ReadFragment extends Fragment {

    private RecyclerView articleView;
    private Dialog dialogProgress;
    private TextView dialogText;

    public ReadFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        articleView = view.findViewById(R.id.articleRecyclerView);
        articleView.setLayoutManager(new LinearLayoutManager(getContext()));

        dialogProgress = new Dialog(requireContext());
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");
        dialogProgress.show();

        DbQuery.loadArticles(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                ArticleAdapter adapter = new ArticleAdapter(requireContext(), DbQuery.g_articleList);
                articleView.setAdapter(adapter);
                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogText.setText("Failed to load articles.");
            }
        });

        return view;
    }
}
