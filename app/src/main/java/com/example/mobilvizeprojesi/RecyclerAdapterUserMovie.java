package com.example.mobilvizeprojesi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RecyclerAdapterUserMovie extends RecyclerView.Adapter<RecyclerAdapterUserMovie.ViewHolder> {

    List<Movie> recMovies = new ArrayList<Movie>();
    List<String> parents = new ArrayList<String>();
    Context context;
    
    private String negatifRenk = "-24159";
    private String pozitifRenk = "-4487428";
    private MoviePointsWithParent moviePointsWithParentForUpdate;
    private MovieUserPoint movieUserPointForUpdate = new MovieUserPoint();
    private List<String> userPointList = new ArrayList<String>();
    int puan = 0;
    String renk;

    public RecyclerAdapterUserMovie(Context context, List<Movie> recMovies, List<String> parents) {
        this.recMovies = recMovies;
        this.context = context;
        this.parents = parents;
    }

    @NonNull
    @Override
    public RecyclerAdapterUserMovie.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_design_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterUserMovie.ViewHolder holder, int position) {

        degerleriDoldur(position, holder);
        puanlariGetir(position, holder);

        holder.textView.setText(recMovies.get(position).getMovieTitle());
        holder.textViewPoint.setText(String.valueOf(recMovies.get(position).getMoviePoint()));
        new DownloadImageTask((ImageView) holder.imageView).execute(recMovies.get(position).getMoviePhoto());

        holder.btn_pozitifPuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renk = String.valueOf(holder.textViewPoint.getCurrentTextColor());
                if(renk.equals(negatifRenk))
                {
                    puanGuncelle("pozitif", holder, position);
                }
                else if(renk.equals(pozitifRenk))
                {
                    Log.d("yakala", "Olmaz" + renk);
                }
                else
                {
                    puanOlustur("pozitif", holder, position);
                }
            }
        });

        holder.btn_negatifPuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renk = String.valueOf(holder.textViewPoint.getCurrentTextColor());
                if(renk.equals(pozitifRenk))
                {
                    puanGuncelle("negatif", holder, position);
                    holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_sonra));
                }
                else if(renk.equals(negatifRenk))
                {
                    Log.d("yakala", "Olmaz" + renk );

                }
                else
                {
                    puanOlustur("negatif", holder, position);
                    holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_sonra));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        TextView textViewPoint;
        TextView textView_oyVerdiniz;
        TextView textViewToplamPuan;
        Button btn_pozitifPuan;
        Button btn_negatifPuan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movieImage_user);
            textView = itemView.findViewById(R.id.tV_movieTitle_user);
            textViewToplamPuan = itemView.findViewById(R.id.tV_toplamPoint_user);
            textViewPoint = itemView.findViewById(R.id.tV_moviePoint_user);
            btn_pozitifPuan = itemView.findViewById(R.id.btn_pozitifPuan_user);
            btn_negatifPuan = itemView.findViewById(R.id.btn_negatifPuan_user);

            textView_oyVerdiniz = itemView.findViewById(R.id.textView13);

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

    //movie point için

    public void degerleriDoldur(int position, RecyclerAdapterUserMovie.ViewHolder holder)
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
                        if(movieUserPoint.getMovieId().equals( parents.get(position)) && movieUserPoint.getUserEmail().equals(user.getEmail()))
                        {
                            MoviePointsWithParent moviePointsWithParent = new MoviePointsWithParent();
                            moviePointsWithParent.setParentId(snapshot.getKey());
                            moviePointsWithParent.setMovie(recMovies.get(position));
                            moviePointsWithParent.setMovieUserPoint(movieUserPoint);
                            movieUserPointForUpdate = movieUserPoint;
                            userPointList.add(movieUserPoint.getMovieId());
                            Log.d("parent", snapshot.getKey() );
                            renkKarsilastir(holder, movieUserPoint);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void renkKarsilastir(RecyclerAdapterUserMovie.ViewHolder holder, MovieUserPoint movieUserPoint)
    {
        if( !( String.valueOf(holder.textViewPoint.getCurrentTextColor()).equals(negatifRenk) ) && !( String.valueOf( holder.textViewPoint.getCurrentTextColor() ).equals(pozitifRenk) ) )
        {
            if(movieUserPoint.getMoviePoint() == 1)
            {
                holder.textViewPoint.setTextColor(context.getResources().getColor(R.color.yeniMavi));
                holder.btn_pozitifPuan.setBackground(context.getDrawable(R.drawable.arti_sonra));
                holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_baslangic));
            }
            else{
                holder.textViewPoint.setTextColor(context.getResources().getColor(R.color.yeniKirmizi));
                holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_sonra));
                holder.btn_pozitifPuan.setBackground(context.getDrawable(R.drawable.likerenk));
            }
            holder.textView_oyVerdiniz.setVisibility(View.VISIBLE);
        }

        Log.d("textViewRenk", String.valueOf(holder.textViewPoint.getCurrentTextColor()));
    }


    // puan değiştirmeler
    public void puanOlustur(String islemTuru, RecyclerAdapterUserMovie.ViewHolder holder, int position)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Movie movie = recMovies.get(position);
        int sayi = Integer.parseInt(holder.textViewPoint.getText().toString());

        movieUserPointForUpdate.setMovieId(parents.get(position));
        movieUserPointForUpdate.setUserEmail(user.getEmail());

        if(islemTuru.equals("pozitif"))
        {
            movieUserPointForUpdate.setMoviePoint(1);
            holder.textViewPoint.setTextColor(context.getResources().getColor(R.color.yeniMavi));
            holder.btn_pozitifPuan.setBackground(context.getDrawable(R.drawable.arti_sonra));
            sayi+=1;
            userVoteEkle(user.getEmail(), movie.getMovieTitle(), "1");
        }
        else
        {
            movieUserPointForUpdate.setMoviePoint(-1);
            holder.textViewPoint.setTextColor(context.getResources().getColor(R.color.yeniKirmizi));
            holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_sonra));
            sayi-=1;
            userVoteEkle(user.getEmail(), movie.getMovieTitle(), "-1");
        }
        holder.textView_oyVerdiniz.setVisibility(View.VISIBLE);
        holder.textViewPoint.setText(String.valueOf(sayi));
        FirebaseDatabase.getInstance().getReference().child("MovieUserPoints").push().setValue(movieUserPointForUpdate);

        //movie güncelliyor.
        movie.setMoviePoint(sayi);
        Task<Void> ref = FirebaseDatabase.getInstance().getReference().child("Movies")
                .child(parents.get(position)).setValue(movie);
        ref.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(context, "Yükleme başarılı", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Yükleme başarısız.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        int gecici_puan = Integer.parseInt(holder.textViewToplamPuan.getText().toString());
        gecici_puan += 1;
        holder.textViewToplamPuan.setText(String.valueOf(gecici_puan));
    }

    public void puanGuncelle(String islemTuru, RecyclerAdapterUserMovie.ViewHolder holder, int position)
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
                        if(movieUserPoint.getMovieId().equals( parents.get(position)) && movieUserPoint.getUserEmail().equals(user.getEmail()))
                        {
                            puanDegistir(islemTuru, holder, position, snapshot.getKey());
                            break;
                        }
                    }
                }
            }
        });

    }

    public void puanDegistir(String islemTuru, RecyclerAdapterUserMovie.ViewHolder holder, int position, String parent)
    {
        int sayi = Integer.parseInt(holder.textViewPoint.getText().toString());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        movieUserPointForUpdate.setMovieId(parents.get(position));
        movieUserPointForUpdate.setUserEmail(user.getEmail());

        if(islemTuru.equals("pozitif"))
        {
            movieUserPointForUpdate.setMoviePoint(1);
            sayi += 2;
            holder.textViewPoint.setTextColor(context.getResources().getColor(R.color.yeniMavi));
            holder.textViewPoint.setText(String.valueOf(sayi));
            holder.btn_pozitifPuan.setBackground(context.getDrawable(R.drawable.arti_sonra));
            holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_baslangic));
            userVoteEkle(user.getEmail(), recMovies.get(position).getMovieTitle(), "1");
        }
        else
        {
            movieUserPointForUpdate.setMoviePoint(-1);
            sayi -= 2;
            holder.textViewPoint.setTextColor(context.getResources().getColor(R.color.yeniKirmizi));
            holder.textViewPoint.setText(String.valueOf(sayi));
            holder.btn_negatifPuan.setBackground(context.getDrawable(R.drawable.eksi_sonra));
            holder.btn_pozitifPuan.setBackground(context.getDrawable(R.drawable.likerenk));
            userVoteEkle(user.getEmail(), recMovies.get(position).getMovieTitle(), "-1");
        }
        movieUserPointForUpdate.setMoviePoint(sayi);

        FirebaseDatabase.getInstance().getReference().child("MovieUserPoints")
                .child(parent).setValue(movieUserPointForUpdate);

        FirebaseDatabase.getInstance().getReference().child("Movies")
                .child(parents.get(position)).child("moviePoint").setValue(sayi);

        Toast.makeText(context, "Puanınız alındı!1", Toast.LENGTH_SHORT).show();

    }

    //puan doldurma kısmı
    public void puanlariGetir(int position, RecyclerAdapterUserMovie.ViewHolder holder)
    {
        puan = 0;
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
                        if(movieUserPoint.getMovieId().equals(parents.get(position)))
                        {
                            puan++;
                        }
                    }
                    holder.textViewToplamPuan.setText(String.valueOf(puan));
                }
            }
        });

    }

    public void userVoteEkle(String userEmail, String movieTitle, String puan)
    {
        String[] email_splited = userEmail.split("@", 2);
        String birlesim = email_splited[0].concat(email_splited[1]);
        String[] birlesim_splited = birlesim.split("\\.", 2);
        String yeni_birlesim = birlesim_splited[0].concat(birlesim_splited[1]);

        Date date = new Date();
        Oy oy = new Oy();
        oy.setMovieTitle(movieTitle);
        oy.setOyTarihi(date);
        oy.setPuan(puan);

        Log.d("birlesimlog", yeni_birlesim);
        FirebaseDatabase.getInstance().getReference().child("UserVotes").child(yeni_birlesim).push().setValue(oy);
    }

}
