package com.example.solveo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private GridView testView;
    private Toolbar toolbar;
    private TextView titleView;
    private TestAdapter adapter;
    private Dialog dialogProgress;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        titleView = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Use custom TextView
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // Enable back arrow
        }

        // Set title
        titleView.setText(DbQuery.g_catList.get(DbQuery.g_selected_cat_index).getName());

        // Back arrow click
        toolbar.setNavigationOnClickListener(v -> finish());

        // GridView setup
        testView = findViewById(R.id.test_grid_view);


        dialogProgress = new Dialog(TestActivity.this);
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading");


        dialogProgress.show();
        //loadTestData();

        DbQuery.loadTestData(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                TestAdapter adapter = new TestAdapter(TestActivity.this, DbQuery.g_testList);
                testView.setAdapter(adapter);

                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogProgress.dismiss();
                Toast.makeText(TestActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }


}
