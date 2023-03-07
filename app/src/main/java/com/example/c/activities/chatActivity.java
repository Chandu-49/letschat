package com.example.c.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.c.R;
import com.example.c.adaptor.ChatAdaptor;
import com.example.c.databinding.ActivityChatBinding;
import com.example.c.databinding.ActivityUsersBinding;
import com.example.c.models.ChatMessage;
import com.example.c.models.User;
import com.example.c.utilites.Constants;
import com.example.c.utilites.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class chatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User reciverUser;
    private List<ChatMessage>chatMessages;
    private ChatAdaptor chatAdaptor;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadreciverdetails();
        setListners();
        init();
        listenMessages();
    }
    private void init(){
        preferenceManager=new PreferenceManager(getApplicationContext());
        chatMessages=new ArrayList<>();
        chatAdaptor=new ChatAdaptor(
                chatMessages,getBitmapFromEncodedImage(reciverUser.image)
                ,preferenceManager.getString(Constants.KEY_USER)

        );
        binding.chatRecyclerView.setAdapter(chatAdaptor);
        database=FirebaseFirestore.getInstance();
    }



    private void sendMessage(){
        HashMap<String,Object>message=new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER));
        message.put(Constants.KEY_RECEIVER_ID,reciverUser.id);
        message.put(Constants.KEY_MESSAGE,binding.imputMessage.getText().toString());
        message.put(Constants.KEY_TIME_STAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.imputMessage.setText(null);
        // This null is used to clear the text after sending the message in the input text

    }
   private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,reciverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,reciverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER))
                .addSnapshotListener(eventListener);
   }
    private final EventListener<QuerySnapshot>eventListener=(value,error)->{
        if(error!=null){
            return;
        }
        if (value!=null){
            int count=chatMessages.size();

            for (DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType()== DocumentChange.Type.ADDED){
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message=documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime=getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIME_STAMP));
                    chatMessage.dateObject=documentChange.getDocument().getDate(Constants.KEY_TIME_STAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1,obj2)-> obj1.dateObject.compareTo(obj2.dateObject ));
            if(count==0){
                chatAdaptor.notifyDataSetChanged();
            }
            else{
               chatAdaptor.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
               binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.GONE);
        }
    };
    private Bitmap getBitmapFromEncodedImage(String encodedImage){
        byte[]bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    private void loadreciverdetails(){
        reciverUser=(User) getIntent().getSerializableExtra(Constants.KEY_UUSERR);
       binding.textName.setText(reciverUser.name);

    }
    private void setListners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }
}