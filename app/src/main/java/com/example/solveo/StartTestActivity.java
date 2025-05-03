package com.example.solveo;

import static com.example.solveo.DbQuery.g_catList;
import static com.example.solveo.DbQuery.g_selected_test_index;
import static com.example.solveo.DbQuery.g_testList;
import static com.example.solveo.DbQuery.loadquestions;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartTestActivity extends AppCompatActivity {

    private TextView catName, test_title, totalQ, bestScore, time;
    private Button startTestB;
    private ImageView backB;


    private Dialog dialogProgress;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        init();

        dialogProgress = new Dialog(StartTestActivity.this);
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading");

        dialogProgress.show();

        loadquestions(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                setData();
                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogProgress.dismiss();
                Toast.makeText(StartTestActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void init(){
        catName = findViewById(R.id.catName);
        test_title = findViewById(R.id.test_title);
        totalQ = findViewById(R.id.st_total_ques);
        bestScore = findViewById(R.id.st_best_score);
        time = findViewById(R.id.st_time);
        startTestB = findViewById(R.id.start_testB);
        backB = findViewById(R.id.st_backB);

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTestActivity.this.finish();
            }
        });

        startTestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTestActivity.this, QuestionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setData(){
        catName.setText(g_catList.get(DbQuery.g_selected_cat_index).getName());
        test_title.setText(g_testList.get(g_selected_test_index).getTitle());
        totalQ.setText(String.valueOf(DbQuery.g_questionList.size()));
        bestScore.setText(String.valueOf(g_testList.get(g_selected_test_index).getTopScore()));
        time.setText(String.valueOf(g_testList.get(g_selected_test_index).getTime()) + "min");
    }
}