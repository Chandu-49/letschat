package com.example.c.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.c.R;
import com.example.c.databinding.ActivityChatBinding;
import com.example.c.databinding.ActivityUsersBinding;
import com.example.c.models.User;
import com.example.c.utilites.Constants;

public class chatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User reciverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadreciverdetails();
        setListners();


    }
    private void loadreciverdetails(){
        reciverUser=(User) getIntent().getSerializableExtra(Constants.KEY_UUSERR);
       binding.textName.setText(reciverUser.name);

    }
    private void setListners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}