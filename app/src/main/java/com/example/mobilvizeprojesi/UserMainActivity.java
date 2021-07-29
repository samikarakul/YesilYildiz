package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserMainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    RecyclerAdapterUserMovie recyclerAdapter;

    private Button btn_userMain_logout;

    List<Movie> moviesClass = new ArrayList<Movie>();
    List<String> parents = new ArrayList<String>();

    boolean sorgu = true;

    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        btn_userMain_logout = findViewById(R.id.btn_userMain_logout);

        toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view_user);
        navigationView.setNavigationItemSelectedListener(this);

        moviesListeCagir();

        recyclerView = findViewById(R.id.recylerView_userPage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapterUserMovie(this, moviesClass, parents);
        recyclerView.setAdapter(recyclerAdapter);


        btn_userMain_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(UserMainActivity.this, "Çıkış yaptınız.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserMainActivity.this, StartActivity.class));
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(UserMainActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

    }

    private void moviesListeCagir()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Movies");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                for(DataSnapshot snap: snapshot.getChildren())
                {
                    parents.add(snap.getKey());
                    Movie movie = snap.getValue(Movie.class);
                    moviesClass.add(movie);
                }
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();

        if(id == R.id.nav_oylarim)
        {
            Toast.makeText(this, "Oylarıma tıkladı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OylarimActivity.class));
            finish();
        }
        else if(id==R.id.iletisim)
        {
            Toast.makeText(this, "İletişime tıkladı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, IletisimActivity.class));
            finish();
        }
        else if(id==R.id.hakkimizda)
        {
            Toast.makeText(this, "Hakkımızdaya tıkladı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HakkimizdaActivity.class));
            finish();
        }
        return true;
    }
}