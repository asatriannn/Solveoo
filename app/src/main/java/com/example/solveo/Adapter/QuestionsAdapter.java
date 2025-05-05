package com.example.solveo.Adapter;

import static com.example.solveo.DbQuery.ANSWERED;
import static com.example.solveo.DbQuery.NOT_ANSWERED;
import static com.example.solveo.DbQuery.REVIEW;
import static com.example.solveo.DbQuery.g_questionList;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveo.DbQuery;
import com.example.solveo.Models.QuestionModel;
import com.example.solveo.R;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionModel> questionsList;

    public QuestionsAdapter(List<QuestionModel> questionsList) {
        this.questionsList = questionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_layout, parent, false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private Button optionA, optionB, optionC,optionD, previousSelectedBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question_text);
            optionA = itemView.findViewById(R.id.optionA);
            optionB = itemView.findViewById(R.id.optionB);
            optionC = itemView.findViewById(R.id.optionC);
            optionD = itemView.findViewById(R.id.optionD);
            previousSelectedBtn = null;


        }

        private void setData(final int position){
            question.setText(questionsList.get(position).getQuestion());
            optionA.setText(questionsList.get(position).getOptionA());
            optionB.setText(questionsList.get(position).getOptionB());
            optionC.setText(questionsList.get(position).getOptionC());
            optionD.setText(questionsList.get(position).getOptionD());


            optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectOption(optionA, 1, position);

                }
            });

            optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionB, 2, position);

                }
            });

            optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionC, 3, position);

                }
            });

            optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionD, 4, position);

                }
            });
        }

        private void selectOption(Button btn, int option_num, int quesID){

            if(previousSelectedBtn == null){
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B6DAF2")));

                DbQuery.g_questionList.get(quesID).setSelcetedAns(option_num);

                changeStatus(quesID, ANSWERED);

                previousSelectedBtn = btn;
            }
            else{

                if( previousSelectedBtn.getId() == btn.getId()){
                    btn.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    DbQuery.g_questionList.get(quesID).setSelcetedAns(-1);

                    changeStatus(quesID, NOT_ANSWERED);

                    previousSelectedBtn = null;

                }

                else{
                    previousSelectedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B6DAF2")));

                    DbQuery.g_questionList.get(quesID).setSelcetedAns(option_num);

                    changeStatus(quesID, ANSWERED);

                    previousSelectedBtn = btn;

                }

            }

        }

        private void changeStatus(int id, int status){
            if(!(g_questionList.get(id).getStatus() == REVIEW)){
                g_questionList.get(id).setStatus(status);
            }
        }
    }
}
