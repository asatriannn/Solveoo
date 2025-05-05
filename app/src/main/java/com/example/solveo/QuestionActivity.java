package com.example.solveo;

import static com.example.solveo.DbQuery.ANSWERED;
import static com.example.solveo.DbQuery.NOT_ANSWERED;
import static com.example.solveo.DbQuery.NOT_VISITED;
import static com.example.solveo.DbQuery.REVIEW;
import static com.example.solveo.DbQuery.g_questionList;
import static com.example.solveo.DbQuery.g_selected_test_index;
import static com.example.solveo.DbQuery.g_testList;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.solveo.Adapter.QuestionGridAdapter;
import com.example.solveo.Adapter.QuestionsAdapter;

import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView questionsView;
    private TextView timerTV, testNameTV, questionNumberTV;
    private ImageView previousBtn, nextBtn, bookmarkBtn, addDelBookmarkBtn, menuBtn, drawer_close;
    private int questionID;
    private DrawerLayout drawer;
    private GridView questionListGV;
    private Button finishBtn;
    public CountDownTimer timer;
    private long timeleft;

    private QuestionGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_list_layout);

        gridAdapter = new QuestionGridAdapter(this, g_questionList.size());

        initViews();
        setupRecyclerView();
        setSnapHelper();
        setClickListeners();
        startTimer();
    }

    private void initViews() {
        questionsView = findViewById(R.id.questions_view);
        questionNumberTV = findViewById(R.id.questionNumber);
        testNameTV = findViewById(R.id.testTitle);
        timerTV = findViewById(R.id.testTimer);

        previousBtn = findViewById(R.id.previous);
        nextBtn = findViewById(R.id.next);
        bookmarkBtn = findViewById(R.id.bookmark);
        addDelBookmarkBtn = findViewById(R.id.add_bookmark);
        menuBtn = findViewById(R.id.menu_q);
        drawer = findViewById(R.id.drawer_layout);

        questionListGV = findViewById(R.id.quest_list_grid);
        questionListGV.setAdapter(gridAdapter);

        drawer_close = findViewById(R.id.drawer_close_btn);
        finishBtn = findViewById(R.id.testFinish); // Make sure this ID exists in your layout

        questionID = 0;

        if (!DbQuery.g_testList.isEmpty()) {
            testNameTV.setText(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTitle());
        }

        questionNumberTV.setText("1/" + DbQuery.g_questionList.size());
        g_questionList.get(0).setStatus(NOT_ANSWERED);
    }

    private void setupRecyclerView() {
        QuestionsAdapter questionsAdapter = new QuestionsAdapter(DbQuery.g_questionList);
        questionsView.setAdapter(questionsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        questionsView.setLayoutManager(layoutManager);
    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionsView);

        questionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (view != null) {
                    questionID = recyclerView.getLayoutManager().getPosition(view);

                    if (DbQuery.g_questionList.get(questionID).getStatus() == NOT_VISITED) {
                        g_questionList.get(questionID).setStatus(NOT_ANSWERED);
                    }

                    if (g_questionList.get(questionID).getStatus() == REVIEW) {
                        addDelBookmarkBtn.setImageResource(R.drawable.bookmark);
                        addDelBookmarkBtn.setTag("filled");
                    } else {
                        addDelBookmarkBtn.setImageResource(R.drawable.bookmark_blank);
                        addDelBookmarkBtn.setTag("blank");
                    }

                    questionNumberTV.setText((questionID + 1) + "/" + DbQuery.g_questionList.size());
                }
            }
        });
    }

    private void setClickListeners() {
        previousBtn.setOnClickListener(v -> {
            if (questionID > 0) {
                questionsView.smoothScrollToPosition(questionID - 1);
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (questionID < DbQuery.g_questionList.size() - 1) {
                questionsView.smoothScrollToPosition(questionID + 1);
            }
        });

        menuBtn.setOnClickListener(v -> {
            if (!drawer.isDrawerOpen(GravityCompat.END)) {
                gridAdapter.notifyDataSetChanged();
                drawer.openDrawer(GravityCompat.END);
            }
        });

        addDelBookmarkBtn.setOnClickListener(v -> {
            String currentTag = (String) addDelBookmarkBtn.getTag();
            if ("blank".equals(currentTag)) {
                addDelBookmarkBtn.setImageResource(R.drawable.bookmark);
                addDelBookmarkBtn.setTag("filled");
                g_questionList.get(questionID).setStatus(REVIEW);
            } else {
                addDelBookmarkBtn.setImageResource(R.drawable.bookmark_blank);
                addDelBookmarkBtn.setTag("blank");
                if (g_questionList.get(questionID).getSelcetedAns() != -1) {
                    g_questionList.get(questionID).setStatus(ANSWERED);
                } else {
                    g_questionList.get(questionID).setStatus(NOT_ANSWERED);
                }
            }
        });

        drawer_close.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        finishBtn.setOnClickListener(v -> finishTest());
    }

    private void finishTest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.alert_dialog_layout, null);
        Button yes_finish = view.findViewById(R.id.yes_finish);
        Button no_finish = view.findViewById(R.id.no_finish);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        no_finish.setOnClickListener(v -> alertDialog.dismiss());

        yes_finish.setOnClickListener(v -> {
            timer.cancel();
            alertDialog.dismiss();

            Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
            long total_Time = g_testList.get(g_selected_test_index).getTime()*60*1000;
            intent.putExtra("TIME TAKEN", total_Time-timeleft);
            startActivity(intent);
            QuestionActivity.this.finish();
        });

        alertDialog.show();
    }

    public void goToQuestion(int position) {
        questionsView.smoothScrollToPosition(position);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    private void startTimer() {
        long totalTime = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime() * 60 * 1000;

        timer = new CountDownTimer(totalTime + 1000, 1000) {
            @Override
            public void onTick(long remainingTime) {

                timeleft = remainingTime;
                String time = String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                        TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60);
                timerTV.setText(time);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
                long total_Time = g_testList.get(g_selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME TAKEN", total_Time-timeleft);
                startActivity(intent);
                QuestionActivity.this.finish();
            }
        }.start();
    }
}
