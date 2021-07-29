package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login_login;
    private EditText eT_login_email;
    private EditText eT_login_password;

    private FirebaseAuth auth;

    private boolean sorgu=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login_login = findViewById(R.id.btn_login_login);
        eT_login_email = findViewById(R.id.eT_login_email);
        eT_login_password = findViewById(R.id.eT_login_password);

        auth = FirebaseAuth.getInstance();

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login_email = eT_login_email.getText().toString();
                String login_password = eT_login_password.getText().toString();

                if(bosSorgu(login_email, login_password))
                {
                    adminSorgusu(login_email, login_password);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Boş Değer Girilemez.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void adminSorgusu(String email, String password)
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
                            if(usr.getRole().equals("admin")) loginAdmin(email,password);
                            else loginUser(email, password);
                            break;
                        }
                    }
                }
            }
        });
    }

    public boolean bosSorgu(String email, String password)
    {
        if(email.equals("") || password.equals("")) return false;
        return true;
    }

    private void loginUser(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Başarılı giriş", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                finish();
            }
        });
    }

    private void loginAdmin(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Başarılı giriş", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

}