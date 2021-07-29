package com.example.mobilvizeprojesi;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class YeniKategoriActivity extends AppCompatActivity {

    private Button btn_yeniKategori_kaydet;
    private Button btn_yeniKategori_mainDon;
    private EditText eT_yeniKategori_kategoriAdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_kategori);

        btn_yeniKategori_kaydet = findViewById(R.id.btn_yeniKategori_kaydet);
        btn_yeniKategori_mainDon = findViewById(R.id.btn_yeniKategori_mainDon);
        eT_yeniKategori_kategoriAdi = findViewById(R.id.eT_yeniKategori_kategoriAdi);

        btn_yeniKategori_mainDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YeniKategoriActivity.this, MainActivity.class));
                finish();
            }
        });

        btn_yeniKategori_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text_kategoriAdi = eT_yeniKategori_kategoriAdi.getText().toString();
                if(!text_kategoriAdi.equals(""))
                {
                    kategoriSorgu(text_kategoriAdi);
                }
            }
        });
    }

    public void kategoriSorgu(String kategoriAdi)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Categories");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean kategoriVarMi = false;
                    DataSnapshot dataSnapshot = task.getResult();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Category cat = snapshot.getValue(Category.class);
                        if(cat.getCategoryName().equals(kategoriAdi))
                        {
                            kategoriVarMi = true;
                            break;
                        }
                    }
                    if(!kategoriVarMi)
                    {
                        kategoriEkle(kategoriAdi);
                    }
                    else
                    {
                        Toast.makeText(YeniKategoriActivity.this, "Bu isimde bir kategori mevcut.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void kategoriEkle(String kategoriAdi)
    {
        Category category= new Category();
        category.setCategoryName(kategoriAdi);
        Task<Void> ref = FirebaseDatabase.getInstance().getReference().child("Categories").push().setValue(category);
        ref.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(YeniKategoriActivity.this, "Yükleme başarılı", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(YeniKategoriActivity.this, MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(YeniKategoriActivity.this, "Yükleme başarısız", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d("tekrarSorgu", "-->" + kategoriAdi);
    }
}