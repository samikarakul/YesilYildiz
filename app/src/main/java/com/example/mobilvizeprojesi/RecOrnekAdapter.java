package com.example.mobilvizeprojesi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecOrnekAdapter extends RecyclerView.Adapter<RecOrnekAdapter.ViewHolder>{

    List<Category> recCategories = new ArrayList<Category>();
    List<String> parents = new ArrayList<String>();
    Context context;

    public RecOrnekAdapter(Context context, List<Category> recCategories, List<String> parents) {
        this.recCategories = recCategories;
        this.context = context;
        this.parents = parents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tV_categorieDesign);

        }
    }
}
