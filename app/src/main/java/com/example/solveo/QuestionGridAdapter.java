package com.example.solveo;

import static com.example.solveo.DbQuery.ANSWERED;
import static com.example.solveo.DbQuery.NOT_ANSWERED;
import static com.example.solveo.DbQuery.NOT_VISITED;
import static com.example.solveo.DbQuery.REVIEW;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestionGridAdapter extends BaseAdapter {

    private int numOfQuest;
    private Context context;

    public QuestionGridAdapter(Context context, int numOfQuest) {
        this.context = context;

        this.numOfQuest = numOfQuest;
    }

    @Override
    public int getCount() {
        return numOfQuest;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View myview;

        if(convertView == null){
            myview = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_grid_item, parent, false);
        }
        else{
            myview = convertView;
        }

        myview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof QuestionActivity)
                    ((QuestionActivity)context).goToQuestion(position);
            }
        });


        TextView questionText = myview.findViewById(R.id.question_NumberGV);
        questionText.setText(String.valueOf(position+1));

        switch (DbQuery.g_questionList.get(position).getStatus()) {
            case NOT_VISITED:
                questionText.setBackgroundResource(R.drawable.blue_bg_q);
                break;

            case NOT_ANSWERED:
                questionText.setBackgroundResource(R.drawable.yellow_bg_q);
                break;

            case ANSWERED:
                questionText.setBackgroundResource(R.drawable.green_bg_q);
                break;

            case REVIEW:
                questionText.setBackgroundResource(R.drawable.purple_bg_q);
                break;

            default:
                break;
        }

        return myview;
    }
}
