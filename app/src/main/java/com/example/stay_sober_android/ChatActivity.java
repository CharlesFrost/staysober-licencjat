package com.example.stay_sober_android;

import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.stay_sober_android.models.ChatMessage;
import com.example.stay_sober_android.models.ChatModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference myDatabase;
    private DatabaseReference myChatDb;

    private TextInputEditText messageContent;
    private Button sendBtn;
    private ListView messagesListView;
    private FirebaseAuth mAuth;
    private FirebaseListAdapter<ChatMessage> adapter;
    private Button sendImageBtn;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myDatabase = FirebaseDatabase.getInstance().getReference("chats");

        messageContent = findViewById(R.id.private_chat_input);
        sendBtn = findViewById(R.id.private_chat_send_bt);
        sendImageBtn = findViewById(R.id.private_chat_imageBtn);
        messagesListView = findViewById(R.id.chat_chat_listview);
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        final String interlocutor = getIntent().getStringExtra("interlocutor");
        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = chatSnapshot.getValue(ChatModel.class);
                    if (chatModel.getSecondPerson() != null) {


                    if (chatModel.getFirstPerson().equals(mAuth.getUid()) && chatModel.getSecondPerson().equals(interlocutor)
                            || chatModel.getFirstPerson().equals(interlocutor) && chatModel.getSecondPerson().equals(mAuth.getUid())) {
                        myChatDb = myDatabase.child(chatModel.getChatId());
                        System.out.println(myChatDb.getKey());
                        displayMessage();
                    }
                }  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), 1);
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(messageContent.getText().toString());
            }
        });
    }

    public void displayMessage() {
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item, myChatDb) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText, messageUser, messageTime;

                ImageView imageView;
                imageView = v.findViewById(R.id.message_image);
                messageText = v.findViewById(R.id.message_text);
                messageText.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);
                if (model.getMessageText().contains("firebasestorage")) {
//                    messageText.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(model.getMessageText()).into(imageView);

                    messagesListView.setSelection(adapter.getCount()-1);
                } else {
                    messageText.setVisibility(View.VISIBLE);
                    messageText.setText(model.getMessageText());
                }
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
            }
        };
        messagesListView.setAdapter(adapter);

    }

    public void sendMessage(String content) {
        if (!content.isEmpty()) {
            myChatDb.push().setValue(new ChatMessage(content, mAuth.getCurrentUser().getDisplayName()));
            messageContent.setText("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            DatabaseReference user_message_push = myDatabase.push();
            final String push_id = user_message_push.getKey();

            StorageReference filePath = storageReference.child("message_images").child(push_id + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                myChatDb.push().setValue(new ChatMessage(task.getResult().buildUpon().toString(), mAuth.getCurrentUser().getDisplayName()));

                            }
                        });

                    }
                }
            });
        }
    }
}




