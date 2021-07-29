package com.example.mobilvizeprojesi;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MoviePointActivity extends AppCompatActivity {

    private ImageView imageView_moviePoint;
    private TextView tV_moviePoint_movieTitle;
    private TextView tV_moviePoint_moviePoint;
    private Button btn_moviePoint_puanVerPozitif;
    private Button btn_moviePoint_puanVerNegatif;
    private Button btn_moviePoint_userMainDon;

    private String movieId;
    private Movie movieForPoint;
    private MovieUserPoint movieUserPointForUpdate;
    private MoviePointsWithParent moviePointsWithParentForUpdate;
    private boolean puanSorgu = false;
    private String negatifRenk = "-24159";
    private String pozitifRenk = "-4487428";
    private String renk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_point);

        Intent myGetIntent = getIntent();
        movieForPoint = (Movie)myGetIntent.getSerializableExtra("movie");
        movieId = myGetIntent.getStringExtra("movieId");

        imageView_moviePoint = findViewById(R.id.imageView_moviePoint);
        tV_moviePoint_movieTitle = findViewById(R.id.tV_moviePoint_movieTitle);
        tV_moviePoint_moviePoint = findViewById(R.id.tV_moviePoint_moviePoint);
        btn_moviePoint_puanVerPozitif = findViewById(R.id.btn_moviePoint_puanVerPozitif);
        btn_moviePoint_puanVerNegatif = findViewById(R.id.btn_moviePoint_puanVerNegatif);
        btn_moviePoint_userMainDon = findViewById(R.id.btn_moviePoint_userMainDon);

        tV_moviePoint_movieTitle.setText(movieForPoint.getMovieTitle());
        new MoviePointActivity.DownloadImageTask((ImageView) imageView_moviePoint).execute(movieForPoint.getMoviePhoto());

        tV_moviePoint_moviePoint.setText(String.valueOf(movieForPoint.getMoviePoint()));

        degerleriDoldur();

        btn_moviePoint_userMainDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoviePointActivity.this, UserMainActivity.class));
                finish();
            }
        });

        btn_moviePoint_puanVerPozitif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renk = String.valueOf(tV_moviePoint_moviePoint.getCurrentTextColor());
                if(renk.equals(negatifRenk))
                {
                    puanGuncelle("pozitif");
                }
                else if(renk.equals(pozitifRenk))
                 {
                     Log.d("yakala", "Olmaz" );
                 }
                else
                {
                    puanOlustur("pozitif");
                }
            }
        });

        btn_moviePoint_puanVerNegatif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renk = String.valueOf(tV_moviePoint_moviePoint.getCurrentTextColor());
                if(renk.equals(pozitifRenk))
                {
                    puanGuncelle("negatif");
                }
                else if(renk.equals(negatifRenk))
                {
                    Log.d("yakala", "Olmaz" );

                }
                else
                {
                    puanOlustur("negatif");
                }
            }
        });

    }

    public void puanOlustur(String islemTuru)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MovieUserPoint movieUserPointC = new MovieUserPoint();
        movieUserPointC.setMovieId(movieId);
        movieUserPointC.setUserEmail(user.getEmail());
        int sayi = movieForPoint.getMoviePoint();

        if(islemTuru.equals("pozitif"))
        {
            movieUserPointC.setMoviePoint(1);
            sayi+=1;
            tV_moviePoint_moviePoint.setTextColor(getResources().getColor(R.color.yeniMavi));
            tV_moviePoint_moviePoint.setText(String.valueOf(sayi));
            degerleriDoldur();

        }
        else
        {
            movieUserPointC.setMoviePoint(-1);
            sayi-=1;
            tV_moviePoint_moviePoint.setTextColor(getResources().getColor(R.color.yeniKirmizi));
            tV_moviePoint_moviePoint.setText(String.valueOf(sayi));
            degerleriDoldur();

        }

        movieForPoint.setMoviePoint(sayi);
        FirebaseDatabase.getInstance().getReference().child("MovieUserPoints")
                .push().setValue(movieUserPointC);

        Task<Void> ref = FirebaseDatabase.getInstance().getReference().child("Movies")
                .child(movieId).setValue(movieForPoint);
        ref.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MoviePointActivity.this, "Yükleme başarılı", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MoviePointActivity.this, "Yükleme başarısız.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void puanGuncelle(String islemTuru)
    {
        //pozitifse farklı negse farklı.
        if(moviePointsWithParentForUpdate == null)
        {
            Task k = FirebaseDatabase.getInstance().getReference().child("MovieUserPoints").orderByKey().limitToLast(1).get();
            while(true)
            {
                if(k.isComplete())
                {
                    String sonuc = k.getResult().toString().split("value = ", 2)[1];
                    String key = sonuc.split("=", 2)[0].substring(1);

                    Log.d("keyResult", "--->" + key);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    movieUserPointForUpdate = new MovieUserPoint();
                    movieUserPointForUpdate.setMoviePoint(movieForPoint.getMoviePoint());
                    movieUserPointForUpdate.setUserEmail(user.getEmail());
                    movieUserPointForUpdate.setMovieId(movieId);

                    moviePointsWithParentForUpdate = new MoviePointsWithParent();
                    moviePointsWithParentForUpdate.setMovieUserPoint(movieUserPointForUpdate);
                    moviePointsWithParentForUpdate.setMovie(movieForPoint);
                    moviePointsWithParentForUpdate.setParentId(key);
                    break;
                }
            }
        }
        else
        {
            int sayi = moviePointsWithParentForUpdate.getMovie().getMoviePoint();
            if(islemTuru.equals("negatif"))
            {
                movieUserPointForUpdate.setMoviePoint(-1);
                sayi -= 2;
                tV_moviePoint_moviePoint.setTextColor(getResources().getColor(R.color.yeniKirmizi));
                tV_moviePoint_moviePoint.setText(String.valueOf(sayi));
                degerleriDoldur();

            }
            if(islemTuru.equals("pozitif"))
            {
                movieUserPointForUpdate.setMoviePoint(1);
                sayi += 2;
                tV_moviePoint_moviePoint.setTextColor(getResources().getColor(R.color.yeniMavi));
                tV_moviePoint_moviePoint.setText(String.valueOf(sayi));
                degerleriDoldur();

            }
            moviePointsWithParentForUpdate.getMovie().setMoviePoint(sayi);

            Log.d("puanGuncelleIci", " -- > " +moviePointsWithParentForUpdate.getParentId());
            FirebaseDatabase.getInstance().getReference().child("MovieUserPoints")
                    .child(moviePointsWithParentForUpdate.getParentId()).setValue(movieUserPointForUpdate);
            moviePointsWithParentForUpdate.getMovie().setMoviePoint(sayi);
            FirebaseDatabase.getInstance().getReference().child("Movies")
                    .child(movieUserPointForUpdate.getMovieId()).setValue(moviePointsWithParentForUpdate.getMovie());
        }
    }

    public void degerleriDoldur()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("MovieUserPoints");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        MovieUserPoint movieUserPoint = snapshot.getValue(MovieUserPoint.class);
                        if(movieUserPoint.getMovieId().equals(movieId) && movieUserPoint.getUserEmail().equals(user.getEmail()))
                        {
                            MoviePointsWithParent moviePointsWithParent = new MoviePointsWithParent();
                            moviePointsWithParent.setParentId(snapshot.getKey());
                            moviePointsWithParent.setMovie(movieForPoint);
                            moviePointsWithParent.setMovieUserPoint(movieUserPoint);
                            puanSorgu = true;
                            Log.d("parent", snapshot.getKey() );
                            new movieListeAsync((TextView) tV_moviePoint_moviePoint).execute(moviePointsWithParent);
                            break;
                        }
                    }
                }
            }
        });

    }
    //async Movie sorgu
    private class movieListeAsync extends AsyncTask<MoviePointsWithParent, Void, MoviePointsWithParent> {
        TextView textViewPuan;

        public movieListeAsync(TextView tv) {
            this.textViewPuan = tv;
        }

        protected MoviePointsWithParent doInBackground(MoviePointsWithParent... moviePointsWithParents) {
            MoviePointsWithParent moviePointsWithParent = moviePointsWithParents[0];

            Movie movie = moviePointsWithParent.getMovie();
            String parentId = moviePointsWithParent.getParentId();
            int sayi = 10;


            try {
                //set ederken burası UI güncelleyeecek.
                // set edilmesi lazım.

            } catch (Exception e) {
                Log.e("mPhotoErr", e.getMessage());
                e.printStackTrace();
            }
            return moviePointsWithParent;
        }

        protected void onPostExecute(MoviePointsWithParent result) {
            movieUserPointForUpdate = result.getMovieUserPoint();
            if( !( String.valueOf(textViewPuan.getCurrentTextColor()).equals(negatifRenk) ) && !( String.valueOf(textViewPuan.getCurrentTextColor()).equals(pozitifRenk) ) )
            {
                if(movieUserPointForUpdate.getMoviePoint() == 1) textViewPuan.setTextColor(getResources().getColor(R.color.yeniMavi));
                else textViewPuan.setTextColor(getResources().getColor(R.color.yeniKirmizi));
            }

            Log.d("textViewRenk", String.valueOf(textViewPuan.getCurrentTextColor()));
            moviePointsWithParentForUpdate = result;
        }

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


    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}