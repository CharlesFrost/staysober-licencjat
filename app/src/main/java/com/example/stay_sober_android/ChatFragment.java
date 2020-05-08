package com.example.stay_sober_android;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.stay_sober_android.models.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.view.Gravity.RIGHT;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private DatabaseReference myDatabase;
    private TextInputEditText messageContent;
    private Button sendBtn;
    private ListView messagesListView;
    private FirebaseAuth mAuth;
    private FirebaseListAdapter<ChatMessage> adapter;
    private Button sendImageBtn;
    private StorageReference storageReference;

    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_chat, container, false);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myDatabase = FirebaseDatabase.getInstance().getReference("public_chat");
        messageContent = getView().findViewById(R.id.public_chat_input);
        sendBtn = getView().findViewById(R.id.public_chat_send_bt);
        sendImageBtn = getView().findViewById(R.id.public_chat_imageBtn);
        mAuth = FirebaseAuth.getInstance();

        messagesListView = getView().findViewById(R.id.public_chat_listview);
        storageReference = FirebaseStorage.getInstance().getReference();
        displayMessage();

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

    public void sendMessage(String content) {
        if (!content.isEmpty()) {
            myDatabase.push().setValue(new ChatMessage(content, mAuth.getCurrentUser().getDisplayName()));
            messageContent.setText("");
        }
    }

    public void displayMessage() {
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_item, myDatabase) {
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
                                myDatabase.push().setValue(new ChatMessage(task.getResult().buildUpon().toString(), mAuth.getCurrentUser().getDisplayName()));

                            }
                        });

                    }
                }
            });
        }
    }
}
