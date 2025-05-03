package com.example.solveo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private List<CategoryModel> cat_list;

    public CategoryAdapter(List<CategoryModel> cat_list) {
        this.cat_list = cat_list;
    }

    @Override
    public int getCount() {
        return cat_list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {


        View myview;

        if(convertView == null){
            myview = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout, parent, false);
        }
        else{
            myview = convertView;
        }

        TextView catName = myview.findViewById(R.id.CatNameText);

        catName.setText(cat_list.get(position).getName());


        return myview;


    }
}
