package com.example.mobilvizeprojesi;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class YeniGonderiActivity extends AppCompatActivity {

    private EditText eT_yeniGonderi_movieTitle;
    private Button btn_yeniGonderi_paylas;
    private Button btn_yeniGonderi_fotoSec;
    private Button btn_yeniGonderi_mainDon;
    private ImageView imageView_yeniGonderi_moviePhoto;

    private Uri imageUri;
    private String url;

    private Spinner spinner;
    private ArrayAdapter<String> dataAdapterForCategories;


    private List<String> categories = new ArrayList<String>();

    private List<Category> categoriesClass = new ArrayList<Category>();
    private static final int IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_gonderi);

        categories.add("Kategori Seçiniz");
        categoriesDoldur();

        eT_yeniGonderi_movieTitle = findViewById(R.id.eT_yeniGonderi_movieTitle);
        btn_yeniGonderi_fotoSec = findViewById(R.id.btn_yeniGonderi_fotoSec);
        btn_yeniGonderi_paylas = findViewById(R.id.btn_yeniGonderi_paylas);
        btn_yeniGonderi_mainDon = findViewById(R.id.btn_yeniGonderi_mainDon);
        imageView_yeniGonderi_moviePhoto = findViewById(R.id.imageView_yeniGonderi_moviePhoto);

        btn_yeniGonderi_mainDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YeniGonderiActivity.this, MainActivity.class));
                finish();
            }
        });

        btn_yeniGonderi_fotoSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        btn_yeniGonderi_paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yeniGonderi_movieTitle = eT_yeniGonderi_movieTitle.getText().toString();
                String yeniGonderi_movieGenre = spinner.getSelectedItem().toString();
                if(bosKontrol(yeniGonderi_movieTitle,url))
                {
                    if(kategoriSeciliMi())
                    {
                        Movie movie = new Movie();
                        movie.setMovieTitle(yeniGonderi_movieTitle);
                        movie.setMovieGenre(yeniGonderi_movieGenre);
                        movie.setMoviePhoto(url);
                        movie.setMoviePoint(0);

                        movieVarMi(movie);
                    }
                    else
                    {
                        Toast.makeText(YeniGonderiActivity.this, "Lütfen Kategori Seçin", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(YeniGonderiActivity.this, "Boş Değer Girilemez.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void categoriesDoldur()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren())
                {
                    Category category = snap.getValue(Category.class);
                    categoriesClass.add(category);
                    categories.add(category.getCategoryName());
                }
                spinner = findViewById(R.id.spinner);
                dataAdapterForCategories = new ArrayAdapter<String>(YeniGonderiActivity.this, android.R.layout.simple_spinner_item, categories);
                dataAdapterForCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapterForCategories);
                dataAdapterForCategories.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean kategoriSeciliMi()
    {
        if(spinner.getSelectedItem().toString().equals("Seçiniz")) return false;
        return true;
    }

    private boolean bosKontrol(String title, String foto)
    {
        if(title.equals("") || foto.equals("")) return false;
        return true;
    }

    public void movieVarMi(Movie movie)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Movies");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean movieSorgu = false;
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap: snapshot.getChildren())
                    {
                        Movie m = snap.getValue(Movie.class);
                        if(movie.getMovieTitle().equals(m.getMovieTitle()))
                        {
                            movieSorgu = true;
                            break;
                        }
                    }
                    if(!movieSorgu)
                    {
                        movieOlustur(movie);
                    }
                    else
                    {
                        Toast.makeText(YeniGonderiActivity.this, "Bu film mevcut.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void movieOlustur(Movie movie)
    {
        Task<Void> ref = FirebaseDatabase.getInstance().getReference().child("Movies").push().setValue(movie);
        ref.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(YeniGonderiActivity.this, "Yükleme başarılı", Toast.LENGTH_SHORT).show();
                    eT_yeniGonderi_movieTitle.setText("");
                    url = "";
                    startActivity(new Intent(YeniGonderiActivity.this, MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(YeniGonderiActivity.this, "Yükleme başarısız", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //upload fonksiyonları
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView_yeniGonderi_moviePhoto);
            uploadImage();
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();

        if(imageUri != null)
        {
            StorageReference fileRef  = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url = uri.toString();

                            Log.d("DownloadUrl", url);
                            pd.dismiss();
                            Toast.makeText(YeniGonderiActivity.this, "Görsel yükleme başarılı", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        }
    }
    //upload fonksiyonları
}