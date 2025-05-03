package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class TestFragment extends Fragment {

    private LinearLayout catContainer;
    private TextView loadingText;
    private boolean categoriesDisplayed = false;

    public TestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        catContainer = view.findViewById(R.id.testCaregoryView);
        loadingText = view.findViewById(R.id.loadingText);

        if (DbQuery.g_catList.isEmpty()) {
            // Show loading
            loadingText.setVisibility(View.VISIBLE);
            catContainer.setVisibility(View.GONE);

            DbQuery.loadCategories(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        loadingText.setVisibility(View.GONE);
                        catContainer.setVisibility(View.VISIBLE);
                        displayCategories();
                    }
                }

                @Override
                public void onFailure() {
                    if (isAdded()) {
                        loadingText.setText("Failed to load categories.");
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
                //intent.putExtra("CAT_INDEX", index);
                startActivity(intent);
            });
        }
    }
}
