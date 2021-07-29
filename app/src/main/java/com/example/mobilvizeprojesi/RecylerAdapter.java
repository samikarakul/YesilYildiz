package com.example.mobilvizeprojesi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

//    String data[];
    List<Movie> recMovies = new ArrayList<Movie>();
    List<String> parents = new ArrayList<String>();
    Context context;
    
    public RecyclerAdapter(Context context, List<Movie> recMovies, List<String> parents) {
        this.recMovies = recMovies;
        this.context = context;
        this.parents = parents;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(recMovies.get(position).getMovieTitle());
        holder.textViewPoint.setText(String.valueOf(recMovies.get(position).getMoviePoint()));
        new DownloadImageTask((ImageView) holder.imageView).execute(recMovies.get(position).getMoviePhoto());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, MovieUpdateActivity.class);
                myIntent.putExtra("movie", recMovies.get(position));
                myIntent.putExtra("movieId", parents.get(position));
                context.startActivity(myIntent);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movieImage);
            textView = itemView.findViewById(R.id.tV_custome_movie);
            textViewPoint = itemView.findViewById(R.id.textView9);

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
}

