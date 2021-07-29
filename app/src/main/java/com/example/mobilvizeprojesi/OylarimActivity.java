package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OylarimActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    RecyclerAdapterOylarim recyclerAdapterOylarim;

    List<Oy> oylarClass = new ArrayList<Oy>();
    List<Oy> oylarClassReverse = new ArrayList<Oy>();

    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oylarim);

        toolbar = (Toolbar) findViewById(R.id.toolbar_oylarim);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view_oylarim);
        navigationView.setNavigationItemSelectedListener(this);

        oylarimCagir();
        oylarClassReverse = Lists.reverse(oylarClass);

        recyclerView = findViewById(R.id.recylerView_oylarim);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapterOylarim = new RecyclerAdapterOylarim(this, oylarClassReverse);
        recyclerView.setAdapter(recyclerAdapterOylarim);
    }


    private void oylarimCagir()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String[] email_splited = user.getEmail().split("@", 2);
        String email = email_splited[0].concat(email_splited[1]);
        email_splited = email.split("\\.", 2);
        email = email_splited[0].concat(email_splited[1]);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserVotes").child(email);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren())
                {
                    Oy oy = snap.getValue(Oy.class);
                    oylarClass.add(oy);
                }
                recyclerAdapterOylarim.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();

        if(id == R.id.nav_anasayfa_oylarim)
        {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        }
        else if(id == R.id.hakkimizda_oylarim)
        {
            startActivity(new Intent(this, HakkimizdaActivity.class));
            finish();
        }
        else if(id == R.id.iletisim_oylarim)
        {
            startActivity(new Intent(this, IletisimActivity.class));
            finish();
        }
        return true;
    }
}