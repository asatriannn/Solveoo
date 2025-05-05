package com.example.solveo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.solveo.Models.CategoryModel;

public class TestFragment extends Fragment {

    private LinearLayout catContainer;
    private boolean categoriesDisplayed = false;

    private Dialog dialogProgress;
    private TextView dialogText;

    public TestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        catContainer = view.findViewById(R.id.testCaregoryView);

        // Initialize dialog
        dialogProgress = new Dialog(requireContext());
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        if (DbQuery.g_catList.isEmpty()) {
            dialogProgress.show();

            DbQuery.loadCategories(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        dialogProgress.dismiss();
                        displayCategories();
                    }
                }

                @Override
                public void onFailure() {
                    if (isAdded()) {
                        dialogProgress.dismiss();
                        Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            displayCategories();
        }

        return view;
    }

    private void displayCategories() {
        if (categoriesDisplayed) return;
        categoriesDisplayed = true;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        catContainer.removeAllViews();

        for (CategoryModel model : DbQuery.g_catList) {
            View itemView = inflater.inflate(R.layout.cat_item_layout, catContainer, false);
            TextView catName = itemView.findViewById(R.id.CatNameText);
            catName.setText(model.getName());
            catContainer.addView(itemView);

            itemView.setOnClickListener(v -> {
                int index = DbQuery.g_catList.indexOf(model);
                DbQuery.g_selected_cat_index = index;
                Intent intent = new Intent(getContext(), TestActivity.class);
                startActivity(intent);
            });
        }
    }
}
