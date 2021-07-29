package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class IletisimActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iletisim);

        toolbar = (Toolbar) findViewById(R.id.toolbar_iletisim);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view_iletisim);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();

        if(id == R.id.nav_oylarim)
        {
            Toast.makeText(this, "Oylarım Tıkladı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OylarimActivity.class));
            finish();
        }
        else if(id == R.id.nav_anasayfa)
        {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        }
        else if(id == R.id.hakkimizda_iletisim)
        {
            startActivity(new Intent(this, HakkimizdaActivity.class));
            finish();
        }
        return true;
    }
}