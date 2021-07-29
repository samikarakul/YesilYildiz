package com.example.mobilvizeprojesi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MovieUpdateActivity extends AppCompatActivity {

    private Button btn_movieUpdate_fotoSec;
    private Button btn_movieUpdate_guncelle;
    private Button btn_movieUpdate_sil;
    private EditText eT_movieUpdate_movieTitle;
    private ImageView imageView_movieUpdate_moviePhoto;

    private Spinner spinner_movieUpdate;
    private ArrayAdapter<String> dataAdapterForCategories;
    private List<String> categories = new ArrayList<String>();
    private String seciliCategory;

    Movie movieForUpdate;
    private Uri imageUri;
    private String url;
    String movieId;
    String oldMovieTitle;

    private Button btn_movieUpdate_mainDon;

    private static final int IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_update);

        url = "";

        spinner_movieUpdate = (Spinner)findViewById(R.id.spinner_movieUpdate);
        categoriesDoldur();

        Intent myGetIntent = getIntent();
        movieId = myGetIntent.getStringExtra("movieId");
        movieForUpdate = (Movie)myGetIntent.getSerializableExtra("movie");

        oldMovieTitle = movieForUpdate.getMovieTitle();

        btn_movieUpdate_fotoSec = findViewById(R.id.btn_movieUpdate_fotoSec);
        btn_movieUpdate_guncelle = findViewById(R.id.btn_movieUpdate_guncelle);
        btn_movieUpdate_sil = findViewById(R.id.btn_movieUpdate_movieSil);
        eT_movieUpdate_movieTitle = findViewById(R.id.eT_movieUpdate_movieTitle);
        imageView_movieUpdate_moviePhoto = findViewById(R.id.imageView_movieUpdate_moviePhoto);



        btn_movieUpdate_mainDon = findViewById(R.id.btn_movieUpdate_mainDon);


        btn_movieUpdate_mainDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MovieUpdateActivity.this, MainActivity.class));
                finish();
            }
        });

        if(!movieId.equals(""))
        {
            eT_movieUpdate_movieTitle.setText(movieForUpdate.getMovieTitle());
            new MovieUpdateActivity.DownloadImageTask((ImageView) imageView_movieUpdate_moviePhoto).execute(movieForUpdate.getMoviePhoto());
        }

        btn_movieUpdate_guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movieForUpdate.setMovieTitle(eT_movieUpdate_movieTitle.getText().toString());
                movieForUpdate.setMovieGenre(spinner_movieUpdate.getSelectedItem().toString());

                Movie m = movieForUpdate;
                movieVarMi(m);


            }
        });

        btn_movieUpdate_fotoSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openImage();
            }
        });

        btn_movieUpdate_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silmeEminMisin();
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
                    categories.add(category.getCategoryName());
                }
                dataAdapterForCategories = new ArrayAdapter<String>(MovieUpdateActivity.this, android.R.layout.simple_spinner_item, categories);
                dataAdapterForCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_movieUpdate.setAdapter(dataAdapterForCategories);
                spinner_movieUpdate.setSelection(((ArrayAdapter)spinner_movieUpdate.getAdapter()).getPosition(movieForUpdate.getMovieGenre()));
                dataAdapterForCategories.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void movieVarMi(Movie movie)
    {
        if(!oldMovieTitle.equals(eT_movieUpdate_movieTitle.getText().toString()))
        {
            Log.d("movieAdAyniMiSorgu", "-->" + oldMovieTitle + "--->" + eT_movieUpdate_movieTitle.getText().toString());
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
                            guncellemeEminMisin(movie);
                        }
                        else
                        {
                            Toast.makeText(MovieUpdateActivity.this, "Bu isimde bir film mevcut", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        else
        {
            guncellemeEminMisin(movie);
        }

    }

    public void guncellemeEminMisin(Movie movie)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieUpdateActivity.this);
        builder.setTitle("Yeşil Yıldız");
        builder.setMessage("Filmin adını "+ movie.getMovieTitle() + ", kategorisini "+ movie.getMovieGenre() +" yapmak istediğinize emin misiniz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                movieGuncelle(movie);
            }
        });
        builder.show();
    }

    public void movieGuncelle(Movie movie)
    {
        Task<Void> ref = FirebaseDatabase.getInstance().getReference().child("Movies").child(movieId).setValue(movie);
        ref.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MovieUpdateActivity.this, "Yükleme başarılı", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MovieUpdateActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(MovieUpdateActivity.this, "Yükleme başarısız.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void silmeEminMisin()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieUpdateActivity.this);
        builder.setTitle("Yeşil Yıldız");
        builder.setMessage(movieForUpdate.getMovieTitle()+" filmini silmek üzeresiniz. Bu işlemi yapmak istediğinize emin misiniz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                movieSil();
            }
        });
        builder.show();
    }

    public void movieSil()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Movies");
        Task k = ref.child(movieId).removeValue();
        k.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    moviePointsSil();
                }
            }
        });
    }

    public void moviePointsSil()
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
                        if(movieUserPoint.getMovieId().equals(movieId))
                        {
                            ref.child(snap.getKey()).removeValue();
                        }
                    }
                }
                Toast.makeText(MovieUpdateActivity.this, "Silme İşlemi Başarılı", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MovieUpdateActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    //Urlden Bitmap'e çevirme
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("mPhotoErr", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    //foto ekleme işlemleri başlangıç

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
            Picasso.get().load(imageUri).into(imageView_movieUpdate_moviePhoto);
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
                            movieForUpdate.setMoviePhoto(url);
                            Log.d("DownloadUrl", url);
                            pd.dismiss();
                            Toast.makeText(MovieUpdateActivity.this, "Görsel yükleme başarılı", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        }
    }
    //foto ekleme işlemleri bitiş
}