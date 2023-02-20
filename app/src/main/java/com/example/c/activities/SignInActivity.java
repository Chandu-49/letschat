package com.example.c.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.c.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setlistners();
    }
    private  void setlistners(){
        binding.textCreateNewAccount.setOnClickListener( v -> {
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
        });

    }

}