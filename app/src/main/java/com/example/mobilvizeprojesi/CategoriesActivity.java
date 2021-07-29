package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesActivity extends AppCompatActivity {

    private Button btn_categoryMain_mainDon;
    private Button btn_categoriMain_yeniKategori;

    RecyclerView recyclerView;
    RecyclerAdapterCategories recyclerAdapter;

    List<Category> categoriesClass = new ArrayList<Category>();
    List<String> parents = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        btn_categoryMain_mainDon = findViewById(R.id.btn_categoryMain_mainDon);
        btn_categoriMain_yeniKategori = findViewById(R.id.btn_categoryMain_yeniKategori);

        categoriesListeCagir();

        recyclerView = findViewById(R.id.recylerView_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerAdapter = new RecyclerAdapterCategories(this, categoriesClass, parents);
        recyclerView.setAdapter(recyclerAdapter);

        btn_categoryMain_mainDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoriesActivity.this, MainActivity.class));
                finish();
            }
        });

        btn_categoriMain_yeniKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoriesActivity.this, YeniKategoriActivity.class));
                finish();
            }
        });

    }

    private void categoriesListeCagir()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
                for(DataSnapshot snap: snapshot.getChildren())
                {
                    parents.add(snap.getKey());
                    Category category = snap.getValue(Category.class);
                    categoriesClass.add(category);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}