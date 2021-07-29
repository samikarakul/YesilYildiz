package com.example.mobilvizeprojesi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapterCategories extends RecyclerView.Adapter<RecyclerAdapterCategories.ViewHolder> {

    List<Category> recCategories = new ArrayList<Category>();
    List<String> parents = new ArrayList<String>();
    Context context;

    public RecyclerAdapterCategories(Context context, List<Category> recCategories, List<String> parents) {
        this.recCategories = recCategories;
        this.context = context;
        this.parents = parents;
    }

    @NonNull
    @Override
    public RecyclerAdapterCategories.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.categorie_design, parent, false);
        RecyclerAdapterCategories.ViewHolder viewHolder = new RecyclerAdapterCategories.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterCategories.ViewHolder holder, int position) {
        holder.textView.setText(recCategories.get(position).getCategoryName());

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Tıkladııı -> " + recCategories.get(position).getCategoryName(), Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(context, CategoryUpdateActivity.class);
                myIntent.putExtra("category", (Serializable) recCategories.get(position));
                myIntent.putExtra("categoryId", parents.get(position));
                context.startActivity(myIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return recCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tV_categorieDesign);

        }
    }

}
