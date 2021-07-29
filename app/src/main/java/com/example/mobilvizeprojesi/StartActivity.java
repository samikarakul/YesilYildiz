package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private Button btn_start_login;
    private Button btn_start_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_start_login = findViewById(R.id.btn_start_login);
        btn_start_register = findViewById(R.id.btn_start_register);

        btn_start_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();;
            }
        });

        btn_start_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            adminSorgusu(user.getEmail());
        }
    }

    public void adminSorgusu(String email)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        User usr = snapshot.getValue(User.class);
                        if(email.equals(usr.getEmail()))
                        {
                            if(usr.getRole().equals("admin")) startActivity(new Intent(StartActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            else startActivity(new Intent(StartActivity.this, UserMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            break;
                        }
                    }
                }
            }
        });
    }
}