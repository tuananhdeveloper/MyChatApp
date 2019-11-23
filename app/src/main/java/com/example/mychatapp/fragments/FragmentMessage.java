package com.example.mychatapp.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.BackgroundChatManager;
import com.example.mychatapp.Chat;
import com.example.mychatapp.MessageService;
import com.example.mychatapp.Model.Message;
import com.example.mychatapp.R;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.adapters.MessageAdapter;
import com.example.mychatapp.adapters.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class FragmentMessage extends Fragment {

    private User user;
    private Context context;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private EmojiconEditText edtMsg;
    private ImageView send;
    private String id_currentUser;
    private RecyclerView recyclerView;
    private String key;
    private MessageAdapter mAdapter;
    private String name;
    private ImageView emojButton;

    private String nameRceiver;
    private String idReceiver;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Bitmap bitmap;

    private String imageUrl;
    SharedPreferences sharedPreferences;

    private RelativeLayout layout;
    public FragmentMessage(User user, Context context) {
        this.user = user;
        this.context = context;
        this.imageUrl = user.getImageUrl();
    }

    public FragmentMessage(Context context, String idReceiver, String nameRceiver, String imageUrl) {
        this.context = context;
        this.user = new User();
        this.user.setName(nameRceiver);
        this.user.setId(idReceiver);
        this.imageUrl = imageUrl;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = auth.getCurrentUser();
        id_currentUser = firebaseUser.getUid();
        name = firebaseUser.getDisplayName();
        sharedPreferences = getActivity().getSharedPreferences("notify", Context.MODE_PRIVATE);

        storage = FirebaseStorage.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(MessageService.ACTION_STOP_FOREGROUND_SERVICE);
        ((AppCompatActivity)context).startService(intent);

        View view = inflater.inflate(R.layout.layout_message, container, false);
        emojButton = view.findViewById(R.id.emoj_button);
        layout = view.findViewById(R.id.background_message);
        edtMsg = view.findViewById(R.id.edt_message);
        send = view.findViewById(R.id.send);
        recyclerView = view.findViewById(R.id.my_recycler_view);

        EmojIconActions emojIconActions = new EmojIconActions(getContext(), layout, edtMsg, emojButton, "#F06292", "#FCE4EC", "#E8F5E9");
        emojIconActions.ShowEmojIcon();
        //background
        BackgroundChatManager backgroundChatManager = BackgroundChatManager.getInstance();
        backgroundChatManager.changeBackground(getActivity(), layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        readMessage(id_currentUser, user.getId());
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtMsg.getText().toString().equals("")){
                    sendMessage(id_currentUser, name, user.getId(), edtMsg.getText().toString());
                }
                edtMsg.setText("");
            }
        });
        ((AppCompatActivity)context).getSupportActionBar().setTitle(user.getName());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(sharedPreferences.getInt("state", -1) != 0){
            ((AppCompatActivity)context).getSupportActionBar().setTitle(R.string.app_name);
            Intent intent = new Intent(context, MessageService.class);
            intent.setAction(MessageService.ACTION_START_FOREGROUND_SERVICE);
            ((AppCompatActivity)context).startService(intent);
        }
    }

    public void sendMessage(String idSender, String name, String idReceiver, String msg){
        reference = FirebaseDatabase.getInstance().getReference("Messages");
        Map<String, Object> map = new HashMap<>();
        map.put("idSender", idSender);
        map.put("nameSender", name);
        map.put("idReceiver", idReceiver);
        map.put("msg", msg);
        map.put("isNotified", false);
        if(firebaseUser.getPhotoUrl() != null){
            map.put("imageUrl", firebaseUser.getPhotoUrl().toString());
        }
        else{
            map.put("imageUrl", "default");
        }
        reference.push().updateChildren(map);
    }

    public void readMessage(final String idSender, final String idReceiver){
        final List<Message> list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message msg = snapshot.getValue(Message.class);
                    if(idSender.equals(msg.getIdSender()) && idReceiver.equals(msg.getIdReceiver())||
                        idSender.equals(msg.getIdReceiver()) && idReceiver.equals(msg.getIdSender())
                    ){
                        list.add(msg);
                    }
                }

                downloadBitmap(list);
                mAdapter = new MessageAdapter(list, bitmap, context);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void downloadBitmap(final List<Message> list){
        if(!imageUrl.equals("default")){
            storageReference = storage.getReferenceFromUrl(imageUrl);
            String tmp[] = imageUrl.split("\\?");
            String extension = tmp[0].charAt(tmp[0].length()-3) + "" + tmp[0].charAt(tmp[0].length() - 2) + "" + tmp[0].charAt(tmp[0].length() - 1);
            try {
                final File localFile = File.createTempFile("images", extension);
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Download", "success");
                        bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        mAdapter = new MessageAdapter(list, bitmap, context);
                        recyclerView.setAdapter(mAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Download", "Fail");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
