package com.example.mobilvizeprojesi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CategoryUpdateActivity extends AppCompatActivity {

    private EditText eT_categoryUpdate_categoryName;
    private Button btn_categoryUpdate_kaydet;
    private Button btn_categoryUpdate_kategoriSil;
    private Button btn_categoryUpdate_categoriesDon;

    String categoryId;
    Category categoryForUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_update);

        eT_categoryUpdate_categoryName = findViewById(R.id.eT_categoryUpdate_categoryName);
        btn_categoryUpdate_kaydet = findViewById(R.id.btn_categoryUpdate_kaydet);
        btn_categoryUpdate_kategoriSil = findViewById(R.id.btn_categoryUpdate_kategoriSil);
        btn_categoryUpdate_categoriesDon = findViewById(R.id.btn_categoryUpdate_categoriesDon);

        Intent myGetIntent = getIntent();
        categoryId = myGetIntent.getStringExtra("categoryId");
        categoryForUpdate = (Category) myGetIntent.getSerializableExtra("category");

        if(categoryForUpdate != null)
        {
            eT_categoryUpdate_categoryName.setText(categoryForUpdate.getCategoryName());
        }

        btn_categoryUpdate_categoriesDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryUpdateActivity.this, CategoriesActivity.class));
                finish();
            }
        });

        btn_categoryUpdate_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eT_categoryUpdate_categoryName.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Boş değer girilemez.", Toast.LENGTH_SHORT);
                }
                else
                {
                    String kategoriAdi = eT_categoryUpdate_categoryName.getText().toString();
                    kategoriVarMi(kategoriAdi);
                }
            }
        });

        btn_categoryUpdate_kategoriSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silmeEminMisin();
            }
        });
    }

    public void kategoriVarMi(String kategoriAdi)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Categories");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean kategoriSorgu = false;
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap: snapshot.getChildren())
                    {
                        Category category = snap.getValue(Category.class);
                        if(category.getCategoryName().equals(kategoriAdi))
                        {
                            kategoriSorgu = true;
                            break;
                        }
                    }
                    if(!kategoriSorgu)
                    {
                        guncellemeEminMisin(kategoriAdi);
                    }
                    else
                    {
                        Toast.makeText(CategoryUpdateActivity.this, "Bu isimde bir kategori var.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void silmeEminMisin()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryUpdateActivity.this);
        builder.setTitle("Yeşil Yıldız");
        builder.setMessage(categoryForUpdate.getCategoryName()+" Kategorisini silmek üzeresiniz. Bu işlemi yapmak istediğinize emin misiniz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                kategoriSil();
            }
        });
        builder.show();
    }

    public void guncellemeEminMisin(String kategoriAdi)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryUpdateActivity.this);
        builder.setTitle("Yeşil Yıldız");
        builder.setMessage("Kategori adını "+ kategoriAdi +" yapmak istediğinize emin misiniz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                kategoriUpdate(kategoriAdi);
            }
        });
        builder.show();
    }

    public void kategoriUpdate(String kategoriAdi)
    {
        categoryForUpdate.setCategoryName(kategoriAdi);
        Task k = FirebaseDatabase.getInstance().getReference().child("Categories").child(categoryId).setValue(categoryForUpdate);
        k.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Güncelleme işlemi başarılı.", Toast.LENGTH_SHORT);
                    startActivity(new Intent(CategoryUpdateActivity.this, CategoriesActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Güncelleme işlemi başarısız..", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void kategoriSil()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Categories");
        Task k = ref.child(categoryId).removeValue();
        k.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    kategoriFilmleriSil();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Silme işlemi başarısız..", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void kategoriFilmleriSil()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Movies");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    List<String> movieIds = new ArrayList<String>();
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap: snapshot.getChildren())
                    {
                        Movie m = snap.getValue(Movie.class);
                        if(m.getMovieGenre().equals(categoryForUpdate.getCategoryName()))
                        {
                            movieIds.add(snap.getKey());
                            ref.child(snap.getKey()).removeValue();
                        }
                    }
                    kategoriFilmYorumlarSil(movieIds);
                }
            }
        });
    }

    public void kategoriFilmYorumlarSil(List<String> movieIds)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("MovieUserPoints");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap: snapshot.getChildren())
                    {
                        MovieUserPoint movieUserPoint = snap.getValue(MovieUserPoint.class);
                        if(movieIds.contains(movieUserPoint.getMovieId()))
                        {
                            ref.child(snap.getKey()).removeValue();
                            Log.d("silinenPoint", "-->" + movieUserPoint.getMovieId());
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), "Silme işlemi tamamlandı", Toast.LENGTH_SHORT);
                startActivity(new Intent(CategoryUpdateActivity.this, CategoriesActivity.class));
                finish();
            }
        });
    }
}