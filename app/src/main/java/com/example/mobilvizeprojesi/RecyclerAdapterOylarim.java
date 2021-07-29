package com.example.mobilvizeprojesi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapterOylarim extends RecyclerView.Adapter<RecyclerAdapterOylarim.ViewHolder>{
    List<Oy> recOylar = new ArrayList<Oy>();
    Context context;
    List<View> views = new ArrayList<View>();
    View mView;

    public RecyclerAdapterOylarim(Context context, List<Oy> recOylar) {
        this.recOylar = recOylar;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapterOylarim.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.oylarim_design, parent, false);
        RecyclerAdapterOylarim.ViewHolder viewHolder = new RecyclerAdapterOylarim.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterOylarim.ViewHolder holder, int position) {
        holder.tV_movieTitle_oylarim.setText(recOylar.get(position).getMovieTitle());
        holder.tV_puan_oylarim.setText(recOylar.get(position).getPuan());
        try {
            holder.tV_oyTarihi_oylarim.setText(dateParse(recOylar.get(position).getOyTarihi()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(recOylar.get(position).getPuan().equals("-1"))
        {
            if(mView != null) mView.setBackground(context.getDrawable(R.drawable.buton_tasarim_2));
        }

    }

    @Override
    public int getItemCount() {
        return recOylar.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tV_movieTitle_oylarim;
        TextView tV_puan_oylarim;
        TextView tV_oyTarihi_oylarim;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tV_movieTitle_oylarim = itemView.findViewById(R.id.tV_movieTitle_oylarim);
            tV_puan_oylarim = itemView.findViewById(R.id.tV_puan_oylarim);
            tV_oyTarihi_oylarim = itemView.findViewById(R.id.tV_oyTarihi_oylarim);

            mView = itemView;

        }
    }

    public String dateParse(Date unparsedDate) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(unparsedDate);
        String[] splittedDateStr = formattedDate.split("-");

        return splittedDateStr[2] + "/" + splittedDateStr[1] + "/" + splittedDateStr[0];
    }
}
