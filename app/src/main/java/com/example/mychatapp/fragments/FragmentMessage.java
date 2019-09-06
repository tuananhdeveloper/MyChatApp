package com.example.mychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.Model.Message;
import com.example.mychatapp.R;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMessage extends Fragment {

    private User user;
    private Context context;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private EditText edtMsg;
    private ImageView send;
    private String id_currentUser;
    private RecyclerView recyclerView;
    private String key;
    private MessageAdapter mAdapter;
    public FragmentMessage(User user, Context context) {
        this.user = user;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = auth.getCurrentUser();
        id_currentUser = firebaseUser.getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_message, container, false);
        edtMsg = view.findViewById(R.id.edt_message);
        send = view.findViewById(R.id.send);
        recyclerView = view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        readMessage(id_currentUser, user.getId());
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtMsg.getText().toString().equals("")){
                    sendMessage(id_currentUser, user.getId(), edtMsg.getText().toString());
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
        ((AppCompatActivity)context).getSupportActionBar().setTitle(R.string.app_name);
    }


    public void sendMessage(String idSender, String idReceiver, String msg){
        reference = FirebaseDatabase.getInstance().getReference("Messages");
        Map<String, Object> map = new HashMap<>();
        map.put("idSender", idSender);
        map.put("idReceiver", idReceiver);
        map.put("msg", msg);
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

                mAdapter = new MessageAdapter(list, context);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
