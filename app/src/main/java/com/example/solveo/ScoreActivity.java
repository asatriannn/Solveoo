package com.example.solveo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.encoders.annotations.Encodable;

import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTV, timeTV, totalQTV, correctQ, wrongQ, unattemptedQ;
    private Button leaderB, retakeB, viewAnswerBtn;
    private long timeTaken;
    private Toolbar toolbar;
    private Dialog dialogProgress;
    private TextView dialogText;
    int finalScore = 800;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Use custom TextView
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // Enable back arrow
        }

        // Set title

        // Back arrow click
        toolbar.setNavigationOnClickListener(v -> finish());

        dialogProgress = new Dialog(ScoreActivity.this);
        dialogProgress.setContentView(R.layout.dialog_layout);
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = dialogProgress.findViewById(R.id.dialog_text);
        dialogText.setText("Loading");


        init();

        loadData();

        viewAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        retakeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reTake();
            }
        });

        saveResults();

    }

    private void init(){
        scoreTV = findViewById(R.id.final_score);
        timeTV = findViewById(R.id.time_taken);
        totalQTV = findViewById(R.id.ques_number);
        correctQ = findViewById(R.id.correct_answers);
        wrongQ = findViewById(R.id.wrong_answers);
        unattemptedQ = findViewById(R.id.unattempted);
        leaderB = findViewById(R.id.leaderboardBTN);
        retakeB = findViewById(R.id.retake);
        viewAnswerBtn = findViewById(R.id.view_answers);
    }

    private void loadData(){
        int correctQuestions = 0, wrongQuestions = 0, unattemptedQuestions = 0;


        int pointPerWrong = 0;

        for(int i = 0; i< DbQuery.g_questionList.size(); i++){
            if(DbQuery.g_questionList.get(i).getSelcetedAns() == -1){
                unattemptedQuestions ++;
            }
            else{
                if(DbQuery.g_questionList.get(i).getSelcetedAns() == DbQuery.g_questionList.get(i).getCorrectAns()){
                    correctQuestions++;
                }
                else{
                    wrongQuestions++;
                }
            }

        }

        correctQ.setText(String.valueOf(correctQuestions));
        wrongQ.setText(String.valueOf(wrongQuestions));
        unattemptedQ.setText(String.valueOf(unattemptedQuestions));

        totalQTV.setText(String.valueOf(DbQuery.g_questionList.size()));

        String categoryName = DbQuery.g_catList.get(DbQuery.g_selected_cat_index).getName();

        if(categoryName.equalsIgnoreCase("Module1")){
            pointPerWrong = 30;
        }
        else{
            pointPerWrong = 10;
        }

        finalScore = 800 - (wrongQuestions + unattemptedQuestions)*pointPerWrong;

        scoreTV.setText(String.valueOf(finalScore));

        timeTaken = getIntent().getLongExtra("TIME TAKEN", 0);

        String time = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) % 60);


        timeTV.setText(time);


    }

    private void reTake(){
       for(int i = 0; i<DbQuery.g_questionList.size(); i++){
           DbQuery.g_questionList.get(i).setSelcetedAns(-1);
           DbQuery.g_questionList.get(i).setStatus(DbQuery.NOT_VISITED);
       }

       Intent intent = new Intent(ScoreActivity.this, StartTestActivity.class);
       startActivity(intent);
       finish();


    }

    private void saveResults(){


        DbQuery.saveResults(finalScore, new MyCompleteListener() {
            @Override
            public void onSuccess() {

                dialogProgress.dismiss();
            }

            @Override
            public void onFailure() {
                dialogProgress.dismiss();
                Toast.makeText(ScoreActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}