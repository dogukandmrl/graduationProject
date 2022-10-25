package com.dogukandemirel.workshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private FirebaseAuth mAuth;
    private EditText editTextFullname, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullname = (EditText) findViewById(R.id.fullName);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }
    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullname = editTextFullname.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        if(fullname.isEmpty()){
            editTextFullname.setError("Bu kısım boş bırakılamaz");
            editTextFullname.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editTextAge.setError("Yaş Kısmı Boş Bırakılamaz.");
            editTextAge.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email alanı boş bırakılamaz.");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Geçerli bir Email Adresi giriniz!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Şifre alanı boş bırakılamaz.");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length()<8){
            editTextPassword.setError("Şifre en az 8 karakterli olmalıdır");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          User user = new User(fullname,age,email);
                          FirebaseDatabase.getInstance().getReference("Users")
                                  .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                  .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      Toast.makeText(RegisterUser.this,"Başarıyla kayıt oldunuz",Toast.LENGTH_LONG).show();
                                      progressBar.setVisibility((View.GONE));
                                  }else{
                                      Toast.makeText(RegisterUser.this,"Kayıt başarısız lütfen tekrar deneyiniz",Toast.LENGTH_LONG).show();
                                      progressBar.setVisibility(View.GONE);
                                  }
                              }
                          });
                      }else{
                          Toast.makeText(RegisterUser.this,"Kayıt başarısız lütfen tekrar deneyiniz",Toast.LENGTH_LONG).show();
                          progressBar.setVisibility(View.GONE);
                      }
                    }
                });
    }
}