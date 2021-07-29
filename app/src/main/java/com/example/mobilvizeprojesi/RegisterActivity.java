package com.example.mobilvizeprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private Button btn_register_register;
    private EditText eT_register_email;
    private EditText eT_register_username;
    private EditText eT_register_password;
    private EditText eT_register_passwordTekrar;

    private FirebaseAuth auth;

    String register_email;
    String register_username;
    String register_password;
    String register_passwordTekrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register_register = findViewById(R.id.btn_register_register);
        eT_register_email = findViewById(R.id.eT_register_email);
        eT_register_username = findViewById(R.id.eT_register_username);
        eT_register_password = findViewById(R.id.eT_register_password);
        eT_register_passwordTekrar = findViewById(R.id.eT_register_passwordTekrar);

        auth = FirebaseAuth.getInstance();

        btn_register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_email = eT_register_email.getText().toString();
                register_username = eT_register_username.getText().toString();
                register_password = eT_register_password.getText().toString();
                register_passwordTekrar = eT_register_passwordTekrar.getText().toString();

                if(bosDegerKontrol(register_email, register_username, register_password, register_passwordTekrar))
                {
                    if(sifrelerAyniMi(register_password, register_passwordTekrar))
                    {
                        if(sifreUzunluguUygunMu(register_password, register_passwordTekrar))
                        {
                            User user = new User();
                            user.setUsername(register_username);
                            user.setEmail(register_email);
                            user.setRole("normal");

                            userVarMi(user, register_password);
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Şifre en az 8 karakter olmalı!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Şifreler Uyuşmuyor!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Boş Değer Bırakmayınız!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean sifreUzunluguUygunMu(String password, String passwordTekrar)
    {
        if(password.length() >= 8 && passwordTekrar.length() >= 8) return true;
        return false;
    }
    public boolean sifrelerAyniMi(String password, String passwordTekrar)
    {
        if(password.equals(passwordTekrar)) return true;
        return false;
    }

    public boolean bosDegerKontrol(String email, String username, String password, String passwordTekrar)
    {
        if(email.equals("") || username.equals("") || password.equals("") || passwordTekrar.equals(""))
        {
            return false;
        }
        return true;
    }

    private  void userVarMi(User user, String password)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean userSorgu = false;
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap: snapshot.getChildren())
                    {
                        User u = snap.getValue(User.class);
                        if(user.getUsername().equals(u.getUsername()))
                        {
                            userSorgu = true;
                            break;
                        }
                    }
                    if(!userSorgu)
                    {
                        registerUser(user, password);
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Kullanıcı Adı Alınmış.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void registerUser(User user, String password) {
        auth.createUserWithEmailAndPassword(user.getEmail(),password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    saveUsersInfos(user);
                    Toast.makeText(RegisterActivity.this, "Kayıt başarılı", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, "Başarısız", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUsersInfos(User user)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").push().setValue(user);
    }
}