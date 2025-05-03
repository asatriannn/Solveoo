package com.example.solveo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TestAdapter extends BaseAdapter {

    private Context context;
    private List<TestModel> testList;

    public TestAdapter(Context context, List<TestModel> testList) {
        this.context = context;
        this.testList = testList;
    }

    @Override
    public int getCount() {
        return testList.size();
    }

    @Override
    public Object getItem(int position) {
        return testList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        } else {
            view = convertView;
        }

        TextView title = view.findViewById(R.id.test_title);
        TextView score = view.findViewById(R.id.test_score);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQuery.g_selected_test_index = position;

                Intent intent = new Intent(view.getContext(), StartTestActivity.class);

                view.getContext().startActivity(intent);
            }
        });

        TestModel model = testList.get(position);
        title.setText(model.getTitle());
        score.setText(model.getTopScore() + "/" + "800");

        return view;
    }
}
