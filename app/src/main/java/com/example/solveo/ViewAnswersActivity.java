package com.example.solveo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.example.solveo.Models.QuestionModel;

public class ViewAnswersActivity extends AppCompatActivity {

    private LinearLayout answersContainer;
    private Typeface fredokaOne, fredokaLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView titleView = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

// Set dynamic title
        String testTitle = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTitle();
        titleView.setText("Answers – " + testTitle);

// Handle back button
        toolbar.setNavigationOnClickListener(v -> finish());

        answersContainer = findViewById(R.id.answers_container);

        // Load fonts
        fredokaOne = ResourcesCompat.getFont(this, R.font.fredoka_one);
        fredokaLight = ResourcesCompat.getFont(this, R.font.fredoka_light);

        showAnswers();
    }

    private void showAnswers() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (QuestionModel question : DbQuery.g_questionList) {
            View card = inflater.inflate(R.layout.answer_card, answersContainer, false);

            TextView qText = card.findViewById(R.id.question_text);
            TextView optionA = card.findViewById(R.id.option_a);
            TextView optionB = card.findViewById(R.id.option_b);
            TextView optionC = card.findViewById(R.id.option_c);
            TextView optionD = card.findViewById(R.id.option_d);

            qText.setText(question.getQuestion());
            optionA.setText("A. " + question.getOptionA());
            optionB.setText("B. " + question.getOptionB());
            optionC.setText("C. " + question.getOptionC());
            optionD.setText("D. " + question.getOptionD());

            // All options default to light
            optionA.setTypeface(fredokaLight);
            optionB.setTypeface(fredokaLight);
            optionC.setTypeface(fredokaLight);
            optionD.setTypeface(fredokaLight);

            // Set correct one to bold
            switch (question.getCorrectAns()) {
                case 1:
                    optionA.setTypeface(fredokaOne);
                    optionA.setTextColor(Color.parseColor("#20B127")); // green
                    break;
                case 2:
                    optionB.setTypeface(fredokaOne);
                    optionB.setTextColor(Color.parseColor("#20B127"));
                    break;
                case 3:
                    optionC.setTypeface(fredokaOne);
                    optionC.setTextColor(Color.parseColor("#20B127"));
                    break;
                case 4:
                    optionD.setTypeface(fredokaOne);
                    optionD.setTextColor(Color.parseColor("#20B127"));
                    break;
            }


            answersContainer.addView(card);
        }
    }
}
