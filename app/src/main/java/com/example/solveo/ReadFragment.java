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

import com.example.solveo.Adapter.ArticleAdapter;
import com.example.solveo.Models.ArticleModel;

import java.util.ArrayList;
import java.util.List;

public class ReadFragment extends Fragment {

    private RecyclerView articleView;
    private Dialog dialogProgress;
    private TextView dialogText;
    private EditText etSearch;
    private ArticleAdapter adapter;
    private List<ArticleModel> allArticles;

    public ReadFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        articleView = view.findViewById(R.id.articleRecyclerView);
        etSearch = view.findViewById(R.id.etSearch);
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
                allArticles = new ArrayList<>(DbQuery.g_articleList);
                adapter = new ArticleAdapter(requireContext(), allArticles);
                articleView.setAdapter(adapter);
                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogText.setText("Failed to load articles.");
            }
        });

        // 🔍 Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArticles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterArticles(String query) {
        List<ArticleModel> filteredList = new ArrayList<>();
        for (ArticleModel article : DbQuery.g_articleList) {
            if (article.getArticleTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(article);
            }
        }
        adapter.filterList(filteredList);
    }
}
